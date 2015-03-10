/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent;

import com.leidos.ode.collector.ODECollector;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author cassadyja
 */
public class TestInjectAgent {
    private static final String APPLICATION_CONTEXT_ARCHIVER_FILE = "classpath*:/META-INF/ODE-Archiver-Context.xml";
    
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_ARCHIVER_FILE);
        ODEAgent agent = (ODEAgent)context.getBean("ritisSpeedSubAgent");
        System.out.println(agent);
        ODECollector collector = (ODECollector) context.getBean("ritisSpeedCollector");
        System.out.println("Have collector");
        System.out.println("Agent is: "+collector.getAgent());
    }
    
}
