package com.leidos.ode.agent;

import com.leidos.ode.agent.data.AgentInfo;
import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.datatarget.DataTargetException;
import com.leidos.ode.agent.datatarget.ODEDataTarget;
import com.leidos.ode.agent.parser.ODEDataParser;
import com.leidos.ode.agent.parser.ODEParseException;
import com.leidos.ode.agent.registration.ODERegistration;
import com.leidos.ode.agent.sanitizer.ODESanitizer;
import com.leidos.ode.agent.sanitizer.ODESanitizerException;
import com.leidos.ode.core.data.ODERegistrationResponse;
import com.leidos.ode.core.registration.RegistrationInformation;
import com.leidos.ode.logging.ODELogger;
import com.leidos.ode.util.SHAHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public abstract class ODEAgent {

    private final String TAG = getClass().getSimpleName();
    private final Logger logger = Logger.getLogger(TAG);

    protected ODERegistration registration;
    protected ODEDataParser parser;
    protected ODESanitizer sanitizer;
    protected ODEDataTarget dataTarget;
    protected RegistrationInformation regInfo;
    protected AgentInfo agentInfo;

    @Autowired
    private ODELogger odeLogger;

    private int threadCount = 0;
    private final Byte mutex = new Byte("1");

    public abstract void startUp() throws DataTargetException;

    /**
     * @return the agentInfo
     */
    public AgentInfo getAgentInfo() {
        return agentInfo;
    }

    /**
     * @param agentInfo the agentInfo to set
     */
    public void setAgentInfo(AgentInfo agentInfo) {
        this.agentInfo = agentInfo;
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
                ODEAgentMessage parsedMessage = parser.parseMessage(messageBytes);
                //Finish log event for parsing message
                getOdeLogger().finish();

                parsedMessage.setMessageId(messageId);

                getOdeLogger().start(ODELogger.ODEStage.SANITIZE, messageId);
                parsedMessage = sanitizer.sanitizeMessage(parsedMessage);
                getOdeLogger().finish();

                parsedMessage.setAgentInfo(agentInfo);

                getOdeLogger().start(ODELogger.ODEStage.SEND, messageId);
                dataTarget.sendMessage(parsedMessage);
                getOdeLogger().finish();

                ODEAgent.this.decreaseCount();
            } catch (ODEParseException e) {
                //TODO: log message and throw to collector
                e.printStackTrace();
            } catch (ODESanitizerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (DataTargetException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }

    }

    public void processMessage(byte[] messageBytes) {
        MessageProcessor mp = new MessageProcessor(messageBytes);
        new Thread(mp).start();
        increaseCount();
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


    protected void createAgentInfo(ODERegistrationResponse regResponse) {
        AgentInfo info = new AgentInfo();
        info.setAgentId(regResponse.getAgentId());
        info.setMessageType(regResponse.getMessageType());
        info.setRegion(regResponse.getRegion());
        info.setRegistrationId(regResponse.getRegistrationId());
        setAgentInfo(info);
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

    public RegistrationInformation getRegInfo() {
        return regInfo;
    }

    public void setRegInfo(RegistrationInformation regInfo) {
        this.regInfo = regInfo;
    }

    private ODELogger getOdeLogger() {
        return odeLogger;
    }
}
