package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.core.data.ODERegistrationResponse;
import javax.jms.JMSException;
import javax.naming.NamingException;

public interface ODEDataTarget {
	
	
	public void configure(ODERegistrationResponse regInfo)throws DataTargetException;
	
	public void sendMessage(ODEAgentMessage message) throws DataTargetException;
        
        public void close();
	
}
