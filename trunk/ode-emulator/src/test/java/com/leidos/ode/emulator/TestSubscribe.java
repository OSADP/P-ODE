/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.emulator;

import com.leidos.ode.agent.datatarget.ODEDataTarget;
import com.leidos.ode.collector.ODECollector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author cassadyja
 */
public class TestSubscribe {
 
    
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:/META-INF/ODE-Emulator-Context.xml");
        ODECollector collector = (ODECollector)context.getBean("speedCollector");
        try {
            collector.startUp(null);
        } catch (ODEDataTarget.DataTargetException ex) {
            Logger.getLogger(TestSubscribe.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
