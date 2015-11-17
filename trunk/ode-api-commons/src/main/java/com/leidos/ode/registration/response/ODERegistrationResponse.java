package com.leidos.ode.registration.response;

import com.leidos.ode.registration.ODERegistrationInformation;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ODERegistrationResponse extends ODERegistrationInformation {

    private String publishWebServiceAddress;
    //Queue/Topic information, messages are placed on here when received.
    private String queueName;
    private String queueConnFact;
    private String queueHostURL;
    private int queueHostPort;
    //Used in subscription
    private String targetAddress;
    private int targetPort;

    public String getPublishWebServiceAddress() {
        return publishWebServiceAddress;
    }

    public void setPublishWebServiceAddress(String publishWebServiceAddress) {
        this.publishWebServiceAddress = publishWebServiceAddress;
    }

    public String getQueueConnFact() {
        return queueConnFact;
    }

    public void setQueueConnFact(String queueConnFact) {
        this.queueConnFact = queueConnFact;
    }

    public int getQueueHostPort() {
        return queueHostPort;
    }

    public void setQueueHostPort(int queueHostPort) {
        this.queueHostPort = queueHostPort;
    }

    public String getQueueHostURL() {
        return queueHostURL;
    }

    public void setQueueHostURL(String queueHostURL) {
        this.queueHostURL = queueHostURL;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
    }

    public int getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(int targetPort) {
        this.targetPort = targetPort;
    }
}
