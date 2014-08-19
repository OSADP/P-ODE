package com.leidos.ode.core.registration;



import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegistrationInformation {
	
	private int id;
	private String messageType;
	private String region;
	private String registrationType;
	private String agentId;
	private Date startDate;
	private Date endDate;
	private String subscriptionReceiveAddress;
	private int subscriptionReceivePort;


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
