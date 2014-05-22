package com.leidos.ode.agent;

import javax.jms.JMSException;

import com.leidos.ode.core.data.ODERegistrationResponse;
import javax.naming.NamingException;


/**
 * This agent is used when a collector wants to publish data only.
 * @author cassadyja
 *
 */
public class PublishODEAgent extends ODEAgent{

	@Override
	public void startUp() throws JMSException,NamingException{
            ODERegistrationResponse regResponse = performRegistration();
            dataTarget.configure(regResponse);
	}

	
	private ODERegistrationResponse performRegistration() {
            ODERegistrationResponse regResponse = registration.register(regInfo);
            return regResponse;
	}

}
