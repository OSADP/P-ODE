package com.leidos.ode.agent.data;


import com.leidos.ode.data.PodeDataDistribution;
import com.leidos.ode.util.ODEMessageType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@XmlRootElement
public class ODEAgentMessage implements Serializable {
    private AgentInfo agentInfo;

    private Date messageReceivedDate;
    private String messageId;
    private String messagePayloadBase64;
    private byte[] messagePayload;
    private Object formattedMessage;
    private Map<ODEMessageType, List<PodeDataDistribution>> podeMessageList;
    
    
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


    /**
     * @return the messagePayloadBase64
     */
    public String getMessagePayloadBase64() {
        return messagePayloadBase64;
    }

    /**
     * @param messagePayloadBase64 the messagePayloadBase64 to set
     */
    public void setMessagePayloadBase64(String messagePayloadBase64) {
        this.messagePayloadBase64 = messagePayloadBase64;
    }

    /**
     * @return the podeMessageList
     */
    public Map<ODEMessageType, List<PodeDataDistribution>> getPodeMessageList() {
        return podeMessageList;
    }

    /**
     * @param podeMessageList the podeMessageList to set
     */
    public void setPodeMessageList(Map<ODEMessageType, List<PodeDataDistribution>> podeMessageList) {
        this.podeMessageList = podeMessageList;
    }
}
