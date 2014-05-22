/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.agent.datatarget;

import com.leidos.ode.core.data.ODERegistrationResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.naming.NamingException;
import junit.framework.TestCase;

/**
 *
 * @author cassadyja
 */
public class TestODEQueueTarget extends TestCase{
    
    public void testQueueTargetConnect(){
        ODEQueueTarget target = new ODEQueueTarget();
        ODERegistrationResponse regResponse = new ODERegistrationResponse();
        regResponse.setQueueConnFact("ODEQueueConnFact");
        regResponse.setQueueName("BSMR1Queue");
        regResponse.setQueueHostURL("localhost");
        regResponse.setQueueHostPort(7676);
        
        try {
            target.configure(regResponse);
            target.close();
        } catch (JMSException ex) {
            Logger.getLogger(TestODEQueueTarget.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        } catch (NamingException ex) {
            Logger.getLogger(TestODEQueueTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
