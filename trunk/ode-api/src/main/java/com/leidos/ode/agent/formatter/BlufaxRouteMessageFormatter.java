/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.data.PodeDataDelivery;
import com.leidos.ode.util.ODEMessageType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cassadyja
 */
public class BlufaxRouteMessageFormatter extends ODEMessageFormatter{
    
    @Override
    public Map<ODEMessageType,PodeDataDelivery> formatMessage(ODEAgentMessage agentMessage) {
        Map<ODEMessageType,PodeDataDelivery> messages = new HashMap<ODEMessageType,PodeDataDelivery>();
        
        return messages;
    }    
    
}
