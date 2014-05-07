package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.core.data.ODERegistrationResponse;

public interface ODEDataTarget {
	
	public void configure(ODERegistrationResponse regInfo);
	
	public void sendMessage(ODEAgentMessage message);
	
}
