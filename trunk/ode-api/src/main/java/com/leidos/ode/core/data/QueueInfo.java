package com.leidos.ode.core.data;

public class QueueInfo {

	private int id;
	private String messageType;
	private String region;
	private String queueName;
	private String queueConnectionFactory;
	private String targetAddress;
	private int targetPort;
	
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

}
