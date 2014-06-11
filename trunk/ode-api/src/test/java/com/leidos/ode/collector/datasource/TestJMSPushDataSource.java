/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.collector.datasource;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author cassadyja
 */
public class TestJMSPushDataSource extends TestCase{
    
    public void testConenct(){
        JMSPushDataSource ds = new JMSPushDataSource();
        ds.setHostURL("jms1.ritis.org");
        ds.setHostPort("61617");
        ds.setUser("leidos");
        ds.setPass("H3SvjshHnNBX");
        ds.setQueueName("Clients.leidos.ATIS.Events");
        
        try {
            ds.startDataSource();
        } catch (DataSourceException ex) {
            Logger.getLogger(TestJMSPushDataSource.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            fail();
        }
    }
    
}
