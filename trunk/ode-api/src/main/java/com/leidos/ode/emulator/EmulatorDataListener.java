/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.emulator;

import com.leidos.ode.agent.data.ODEAgentMessage;

/**
 *
 * @author cassadyja
 */
public interface EmulatorDataListener {
    public void dataReceived(String messageType, ODEAgentMessage data);
}
