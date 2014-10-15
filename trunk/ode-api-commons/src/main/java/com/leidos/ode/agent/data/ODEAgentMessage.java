package com.leidos.ode.agent.data;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

@XmlRootElement
public class ODEAgentMessage implements Serializable {
    private AgentInfo agentInfo;

    private Date messageReceivedDate;
    private String messageId;
    private byte[] messagePayload;
    private Object formattedMessage;

    public byte[] getMessagePayload() {
        return messagePayload;
    }

    public ODEAgentMessage setMessagePayload(byte[] messagePayload) {
        this.messagePayload = messagePayload;
        return this;
    }

    public Object getFormattedMessage() {
        return formattedMessage;
    }

    public ODEAgentMessage setFormattedMessage(Object formattedMessage) {
        this.formattedMessage = formattedMessage;
        return this;
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
    public ODEAgentMessage setAgentInfo(AgentInfo agentInfo) {
        this.agentInfo = agentInfo;
        return this;
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
    public ODEAgentMessage setMessageReceivedDate(Date messageReceivedDate) {
        this.messageReceivedDate = messageReceivedDate;
        return this;
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
    public ODEAgentMessage setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }
}
