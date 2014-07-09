package com.leidos.ode.agent.data;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ODEAgentMessage {
    private AgentInfo agentInfo;
    
    private Date messageReceivedDate;
    private String messageId;
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

    /**
     * @return the messageReceivedDate
     */
    public Date getMessageReceivedDate() {
        return messageReceivedDate;
    }

    /**
     * @param messageReceivedDate the messageReceivedDate to set
     */
    public void setMessageReceivedDate(Date messageReceivedDate) {
        this.messageReceivedDate = messageReceivedDate;
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

        
}
