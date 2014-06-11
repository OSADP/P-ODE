package com.leidos.ode.agent.data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ODEAgentMessage {
    private AgentInfo agentInfo;

    private byte[] messagePayload;
    private Object formattedMessage;

    public byte[] getMessagePayload() {
            return messagePayload;
    }

    public void setMessagePayload(byte[] messagePayload) {
            this.messagePayload = messagePayload;
    }

    public Object getFormattedMessage() {
            return formattedMessage;
    }

    public void setFormattedMessage(Object formattedMessage) {
            this.formattedMessage = formattedMessage;
    }

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

        
}
