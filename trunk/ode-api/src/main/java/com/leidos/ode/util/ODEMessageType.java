/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.util;

/**
 * Class for keeping track of ODE message types.
 * **WARNING: Enum is strongly coupled to bean names in the ODE-Context.xml.
 * Changes to bean names could result in unexpected behavior and/or exceptions
 * being thrown! Do not modify this class.
 * @author cassadyja, lamde
 */
public enum ODEMessageType {
    VDOTWeather("VDOTWeather"),
    VDOTSpeed("VDOTSpeed"),
    VDOTTravel("VDOTTravelTime"),
    RITISSpeed("RITISSpeed"),
    RITISWeather("RITISWeather"),
    BSM("BSM");

    private final String name;

    private ODEMessageType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
