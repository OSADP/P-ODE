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
 *
 * @author cassadyja, lamde
 */
public enum ODEMessageType {
    VDOTWeather("VDOT", false),
    VDOTSpeed("VDOT", false),
    VDOTTravelTime("VDOT", false),
    RITISSpeed("RITIS", true),
    RITISWeather("RITIS", true),
    BSM("BSM", false);
    private String dataSource;
    private boolean isHandleable;

    private ODEMessageType(String dataSource, boolean isHandleable) {
        this.dataSource = dataSource;
        this.isHandleable = isHandleable;
    }

    /**
     * Returns this message type's data source. Meaning,
     * the provider of this message type.
     *
     * @return the data source of this message type
     */
    public String dataSource() {
        return dataSource;
    }

    /**
     * Returns whether this message type is "handleable". Meaning,
     * the type belongs to a DataSource that requires a handler for
     * more than one data type request.
     *
     * @return true if this message type is handleable
     */
    public boolean isHandleable() {
        return isHandleable;
    }
}
