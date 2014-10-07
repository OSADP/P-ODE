/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.collector;

import com.leidos.ode.agent.datatarget.ODEDataTarget;
import com.leidos.ode.collector.datasource.CollectorDataSource;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



/**
 *
 * @author cassadyja
 */
public class TestRITISFeed {
    public static void main(String args[]){
        try {
            new TestRITISFeed().run();
        } catch (CollectorDataSource.DataSourceException ex) {
            Logger.getLogger(TestRITISFeed.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ODEDataTarget.DataTargetException ex) {
            Logger.getLogger(TestRITISFeed.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    
    public void run() throws CollectorDataSource.DataSourceException, ODEDataTarget.DataTargetException{
        ApplicationContext ctx = new ClassPathXmlApplicationContext("META-INF/ODE-Context.xml");
        ODECollector collector = (ODECollector) ctx.getBean("RITISFilterCollector");
        collector.startUp();        
    }
}
