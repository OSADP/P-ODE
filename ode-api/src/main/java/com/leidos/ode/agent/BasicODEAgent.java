package com.leidos.ode.agent;

import com.leidos.ode.agent.data.AgentInfo;
import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.datatarget.ODEDataTarget;
import com.leidos.ode.agent.formatter.ODEMessageFormatter;
import com.leidos.ode.agent.parser.ODEDataParser;
import com.leidos.ode.agent.registration.ODERegistration;
import com.leidos.ode.agent.registration.RegistrationResponse;
import com.leidos.ode.agent.sanitizer.ODESanitizer;
import com.leidos.ode.logging.ODELogger;
import com.leidos.ode.registration.request.ODERegistrationRequest;
import com.leidos.ode.registration.response.ODERegistrationResponse;
import com.leidos.ode.util.SHAHasher;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class BasicODEAgent implements ODEAgent {

    private final String TAG = getClass().getSimpleName();
    private final Logger logger = Logger.getLogger(TAG);
    private final Byte mutex = new Byte("1");
    protected AgentInfo agentInfo;
    protected ODERegistration registration;
    protected ODEDataParser parser;
    protected ODESanitizer sanitizer;
    private ODEMessageFormatter formatter;
    protected ODEDataTarget dataTarget;
    protected ODERegistrationRequest registrationRequest;
    protected RegistrationResponse registrationResponse;
    private int threadCount = 0;
    private ODELogger odeLogger;
    private MessageListener messageListener;

    private boolean dataLoggingEnabled = false;

    @Override
    public void startUp() throws ODEDataTarget.DataTargetException {
        if (getRegistrationRequest() != null) {
            registrationResponse = getRegistration().register(getRegistrationRequest());
            if (getRegistrationResponse() != null) {
//                getRegistrationRequest().setRegistrationId(getRegistrationResponse().getRegistrationId());
//                createAgentInfo(getRegistrationResponse());
                getLogger().debug("Registration succeeded.");
            } else {
                getLogger().error("Registration failed. Registration response was null.");
            }
        } else {
            getLogger().error("Unable to register. Registration request is null.");
        }
    }

    @Override
    public void startUp(MessageListener messageListener) throws ODEDataTarget.DataTargetException {
        this.messageListener = messageListener;
        startUp();
    }

    @Override
    public void processMessage(byte[] messageBytes) {
        MessageProcessor mp = new MessageProcessor(messageListener, messageBytes);
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

    public ODERegistrationRequest getRegistrationRequest() {
        return registrationRequest;
    }

    public void setRegistrationRequest(ODERegistrationRequest registrationRequest) {
        this.registrationRequest = registrationRequest;
    }

    protected RegistrationResponse getRegistrationResponse() {
        return registrationResponse;
    }

    protected int getThreadCount() {
        return threadCount;
    }

    private ODELogger getOdeLogger() {
        return odeLogger;
    }

    public void setOdeLogger(ODELogger odeLogger) {
        this.odeLogger = odeLogger;
    }

    protected Logger getLogger() {
        return logger;
    }

    public void stopAgent() {
        registration.unregister(registrationRequest);
    }

    /**
     * @return the formatter
     */
    public ODEMessageFormatter getFormatter() {
        return formatter;
    }

    /**
     * @param formatter the formatter to set
     */
    public void setFormatter(ODEMessageFormatter formatter) {
        this.formatter = formatter;
    }

    private class MessageProcessor implements Runnable {

        private MessageListener messageListener;
        private byte[] messageBytes;

        protected MessageProcessor(byte[] message) {
            this(null, message);
        }

        protected MessageProcessor(MessageListener messageListener, byte[] message) {
            this.messageListener = messageListener;
            messageBytes = message;
        }

        public void run() {
            try {
                String messageId = SHAHasher.sha256Hash(messageBytes);

                //Start log event for parsing message
                getOdeLogger().start(ODELogger.ODEStage.PARSE, messageId, getRegistrationRequest().getMessageType());
                ODEAgentMessage odeAgentMessage = getParser().parseMessage(messageBytes);
                //Finish log event for parsing message
                getOdeLogger().finish();

                odeAgentMessage.setMessageId(messageId);

                getOdeLogger().start(ODELogger.ODEStage.SANITIZE, messageId, getRegistrationRequest().getMessageType());
                logger.debug("Sanitizing message");
                odeAgentMessage = getSanitizer().sanitizeMessage(odeAgentMessage);
                getOdeLogger().finish();

                odeAgentMessage.setAgentInfo(getAgentInfo());
                logger.debug("Formatting message");
                odeAgentMessage.setPodeMessageList(formatter.formatMessage(odeAgentMessage, getRegistration().getServiceRequest()));
                
                getOdeLogger().start(ODELogger.ODEStage.SEND, messageId, getRegistrationRequest().getMessageType());

                if (dataLoggingEnabled) {
                    ObjectMapper om = new ObjectMapper();
                    try {
                        logger.debug("!!! SENDING MESSAGES !!!:\n" + om.writeValueAsString(odeAgentMessage.getPodeMessageList()));
                    } catch (IOException e) {
                        logger.error("Unable to serialize message to JSON.");
                    }
                }

                getDataTarget().sendMessage(odeAgentMessage);
                getOdeLogger().finish();

                BasicODEAgent.this.decreaseCount();

                notifyListener(odeAgentMessage);

            } catch (ODEDataParser.ODEParseException e) {
                getLogger().error(e.getLocalizedMessage());
            } catch (ODESanitizer.ODESanitizeException e) {
                getLogger().error(e.getLocalizedMessage());
            } catch (ODEDataTarget.DataTargetException e) {
                getLogger().error(e.getLocalizedMessage());
            } catch (ODELogger.ODELoggerException e) {
                getLogger().error(e.getLocalizedMessage());
            }
        }

        private void notifyListener(ODEAgentMessage odeAgentMessage) {
            if (getMessageListener() != null) {
                getMessageListener().onMessageProcessed(odeAgentMessage);
            }
        }

        private MessageListener getMessageListener() {
            return messageListener;
        }
    }
}
