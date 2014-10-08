package com.leidos.ode.agent.registration;

import com.leidos.ode.core.data.ODERegistrationResponse;
import com.leidos.ode.agent.data.RegistrationInformation;

public interface ODERegistration {

    public ODERegistrationResponse register(RegistrationInformation regInfo);

}
