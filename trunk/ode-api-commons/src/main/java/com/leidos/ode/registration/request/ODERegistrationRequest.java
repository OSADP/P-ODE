package com.leidos.ode.registration.request;


import com.leidos.ode.registration.ODERegistrationInformation;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class ODERegistrationRequest extends ODERegistrationInformation {

    private String dataTypes;
    private String subscriptionType;
    private String subscriptionReceiveAddress;
    private int subscriptionReceivePort;

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
}
