package com.leidos.ode.agent;

import com.leidos.ode.agent.data.AgentInfo;
import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.datatarget.ODEDataTarget;
import com.leidos.ode.agent.datatarget.ODEDataTarget.DataTargetException;
import com.leidos.ode.agent.parser.ODEDataParser;
import com.leidos.ode.agent.parser.ODEDataParser.ODEParseException;
import com.leidos.ode.agent.registration.ODERegistration;
import com.leidos.ode.agent.sanitizer.ODESanitizer;
import com.leidos.ode.agent.sanitizer.ODESanitizer.ODESanitizerException;
import com.leidos.ode.core.data.ODERegistrationResponse;
import com.leidos.ode.core.registration.RegistrationInformation;
import com.leidos.ode.logging.ODELogger;
import com.leidos.ode.util.SHAHasher;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class ODEAgent {

    private final String TAG = getClass().getSimpleName();
    private final Logger logger = Logger.getLogger(TAG);
    private final Byte mutex = new Byte("1");
    private int threadCount = 0;

    protected AgentInfo agentInfo;
    protected ODERegistration registration;
    protected ODEDataParser parser;
    protected ODESanitizer sanitizer;
    protected ODEDataTarget dataTarget;
    protected RegistrationInformation registrationInformation;
    @Autowired
    private ODELogger odeLogger;

    public abstract void startUp() throws DataTargetException;

    public void processMessage(byte[] messageBytes) {
        MessageProcessor mp = new MessageProcessor(messageBytes);
        new Thread(mp).start();
        increaseCount();
    }

    protected void createAgentInfo(ODERegistrationResponse regResponse) {
        AgentInfo info = new AgentInfo();
        info.setAgentId(regResponse.getAgentId());
        info.setMessageType(regResponse.getMessageType());
        info.setRegion(regResponse.getRegion());
        info.setRegistrationId(regResponse.getRegistrationId());
        setAgentInfo(info);
    }

    private void increaseCount() {
        synchronized (mutex) {
            threadCount++;
        }
    }

    private void decreaseCount() {
        synchronized (mutex) {
            threadCount--;
        }
    }

    protected int getThreadCount() {
        return threadCount;
    }

    public AgentInfo getAgentInfo() {
        return agentInfo;
    }

    public void setAgentInfo(AgentInfo agentInfo) {
        this.agentInfo = agentInfo;
    }

    public ODERegistration getRegistration() {
        return registration;
    }

    public void setRegistration(ODERegistration registration) {
        this.registration = registration;
    }

    public ODEDataParser getParser() {
        return parser;
    }

    public void setParser(ODEDataParser parser) {
        this.parser = parser;
    }

    public ODESanitizer getSanitizer() {
        return sanitizer;
    }

    public void setSanitizer(ODESanitizer sanitizer) {
        this.sanitizer = sanitizer;
    }

    public ODEDataTarget getDataTarget() {
        return dataTarget;
    }

    public void setDataTarget(ODEDataTarget dataTarget) {
        this.dataTarget = dataTarget;
    }

    public RegistrationInformation getRegistrationInformation() {
        return registrationInformation;
    }

    public void setRegistrationInformation(RegistrationInformation registrationInformation) {
        this.registrationInformation = registrationInformation;
    }

    private ODELogger getOdeLogger() {
        return odeLogger;
    }

    protected Logger getLogger(){
        return logger;
    }

    private class MessageProcessor implements Runnable {

        private byte[] messageBytes;

        protected MessageProcessor(byte[] message) {
            messageBytes = message;
        }

        public void run() {
            try {
                String messageId = SHAHasher.sha256Hash(messageBytes);

                //Start log event for parsing message
                getOdeLogger().start(ODELogger.ODEStage.PARSE, messageId);
                ODEAgentMessage odeAgentMessage = getParser().parseMessage(messageBytes);
                //Finish log event for parsing message
                getOdeLogger().finish();

                odeAgentMessage.setMessageId(messageId);

                getOdeLogger().start(ODELogger.ODEStage.SANITIZE, messageId);
                odeAgentMessage = getSanitizer().sanitizeMessage(odeAgentMessage);
                getOdeLogger().finish();

                odeAgentMessage.setAgentInfo(getAgentInfo());

                getOdeLogger().start(ODELogger.ODEStage.SEND, messageId);
                getDataTarget().sendMessage(odeAgentMessage);
                getOdeLogger().finish();

                ODEAgent.this.decreaseCount();
            } catch (ODEParseException e) {
                getLogger().error(e.getLocalizedMessage());
            } catch (ODESanitizerException e) {
                getLogger().error(e.getLocalizedMessage());
            } catch (DataTargetException e) {
                getLogger().error(e.getLocalizedMessage());
            }
        }
    }
}
