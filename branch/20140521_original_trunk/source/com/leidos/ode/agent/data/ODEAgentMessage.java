package com.leidos.ode.agent.data;

public class ODEAgentMessage {
	
	
	private byte[] messagePayload;
	private Object formattedMessage;
	
	
	
	public byte[] getMessagePayload() {
		return messagePayload;
	}
	public void setMessagePayload(byte[] messagePayload) {
		this.messagePayload = messagePayload;
	}
	public Object getFormattedMessage() {
		return formattedMessage;
	}
	public void setFormattedMessage(Object formattedMessage) {
		this.formattedMessage = formattedMessage;
	}
	
	
	

}
