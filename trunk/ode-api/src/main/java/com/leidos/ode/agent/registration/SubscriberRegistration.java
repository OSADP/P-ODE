package com.leidos.ode.agent.registration;

import com.leidos.ode.core.data.ODERegistrationResponse;
import com.leidos.ode.core.registration.RegistrationInformation;

public class SubscriberRegistration extends AbstractODERegistration {

    private String returnAddress;
    private String returnPort;

    @Override
    public ODERegistrationResponse register(RegistrationInformation regInfo) {
        return null;
    }

    public String getReturnAddress() {
        return returnAddress;
    }

    public void setReturnAddress(String returnAddress) {
        this.returnAddress = returnAddress;
    }

    public String getReturnPort() {
        return returnPort;
    }

    public void setReturnPort(String returnPort) {
        this.returnPort = returnPort;
    }

}
