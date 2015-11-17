package com.leidos.ode.agent.sanitizer;

public class ODESanitizerException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public ODESanitizerException(){
		super();
	}
	
	public ODESanitizerException(String msg){
		super(msg);
	}
	
	public ODESanitizerException(String msg, Throwable t){
		super(msg,t);
	}
	

}
