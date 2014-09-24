/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.emulator.agent;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.datatarget.ODEDataTarget;
import com.leidos.ode.core.data.ODERegistrationResponse;
import com.leidos.ode.emulator.EmulatorDataListener;

/**
 *
 * @author cassadyja
 */
public class EmulatorDataTarget implements ODEDataTarget {

    private EmulatorDataListener listener;
    private String messageType;
    
    public void configure(ODERegistrationResponse regInfo) throws DataTargetException {
        
    }

    public void sendMessage(ODEAgentMessage message) throws DataTargetException {
        listener.dataReceived(messageType, message);
    }

    public void close() {
        
    }

    /**
     * @return the listener
     */
    public EmulatorDataListener getListener() {
        return listener;
    }

    /**
     * @param listener the listener to set
     */
    public void setListener(EmulatorDataListener listener) {
        this.listener = listener;
    }

    /**
     * @return the messageType
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * @param messageType the messageType to set
     */
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    
    
}
