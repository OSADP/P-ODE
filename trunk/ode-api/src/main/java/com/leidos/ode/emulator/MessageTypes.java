/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.emulator;

/**
 *
 * @author cassadyja
 */
public enum MessageTypes {
    VDOTWeather("VDOTWeather"),
    VDOTSpeed("VDOTSpeed"),
    VDOTTravel("VDOTTravelTime"),
    RITISSpeed("RITISSpeed"),
    RITISWeather("RITISWeather"),
    BSM("BSM");
    
    private final String name;
    private MessageTypes(String s){
        name = s;
    }
    
    public boolean equalsName(String otherName){
        return (otherName == null)? false:name.equals(otherName);
    }

    @Override
    public String toString(){
       return name;
    }
}
