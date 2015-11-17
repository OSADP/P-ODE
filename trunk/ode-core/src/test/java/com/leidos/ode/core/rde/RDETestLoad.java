/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.core.rde;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author cassadyja
 */
public class RDETestLoad {
    public static void main(String[] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("resources/RDEClientWriterContext.xml");
        Object o = context.getBean("clientWriter");
        System.out.println(o);
        
    }
    
}
