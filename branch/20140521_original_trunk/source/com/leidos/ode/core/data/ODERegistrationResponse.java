package com.leidos.ode.core.data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ODERegistrationResponse {
	
	private int registrationId;
	private String messageType;
	private String region;
	private String agentId;
	private String registrationType;
	private String queueName;
	private String queueConnFact;
	private String queueHostURL;
	private int queueHostPort;
	private String targetAddress;
	private int targetPort;
	
	
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
	public String getAgentId() {
		return agentId;
	}
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	public String getRegistrationType() {
		return registrationType;
	}
	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	public String getQueueConnFact() {
		return queueConnFact;
	}
	public void setQueueConnFact(String queueConnFact) {
		this.queueConnFact = queueConnFact;
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
	public String getQueueHostURL() {
		return queueHostURL;
	}
	public void setQueueHostURL(String queueHostURL) {
		this.queueHostURL = queueHostURL;
	}
	public int getQueueHostPort() {
		return queueHostPort;
	}
	public void setQueueHostPort(int queueHostPort) {
		this.queueHostPort = queueHostPort;
	}
	
	

}
