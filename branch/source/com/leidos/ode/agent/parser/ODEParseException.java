package com.leidos.ode.agent.parser;

public class ODEParseException extends Exception {
	private static final long serialVersionUID = 1L;

	public ODEParseException(){
		super();
	}
	
	public ODEParseException(String msg){
		super(msg);
	}
	
	public ODEParseException(String msg, Throwable t){
		super(msg,t);
	}
	
}
