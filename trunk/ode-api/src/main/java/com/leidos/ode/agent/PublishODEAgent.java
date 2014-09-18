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
        ODERegistrationResponse regResponse = registration.register(registrationInformation);
        getLogger().debug("Registration response" + (regResponse != null ? "is not null." : "is null!"));
        dataTarget.configure(regResponse);
        createAgentInfo(regResponse);
    }
}
