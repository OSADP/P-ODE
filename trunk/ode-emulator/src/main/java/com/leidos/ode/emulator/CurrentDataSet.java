/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.emulator;

import com.leidos.ode.agent.data.bsm.BSM;
import com.leidos.ode.agent.data.ritis.RITISSpeedData;
import com.leidos.ode.agent.data.ritis.RITISWeatherDataNWS;
import com.leidos.ode.agent.data.vdot.VDOTSpeedData;
import com.leidos.ode.agent.data.vdot.VDOTTravelTimeData;
import com.leidos.ode.agent.data.vdot.VDOTWeatherData;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cassadyja
 */
@XmlRootElement
public class CurrentDataSet {

    private String eastBoundTravelTime;
    private String westBoundTravelTime;
    private Date bsmLastUpdate;
    private Map<String, BSM> bsmDataEast = new HashMap<String, BSM>();
    private Map<String, BSM> bsmDataWest = new HashMap<String, BSM>();
    
    private Date vdotWeatherLastUpdate;
    private VDOTWeatherData vdotWeatherDataEastValue;
    private VDOTWeatherData vdotWeatherDataWestValue;
    
    private Date vdotTravelLastUpdate;
    private VDOTTravelTimeData vdotTravelDataEastValue;
    private VDOTTravelTimeData vdotTravelDataWestValue;
    
    
    private Date vdotSpeedLastUpdate;
    
    private VDOTSpeedData vdotSpeedDataEastValue;
    private VDOTSpeedData vdotSpeedDataWestValue;
    
    
    private Date ritisWeatherLastUpdate;
    private RITISWeatherDataNWS ritisWeatherDataEastValue;
    private RITISWeatherDataNWS ritisWeatherDataWestValue;
    
    private Date ritisSpeedLastUpdate;
    private RITISSpeedData ritisSpeedDataEastValue;
    private RITISSpeedData ritisSpeedDataWestValue;

    /**
     * @return the bsmLastUpdate
     */
    public Date getBsmLastUpdate() {
        return bsmLastUpdate;
    }

    /**
     * @param bsmLastUpdate the bsmLastUpdate to set
     */
    private void setBsmLastUpdate(Date bsmLastUpdate) {
        this.bsmLastUpdate = bsmLastUpdate;
    }

    /**
     * @return the vdotWeatherLastUpdate
     */
    public Date getVdotWeatherLastUpdate() {
        return vdotWeatherLastUpdate;
    }

    /**
     * @param vdotWeatherLastUpdate the vdotWeatherLastUpdate to set
     */
    private void setVdotWeatherLastUpdate(Date vdotWeatherLastUpdate) {
        this.vdotWeatherLastUpdate = vdotWeatherLastUpdate;
    }

    /**
     * @return the vdotTravelLastUpdate
     */
    public Date getVdotTravelLastUpdate() {
        return vdotTravelLastUpdate;
    }

    /**
     * @param vdotTravelLastUpdate the vdotTravelLastUpdate to set
     */
    private void setVdotTravelLastUpdate(Date vdotTravelLastUpdate) {
        this.vdotTravelLastUpdate = vdotTravelLastUpdate;
    }

    /**
     * @return the vdotSpeedLastUpdate
     */
    public Date getVdotSpeedLastUpdate() {
        return vdotSpeedLastUpdate;
    }

    /**
     * @param vdotSpeedLastUpdate the vdotSpeedLastUpdate to set
     */
    private void setVdotSpeedLastUpdate(Date vdotSpeedLastUpdate) {
        this.vdotSpeedLastUpdate = vdotSpeedLastUpdate;
    }

    /**
     * @return the ritisWeatherLastUpdate
     */
    public Date getRitisWeatherLastUpdate() {
        return ritisWeatherLastUpdate;
    }

    /**
     * @param ritisWeatherLastUpdate the ritisWeatherLastUpdate to set
     */
    private void setRitisWeatherLastUpdate(Date ritisWeatherLastUpdate) {
        this.ritisWeatherLastUpdate = ritisWeatherLastUpdate;
    }

    /**
     * @return the ritisSpeedLastUpdate
     */
    public Date getRitisSpeedLastUpdate() {
        return ritisSpeedLastUpdate;
    }

