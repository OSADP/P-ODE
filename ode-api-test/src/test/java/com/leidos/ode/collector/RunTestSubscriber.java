/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.collector;

import com.leidos.ode.agent.datatarget.ODEDataTarget;
import com.leidos.ode.agent.datatarget.PrintingDataTarget;
import com.leidos.ode.collector.datasource.CollectorDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author cassadyja
 */
public class RunTestSubscriber {
    public static void main(String args[]){
        try {
            new RunTestSubscriber().run();
        } catch (CollectorDataSource.DataSourceException ex) {
            Logger.getLogger(RunTestSubscriber.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ODEDataTarget.DataTargetException ex) {
            Logger.getLogger(RunTestSubscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run() throws CollectorDataSource.DataSourceException, ODEDataTarget.DataTargetException{
        ApplicationContext ctx = new ClassPathXmlApplicationContext("META-INF/ODE-API-Context.xml");
        ODECollector collector = (ODECollector) ctx.getBean("BSMCollector");
        ODEDataTarget target = new PrintingDataTarget();
        collector.getAgent().setDataTarget(target);
        collector.startUp();        
    }
}
