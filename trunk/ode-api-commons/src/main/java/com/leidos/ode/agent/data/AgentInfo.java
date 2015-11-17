package com.leidos.ode.agent.data;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author cassadyja
 */
@XmlRootElement
public class AgentInfo  implements Serializable{
    private int registrationId;
    private String messageType;
    private String region;
    private String agentId;

    /**
     * @return the registrationId
     */
    public int getRegistrationId() {
        return registrationId;
    }

    /**
     * @param registrationId the registrationId to set
     */
    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    /**
     * @return the messageType
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * @param messageType the messageType to set
     */
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @param region the region to set
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * @return the agentId
     */
    public String getAgentId() {
        return agentId;
    }

    /**
     * @param agentId the agentId to set
     */
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }


}
