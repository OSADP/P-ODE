package com.leidos.ode.registration.request;


import com.leidos.ode.registration.ODERegistrationInformation;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class ODERegistrationRequest extends ODERegistrationInformation {

    private String dataTypes;
    private String subscriptionType;
    private String subscriptionReceiveAddress;
    private int subscriptionReceivePort;
    private String replayStartDate;
    private String replayEndDate;
    
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

    /**
     * @return the subscriptionType
     */
    public String getSubscriptionType() {
        return subscriptionType;
    }

    /**
     * @param subscriptionType the subscriptionType to set
     */
    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    /**
     * @return the dataTypes
     */
    public String getDataTypes() {
        return dataTypes;
    }

    /**
     * @param dataTypes the dataTypes to set
     */
    public void setDataTypes(String dataTypes) {
        this.dataTypes = dataTypes;
    }

    /**
     * @return the replayStartDate
     */
    public String getReplayStartDate() {
        return replayStartDate;
    }

    /**
     * @param replayStartDate the replayStartDate to set
     */
    public void setReplayStartDate(String replayStartDate) {
        this.replayStartDate = replayStartDate;
    }

    /**
     * @return the replayEndDate
     */
    public String getReplayEndDate() {
        return replayEndDate;
    }

    /**
     * @param replayEndDate the replayEndDate to set
     */
    public void setReplayEndDate(String replayEndDate) {
        this.replayEndDate = replayEndDate;
    }
}
