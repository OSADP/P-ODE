/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.util;

/**
 *
 * @author cassadyja
 */
public class TestODEMessageType{
    
    public static void main(String[] args){
        
        String msgType = "BluFaxLink";
        if(ODEMessageType.BluFaxLink.equals(ODEMessageType.valueOf(msgType))){
            System.out.println("OK BF Link");
        }
        
        
        msgType = "BluFaxRoute";
        if(ODEMessageType.BluFaxRoute.equals(ODEMessageType.valueOf(msgType))){
            System.out.println("OK BF Route");
        }
        if(ODEMessageType.BluFaxLink.equals(ODEMessageType.valueOf(msgType))){
            System.out.println("NOT OK BF Link");
        }
        
        
    }
    
    
    
}
