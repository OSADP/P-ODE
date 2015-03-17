package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.datatarget.ODEDataTarget.DataTargetException;
import com.leidos.ode.registration.response.ODERegistrationResponse;
import junit.framework.TestCase;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author cassadyja
 */
public class TestODERestTarget extends TestCase {

    private ODERegistrationResponse response;

    @Override
    public void setUp() {
        response = new ODERegistrationResponse();
        response.setQueueHostURL("localhost");
        response.setQueueHostPort(9090);
        response.setQueueName("/ode-web/publish");
    }

    public void testConfigure() {
        ODERestTarget target = new ODERestTarget();
       /* try {

            target.configure(response);
        } catch (DataTargetException ex) {
            Logger.getLogger(TestODERestTarget.class.getName()).log(Level.SEVERE, null, ex);
        } */
    }

    public void testSendMessage() {
        /*ODERestTarget target = new ODERestTarget();
        try {
            target.configure(response);
            ODEAgentMessage message = new ODEAgentMessage();
            for (int i = 0; i < 10; i++) {
                target.sendMessage(message);
            }
            target.close();
        } catch (DataTargetException ex) {
            Logger.getLogger(TestODERestTarget.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

}
