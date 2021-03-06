package com.leidos.ode.core.data;

public class QueueInfo {

    private int id;
    private String messageType;
    private String region;
    private String queueName;
    private String queueConnectionFactory;
    private String targetAddress;
    private int targetPort;
    private String wsHost;
    private String wsURL;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueConnectionFactory() {
        return queueConnectionFactory;
    }

    public void setQueueConnectionFactory(String queueConnectionFactory) {
        this.queueConnectionFactory = queueConnectionFactory;
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

    /**
     * @return the wsURL
     */
    public String getWsURL() {
        return wsURL;
    }

    /**
     * @param wsURL the wsURL to set
     */
    public void setWsURL(String wsURL) {
        this.wsURL = wsURL;
    }

    /**
     * @return the wsHost
     */
    public String getWsHost() {
        return wsHost;
    }

    /**
     * @param wsHost the wsHost to set
     */
    public void setWsHost(String wsHost) {
        this.wsHost = wsHost;
    }
    

}
