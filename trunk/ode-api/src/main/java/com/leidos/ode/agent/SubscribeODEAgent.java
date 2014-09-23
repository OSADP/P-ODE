package com.leidos.ode.agent;

import com.leidos.ode.core.data.ODERegistrationResponse;

/**
 * This controller is used when a collector will only subscribe to data feeds.
 *
 * @author cassadyja
 */
public class SubscribeODEAgent extends ODEAgent {

    @Override
    public void startUp() {
        ODERegistrationResponse registrationResponse = registration.register(registrationInformation);
        if (registrationResponse != null) {
            createAgentInfo(registrationResponse);
            getLogger().debug("Registration succeeded.");
        } else {
            getLogger().error("Registration failed. Registration response was null.");
        }
    }

}
