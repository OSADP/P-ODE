package com.leidos.ode.agent.sanitizer;

import com.leidos.ode.agent.data.ODEAgentMessage;

public interface ODESanitizer {

	public ODEAgentMessage sanitizeMessage(ODEAgentMessage message) throws ODESanitizerException;
	
	
}
