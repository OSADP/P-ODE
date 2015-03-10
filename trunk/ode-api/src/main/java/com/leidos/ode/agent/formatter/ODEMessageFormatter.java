/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.data.PodeDataDelivery;
import com.leidos.ode.util.ODEMessageType;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cassadyja
 */
public abstract class ODEMessageFormatter {
    protected static final int SPEED_MESSAGE = 1;
    protected static final int VOLUME_MESSAGE = 2;
    protected static final int OCCUPANCY_MESSAGE = 3;
    
    public abstract Map<ODEMessageType,PodeDataDelivery> formatMessage(ODEAgentMessage agentMessage);
    
}
