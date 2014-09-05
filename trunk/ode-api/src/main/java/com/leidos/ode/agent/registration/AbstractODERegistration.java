package com.leidos.ode.agent.registration;

public abstract class AbstractODERegistration implements ODERegistration {

    protected String regServiceAddress;

    public String getRegServiceAddress() {
        return regServiceAddress;
    }

    public void setRegServiceAddress(String regServiceAddress) {
        this.regServiceAddress = regServiceAddress;
    }

}
