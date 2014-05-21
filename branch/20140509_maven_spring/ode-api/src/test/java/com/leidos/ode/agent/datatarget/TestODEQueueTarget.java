/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.agent.datatarget;

import com.leidos.ode.core.data.ODERegistrationResponse;
import junit.framework.TestCase;

/**
 *
 * @author cassadyja
 */
public class TestODEQueueTarget extends TestCase{
    
    public void testQueueTargetConnect(){
        ODEQueueTarget target = new ODEQueueTarget();
        ODERegistrationResponse regResponse = new ODERegistrationResponse();
        regResponse.setQueueConnFact("");
        regResponse.setQueueHostURL("");
        regResponse.setQueueName(null);
    }
    
}
