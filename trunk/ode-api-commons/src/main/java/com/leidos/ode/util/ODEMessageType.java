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
    VDOTWeather("VDOT", false, new byte[]{0x01,0x00,0x00,0x00}),
    VDOTSpeed("VDOT", false, new byte[]{0x01,0x00,0x00,0x00}),
    VDOTTravelTime("VDOT", false, new byte[]{0x01,0x00,0x00,0x00}),
    RITISSpeed("RITIS", true, new byte[]{0x02,0x00,0x00,0x00}),
    RITISWeather("RITIS", true, new byte[]{0x02,0x00,0x00,0x00}),
    BSM("BSM", false, new byte[]{0x04,0x00,0x00,0x00}),
    BluFaxLink("BLUFAX", false, new byte[]{0x03,0x00,0x00,0x00}),
    BluFaxRoute("BLUFAX", false, new byte[]{0x03,0x00,0x00,0x00}),
    WXDE("WXDE", false, new byte[]{0x07,0x00,0x00,0x00}),
    RTMS("RTMS", false, new byte[]{0x05,0x00,0x00,0x00}),
    SPEED("SPEED",false,new byte[]{0x00,0x00,0x00,0x00}),
    VOLUME("VOLUME",false,new byte[]{0x01,0x00,0x00,0x00}),
    OCCUPANCY("OCCUPANCY",false,new byte[]{0x01,0x00,0x00,0x00}),
    TRAVEL("TRAVEL",false,new byte[]{0x01,0x00,0x00,0x00}),
    WEATHER("WEATHER",false,new byte[]{0x01,0x00,0x00,0x00}),
    SPATMAP("SPATMAP", false, new byte[]{0x06,0x00,0x00,0x00});

    private String dataSource;
    private boolean isRestrictedRequestInterval;
    private byte[] groupId;

    private ODEMessageType(String dataSource, boolean isRestrictedRequestInterval, byte[] groupId) {
        this.dataSource = dataSource;
        this.isRestrictedRequestInterval = isRestrictedRequestInterval;
        this.groupId = groupId;
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
     * Returns whether this message type is from a data source that
     * enforces restrictions on request intervals.
     *
     * @return true if there is a restricted request interval from this source
     */
    public boolean restrictedRequestInterval() {
        return isRestrictedRequestInterval;
    }
    
    public byte[] getGroupId(){
        return groupId;
    }
}
