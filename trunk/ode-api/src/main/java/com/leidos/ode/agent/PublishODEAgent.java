package com.leidos.ode.agent;

import com.leidos.ode.agent.datatarget.ODEDataTarget.DataTargetException;
import com.leidos.ode.core.data.ODERegistrationResponse;

/**
 * This controllers is used when a collector wants to publish data only.
 *
 * @author cassadyja
 */
public class PublishODEAgent extends ODEAgent {

    @Override
    public void startUp() throws DataTargetException {
        ODERegistrationResponse registrationResponse = registration.register(registrationInformation);
        if (registrationResponse != null) {
            dataTarget.configure(registrationResponse);
            createAgentInfo(registrationResponse);
            getLogger().debug("Registration succeeded.");
        } else {
            getLogger().error("Registration failed. Registration response was null.");
        }
    }
}
