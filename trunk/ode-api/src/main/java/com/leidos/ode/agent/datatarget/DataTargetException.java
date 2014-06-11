/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.agent.datatarget;

/**
 *
 * @author cassadyja
 */
public class DataTargetException extends Exception{
    
    public DataTargetException(){
        super();
    }
    public DataTargetException(String s){
        super(s);
    }
    public DataTargetException(String s, Throwable t){
        super(s,t);
    }
    
}