    /**
     * @param ritisSpeedLastUpdate the ritisSpeedLastUpdate to set
     */
    private void setRitisSpeedLastUpdate(Date ritisSpeedLastUpdate) {
        this.ritisSpeedLastUpdate = ritisSpeedLastUpdate;
    }

    /**
     * @return the eastBoundTravelTime
     */
    public String getEastBoundTravelTime() {
        return eastBoundTravelTime;
    }

    /**
     * @param eastBoundTravelTime the eastBoundTravelTime to set
     */
    public void setEastBoundTravelTime(String eastBoundTravelTime) {
        this.eastBoundTravelTime = eastBoundTravelTime;
    }

    /**
     * @return the westBoundTravelTime
     */
    public String getWestBoundTravelTime() {
        return westBoundTravelTime;
    }

    /**
     * @param westBoundTravelTime the westBoundTravelTime to set
     */
    public void setWestBoundTravelTime(String westBoundTravelTime) {
        this.westBoundTravelTime = westBoundTravelTime;
    }


    /**
     * @return the bsmDataEast
     */
    public Map<String, BSM> getBsmDataEast() {
        return bsmDataEast;
    }

    /**
     * @param bsmDataEast the bsmDataEast to set
     */
    public void setBsmDataEast(Map<String, BSM> bsmDataEast) {
        this.bsmDataEast = bsmDataEast;
    }

    /**
     * @return the bsmDataWest
     */
    public Map<String, BSM> getBsmDataWest() {
        return bsmDataWest;
    }

    /**
     * @param bsmDataWest the bsmDataWest to set
     */
    public void setBsmDataWest(Map<String, BSM> bsmDataWest) {
        this.bsmDataWest = bsmDataWest;
    }

    public void addBSMWestData(String ip, BSM data) {
        BSM existing = getBsmDataWest().get(ip);
        if (existing != null) {
            if (existing.getMessageCount() != data.getMessageCount()
                    && !existing.getBsmMessageId().equalsIgnoreCase(data.getBsmMessageId())) {
                getBsmDataWest().put(ip, data);
                recalculateWest();
            }
        } else {
            getBsmDataWest().put(ip, data);
            recalculateWest();
        }
        setBsmLastUpdate(new Date());
    }

    public void addBSMEastData(String ip, BSM data) {
        BSM existing = getBsmDataEast().get(ip);
        if (existing != null) {
            if (existing.getMessageCount() != data.getMessageCount()
                    && !existing.getBsmMessageId().equalsIgnoreCase(data.getBsmMessageId())) {
                getBsmDataEast().put(ip, data);
                recalculateEast();
            }
        } else {
            getBsmDataEast().put(ip, data);
            recalculateEast();
        }
        setBsmLastUpdate(new Date());
    }

    private void recalculateWest() {

    }

    private void recalculateEast() {

    }

    /**
     * @return the vdotWeatherDataEastValue
     */
    public VDOTWeatherData getVdotWeatherDataEastValue() {
        return vdotWeatherDataEastValue;
    }

    /**
     * @param vdotWeatherDataEastValue the vdotWeatherDataEastValue to set
     */
    public void setVdotWeatherDataEastValue(VDOTWeatherData vdotWeatherDataEastValue) {
        this.vdotWeatherDataEastValue = vdotWeatherDataEastValue;
        setVdotWeatherLastUpdate(new Date());
        
    }

    /**
     * @return the vdotWeatherDataWestValue
     */
    public VDOTWeatherData getVdotWeatherDataWestValue() {
        return vdotWeatherDataWestValue;
    }

    /**
     * @param vdotWeatherDataWestValue the vdotWeatherDataWestValue to set
     */
    public void setVdotWeatherDataWestValue(VDOTWeatherData vdotWeatherDataWestValue) {
        this.vdotWeatherDataWestValue = vdotWeatherDataWestValue;
        setVdotWeatherLastUpdate(new Date());
    }

    /**
     * @return the vdotTravelDataEastValue
     */
    public VDOTTravelTimeData getVdotTravelDataEastValue() {
        return vdotTravelDataEastValue;
    }

