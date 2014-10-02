/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.util;

/**
 * @author cassadyja
 */
public enum ODEMessageType {
    VDOTWeather("VDOTWeather"),
    VDOTSpeed("VDOTSpeed"),
    VDOTTravel("VDOTTravelTime"),
    RITISSpeed("RITISSpeed"),
    RITISWeather("RITISWeather"),
    BSM("BSM"),
    UNDEFINED("Undefined");
    private final String name;

    private ODEMessageType(String name) {
        this.name = name;
    }

    public static ODEMessageType typeForFeed(String feed) {
        if (feed != null) {
            if (feed.equals("vat_road_cond_point") || feed.equals("vat_road_cond_line") || feed.equals("vat_road_cond_area")) {
                return VDOTWeather;
            }
            if (feed.equals("tss_detector_status")) {
                return VDOTSpeed;
            }
            if (feed.equals("traveltimesegment")) {
                return VDOTTravel;
            }
            if (feed.equals("detector")) {
                return RITISSpeed;
            }
            if (feed.equals("weather")) {
                return RITISWeather;
            }
            if (feed.equals("bsm")) {
                return BSM;
            }
        }
        return UNDEFINED;
    }

    public boolean equalsName(String otherName) {
        return (otherName != null) && name.equals(otherName);
    }

    @Override
    public String toString() {
        return name;
    }
}
