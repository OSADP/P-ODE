/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.data.PodeDataDelivery;
import com.leidos.ode.data.PodeDataDistribution;
import com.leidos.ode.data.ServiceRequest;
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
    protected static final int TRAVEL_TIME_MESSAGE = 4;
    
    public abstract Map<ODEMessageType,List<PodeDataDistribution>> formatMessage(ODEAgentMessage agentMessage, ServiceRequest serviceRequst);
    
    protected int getLatitudeValue(double lat){
        int base = 10000000;
        
        return (int)Math.round(base*lat);
    }
    
    protected int getLongitudeValue(double lon){
        int base = 10000000;
        return (int)Math.round(base*lon);
    }    
    
}
