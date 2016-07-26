/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.emulator;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author cassadyja
 */
@XmlRootElement

public class EmulatorCollectorList {
    private String[] collectors = new String[]{"SpeedCollector",
            "VolumeCollector", "OccupancyCollector", "TravelTimeCollector",
            "WeatherCollector", "IncidentCollector"};
   

    private String startDate;
    private String endDate;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    /**
     * @return the collectors
     */
    public String[] getCollectors() {
        return collectors;
    }

    /**
     * @param collectors the collectors to set
     */
    public void setCollectors(String[] collectors) {
        this.collectors = collectors;
    }


}
