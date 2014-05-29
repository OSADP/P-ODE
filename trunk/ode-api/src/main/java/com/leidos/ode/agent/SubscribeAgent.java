package com.leidos.ode.agent;

import com.leidos.ode.core.data.ODERegistrationResponse;

/**
 * This agent is used when a collector will only subscribe to data feeds.
 * @author cassadyja
 *
 */
public class SubscribeAgent extends ODEAgent {

	@Override
	public void startUp() {
		ODERegistrationResponse regResponse = registration.register(regInfo);
	}

}