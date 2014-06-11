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
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class ODEAgent {

    protected ODERegistration registration;
    protected ODEDataParser parser;
    protected ODESanitizer sanitizer;
    protected ODEDataTarget dataTarget;
    protected RegistrationInformation regInfo;
    protected AgentInfo agentInfo;
    
    
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

    private class MessageProcessor implements Runnable{
        
        private byte[] messageBytes;
        
        protected MessageProcessor(byte[] message){
            messageBytes = message;
        }

        public void run() {
            try {
                ODEAgentMessage parsedMessage = parser.parseMessage(messageBytes);
                parsedMessage = sanitizer.sanitizeMessage(parsedMessage);
                parsedMessage.setAgentInfo(agentInfo);
                dataTarget.sendMessage(parsedMessage);
                ODEAgent.this.decreaseCount();
            } catch (ODEParseException e) {
                //TODO: log message and throw to collector
                e.printStackTrace();
            } catch (ODESanitizerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
}           catch (DataTargetException ex) {           
                Logger.getLogger(ODEAgent.class.getName()).log(Level.SEVERE, null, ex);
            }           
        }
        
    }
    
    public void processMessage(byte[] messageBytes) {
        MessageProcessor mp = new MessageProcessor(messageBytes);
        new Thread(mp).start();
        increaseCount();
    }

    private void increaseCount(){
        synchronized(mutex){
            threadCount++;
        }
    }
    
    private void decreaseCount(){
        synchronized(mutex){
            threadCount--;
        }
    }
    
    protected int getThreadCount(){
        return threadCount;
    }
    
    
    protected void createAgentInfo(ODERegistrationResponse regResponse){
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

}
