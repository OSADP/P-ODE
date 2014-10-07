package com.leidos.ode.agent;

import com.leidos.ode.agent.datatarget.ODEDataTarget.DataTargetException;

/**
 * This controllers is used when a collector wants to publish data only.
 *
 * @author cassadyja
 */
public class PublishODEAgent extends BasicODEAgent {

    @Override
    public void startUp() throws DataTargetException {
        super.startUp();
        if (getRegistrationResponse() != null) {
            getDataTarget().configure(getRegistrationResponse());
        }
    }
}
