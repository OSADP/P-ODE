/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.datatarget.ODEDataTarget.DataTargetException;
import com.leidos.ode.core.data.ODERegistrationResponse;
import junit.framework.TestCase;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author cassadyja
 */
public class TestODEQueueTarget extends TestCase {

    public void testQueueTargetConnect() {
        try {
            ODEQueueTarget target = new ODEQueueTarget();
            ODERegistrationResponse regResponse = new ODERegistrationResponse();
            regResponse.setQueueConnFact("ODEQueueConnFact");
            regResponse.setQueueName("BSMR1Queue");
            regResponse.setQueueHostURL("localhost");
            regResponse.setQueueHostPort(7676);
            target.configure(regResponse);
            target.close();
        } catch (DataTargetException ex) {
            Logger.getLogger(TestODEQueueTarget.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }

    }

}
