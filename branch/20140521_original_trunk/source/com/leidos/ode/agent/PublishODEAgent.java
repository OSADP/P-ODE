package com.leidos.ode.agent;

import javax.jms.JMSException;

import com.leidos.ode.core.data.ODERegistrationResponse;


/**
 * This agent is used when a collector wants to publish data only.
 * @author cassadyja
 *
 */
public class PublishODEAgent extends ODEAgent{

	@Override
	public void startUp() {
		ODERegistrationResponse regResponse = performRegistration();
		try {
			dataTarget.configure(regResponse);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private ODERegistrationResponse performRegistration() {
		ODERegistrationResponse regResponse = registration.register(regInfo);
		return regResponse;
	}

}
