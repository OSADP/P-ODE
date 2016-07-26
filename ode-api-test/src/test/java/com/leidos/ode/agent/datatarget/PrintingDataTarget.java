/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.registration.RegistrationResponse;
import com.leidos.ode.registration.response.ODERegistrationResponse;

/**
 * @author cassadyja
 */
public class PrintingDataTarget implements ODEDataTarget {

    public void configure(ODERegistrationResponse odeRegistrationResponse) throws DataTargetException {

    }

    @Override
    public void configure(RegistrationResponse registrationResponse) throws DataTargetException {

    }

    public void sendMessage(ODEAgentMessage message) throws DataTargetException {
        System.out.println("Receieved Message");
        System.out.println(message.getMessageId());
    }

    public void close() {

    }

}
