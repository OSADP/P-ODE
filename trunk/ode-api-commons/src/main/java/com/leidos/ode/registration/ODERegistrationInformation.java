package com.leidos.ode.registration;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/15/14
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ODERegistrationInformation {

    private int registrationId;
    private String messageType;
    private String region;
    private String registrationType;
    private String agentId;
    private String subscriptionProtocol;
    private Date startDate;
    private Date endDate;
    
    
    
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    
    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(String registrationType) {
        this.registrationType = registrationType;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    /**
     * @return the subscriptionProtocol
     */
    public String getSubscriptionProtocol() {
        return subscriptionProtocol;
    }

    /**
     * @param subscriptionProtocol the subscriptionProtocol to set
     */
    public void setSubscriptionProtocol(String subscriptionProtocol) {
        this.subscriptionProtocol = subscriptionProtocol;
    }
}
