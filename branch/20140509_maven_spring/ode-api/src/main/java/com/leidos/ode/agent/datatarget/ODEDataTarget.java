package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.core.data.ODERegistrationResponse;
import javax.jms.JMSException;

public interface ODEDataTarget {
	
	
	public void configure(ODERegistrationResponse regInfo)throws JMSException;
	
	public void sendMessage(ODEAgentMessage message) throws JMSException;
	
}
