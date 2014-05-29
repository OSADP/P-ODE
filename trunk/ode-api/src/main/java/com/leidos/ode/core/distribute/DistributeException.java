/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.core.distribute;

/**
 *
 * @author cassadyja
 */
public class DistributeException extends Exception{
    
    public DistributeException(){
        super();
    }
    
    public DistributeException(String s){
        super(s);
    }
    
    
    public DistributeException(String s, Throwable t){
        super(s,t);
    }
}
