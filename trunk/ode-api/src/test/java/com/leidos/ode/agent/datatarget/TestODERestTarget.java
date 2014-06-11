/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.core.data.ODERegistrationResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author cassadyja
 */
public class TestODERestTarget extends TestCase{
    
    private ODERegistrationResponse response;
    
    @Override
    public void setUp(){
        response = new ODERegistrationResponse();
        response.setQueueHostURL("localhost");
        response.setQueueHostPort(9090);
        response.setQueueName("/ode-web/publish");
    }
    
    
    public void testConfigure(){
        ODERestTarget target = new ODERestTarget();
        try {
            
            target.configure(response);
        } catch (DataTargetException ex) {
            Logger.getLogger(TestODERestTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void testSendMessage(){
        ODERestTarget target = new ODERestTarget();
        try {
            target.configure(response);
            ODEAgentMessage message = new ODEAgentMessage();
            for(int i=0;i<10;i++){
                target.sendMessage(message);
            }
            target.close();
        } catch (DataTargetException ex) {
            Logger.getLogger(TestODERestTarget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
