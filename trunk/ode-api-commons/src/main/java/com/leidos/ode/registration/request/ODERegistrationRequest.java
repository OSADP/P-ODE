package com.leidos.ode.registration.request;


import com.leidos.ode.registration.ODERegistrationInformation;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class ODERegistrationRequest extends ODERegistrationInformation {

    private String agentId;
    private Date startDate;
    private Date endDate;
    private String subscriptionReceiveAddress;
    private int subscriptionReceivePort;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getSubscriptionReceiveAddress() {
        return subscriptionReceiveAddress;
    }

    public void setSubscriptionReceiveAddress(String subscriptionReceiveAddress) {
        this.subscriptionReceiveAddress = subscriptionReceiveAddress;
    }

    public int getSubscriptionReceivePort() {
        return subscriptionReceivePort;
    }

    public void setSubscriptionReceivePort(int subscriptionReceivePort) {
        this.subscriptionReceivePort = subscriptionReceivePort;
    }
}
