package com.leidos.ode.agent.parser;

import com.leidos.ode.agent.data.ODEAgentMessage;

public interface ODEDataParser {

	
	
	public ODEAgentMessage parseMessage(byte[] bytes)throws ODEParseException;
	
	
}