    /**
     * @param vdotTravelDataEastValue the vdotTravelDataEastValue to set
     */
    public void setVdotTravelDataEastValue(VDOTTravelTimeData vdotTravelDataEastValue) {
        this.vdotTravelDataEastValue = vdotTravelDataEastValue;
         recalculateEast();
        setVdotTravelLastUpdate(new Date());
    }

    /**
     * @return the vdotTravelDataWestValue
     */
    public VDOTTravelTimeData getVdotTravelDataWestValue() {
        return vdotTravelDataWestValue;
    }

    /**
     * @param vdotTravelDataWestValue the vdotTravelDataWestValue to set
     */
    public void setVdotTravelDataWestValue(VDOTTravelTimeData vdotTravelDataWestValue) {
        this.vdotTravelDataWestValue = vdotTravelDataWestValue;
        recalculateWest();
        setVdotTravelLastUpdate(new Date());
    }

    /**
     * @return the vdotSpeedDataEastValue
     */
    public VDOTSpeedData getVdotSpeedDataEastValue() {
        return vdotSpeedDataEastValue;
    }

    /**
     * @param vdotSpeedDataEastValue the vdotSpeedDataEastValue to set
     */
    public void setVdotSpeedDataEastValue(VDOTSpeedData vdotSpeedDataEastValue) {
        this.vdotSpeedDataEastValue = vdotSpeedDataEastValue;
        recalculateEast();
        setVdotSpeedLastUpdate(new Date());
    }

    /**
     * @return the vdotSpeedDataWestValue
     */
    public VDOTSpeedData getVdotSpeedDataWestValue() {
        return vdotSpeedDataWestValue;
    }

    /**
     * @param vdotSpeedDataWestValue the vdotSpeedDataWestValue to set
     */
    public void setVdotSpeedDataWestValue(VDOTSpeedData vdotSpeedDataWestValue) {
        this.vdotSpeedDataWestValue = vdotSpeedDataWestValue;
        recalculateWest();
        setVdotSpeedLastUpdate(new Date());
    }

    /**
     * @return the ritisWeatherDataEastValue
     */
    public RITISWeatherDataNWS getRitisWeatherDataEastValue() {
        return ritisWeatherDataEastValue;
    }

    /**
     * @param ritisWeatherDataEastValue the ritisWeatherDataEastValue to set
     */
    public void setRitisWeatherDataEastValue(RITISWeatherDataNWS ritisWeatherDataEastValue) {
        this.ritisWeatherDataEastValue = ritisWeatherDataEastValue;
        setRitisWeatherLastUpdate(new Date());
    }

    /**
     * @return the ritisWeatherDataWestValue
     */
    public RITISWeatherDataNWS getRitisWeatherDataWestValue() {
        return ritisWeatherDataWestValue;
    }

    /**
     * @param ritisWeatherDataWestValue the ritisWeatherDataWestValue to set
     */
    public void setRitisWeatherDataWestValue(RITISWeatherDataNWS ritisWeatherDataWestValue) {
        this.ritisWeatherDataWestValue = ritisWeatherDataWestValue;
        setRitisWeatherLastUpdate(new Date());
    }

    /**
     * @return the ritisSpeedDataEastValue
     */
    public RITISSpeedData getRitisSpeedDataEastValue() {
        return ritisSpeedDataEastValue;
    }

    /**
     * @param ritisSpeedDataEastValue the ritisSpeedDataEastValue to set
     */
    public void setRitisSpeedDataEastValue(RITISSpeedData ritisSpeedDataEastValue) {
        this.ritisSpeedDataEastValue = ritisSpeedDataEastValue;
        recalculateEast();
        setRitisSpeedLastUpdate(new Date());
    }

    /**
     * @return the ritisSpeedDataWestValue
     */
    public RITISSpeedData getRitisSpeedDataWestValue() {
        return ritisSpeedDataWestValue;
    }

    /**
     * @param ritisSpeedDataWestValue the ritisSpeedDataWestValue to set
     */
    public void setRitisSpeedDataWestValue(RITISSpeedData ritisSpeedDataWestValue) {
        this.ritisSpeedDataWestValue = ritisSpeedDataWestValue;
        recalculateWest();
        setRitisSpeedLastUpdate(new Date());
    }

    
    
    
    
    

}
