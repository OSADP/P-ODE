package com.leidos.ode.agent.registration;

import com.leidos.ode.registration.request.ODERegistrationRequest;
import com.leidos.ode.registration.response.ODERegistrationResponse;

public interface ODERegistration {

    public ODERegistrationResponse register(ODERegistrationRequest registrationRequest);

    public String unregister(ODERegistrationRequest registrationRequest);
}
