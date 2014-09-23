/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.emulator;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.bsm.BSM;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
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
    private ODEAgentMessage vdotWeatherDataEast;
    private ODEAgentMessage vdotWeatherDataWest;
    
    private Date vdotTravelLastUpdate;
    private ODEAgentMessage vdotTravelDataEast;
    private ODEAgentMessage vdotTravelDataWest;
    
    private Date vdotSpeedLastUpdate;
    private ODEAgentMessage vdotSpeedDataEast;
    private ODEAgentMessage vdotSpeedDataWest;
    
    private Date ritisWeatherLastUpdate;
    private ODEAgentMessage ritisWeatherDataEast;
    private ODEAgentMessage ritisWeatherDataWest;
    
    private Date ritisSpeedLastUpdate;
    private ODEAgentMessage ritisSpeedDataEast;
    private ODEAgentMessage ritisSpeedDataWest;

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
     * @return the vdotWeatherDataEast
     */
    public ODEAgentMessage getVdotWeatherDataEast() {
        return vdotWeatherDataEast;
    }

    /**
     * @param vdotWeatherDataEast the vdotWeatherDataEast to set
     */
    public void setVdotWeatherDataEast(ODEAgentMessage vdotWeatherDataEast) {
        if(!this.vdotWeatherDataEast.getMessageId().equals(vdotWeatherDataEast.getMessageId())){
            this.vdotWeatherDataEast = vdotWeatherDataEast;
            recalculateEast();
        }     
        setVdotWeatherLastUpdate(new Date());
    }

    /**
     * @return the vdotWeatherDataWest
     */
    public ODEAgentMessage getVdotWeatherDataWest() {
        return vdotWeatherDataWest;
    }

    /**
     * @param vdotWeatherDataWest the vdotWeatherDataWest to set
     */
    public void setVdotWeatherDataWest(ODEAgentMessage vdotWeatherDataWest) {
        if(!this.vdotWeatherDataWest.getMessageId().equals(vdotWeatherDataWest.getMessageId())){
            this.vdotWeatherDataWest = vdotWeatherDataWest;
            recalculateWest();
        }
        setVdotWeatherLastUpdate(new Date());

    }

    /**
     * @return the vdotTravelDataEast
     */
    public ODEAgentMessage getVdotTravelDataEast() {
        return vdotTravelDataEast;
    }

    /**
     * @param vdotTravelDataEast the vdotTravelDataEast to set
     */
    public void setVdotTravelDataEast(ODEAgentMessage vdotTravelDataEast) {
        if(!this.vdotTravelDataEast.getMessageId().equals(vdotTravelDataEast.getMessageId())){
            this.vdotTravelDataEast = vdotTravelDataEast;
            recalculateEast();
        }
        setVdotTravelLastUpdate(new Date());
    }

    /**
     * @return the vdotTravelDataWest
     */
    public ODEAgentMessage getVdotTravelDataWest() {
        return vdotTravelDataWest;
    }

    /**
     * @param vdotTravelDataWest the vdotTravelDataWest to set
     */
    public void setVdotTravelDataWest(ODEAgentMessage vdotTravelDataWest) {
        if(!this.vdotTravelDataWest.getMessageId().equals(vdotTravelDataWest.getMessageId())){
            this.vdotTravelDataWest = vdotTravelDataWest;
            recalculateWest();
        }            
        setVdotTravelLastUpdate(new Date());
    }

    /**
     * @return the vdotSpeedDataEast
     */
    public ODEAgentMessage getVdotSpeedDataEast() {
        return vdotSpeedDataEast;
    }

    /**
     * @param vdotSpeedDataEast the vdotSpeedDataEast to set
     */
    public void setVdotSpeedDataEast(ODEAgentMessage vdotSpeedDataEast) {
        if(!this.vdotSpeedDataEast.getMessageId().equals(vdotSpeedDataEast.getMessageId())){
            this.vdotSpeedDataEast = vdotSpeedDataEast;
            recalculateEast();
        }            
        setVdotSpeedLastUpdate(new Date());
    }

    /**
     * @return the vdotSpeedDataWest
     */
    public ODEAgentMessage getVdotSpeedDataWest() {
        return vdotSpeedDataWest;
    }

    /**
     * @param vdotSpeedDataWest the vdotSpeedDataWest to set
     */
    public void setVdotSpeedDataWest(ODEAgentMessage vdotSpeedDataWest) {
        if(!this.vdotSpeedDataWest.getMessageId().equals(vdotSpeedDataWest.getMessageId())){
            this.vdotSpeedDataWest = vdotSpeedDataWest;
            recalculateWest();
        }            
        setVdotSpeedLastUpdate(new Date());
    }

    /**
     * @return the ritisWeatherDataEast
     */
    public ODEAgentMessage getRitisWeatherDataEast() {
        return ritisWeatherDataEast;
    }

    /**
     * @param ritisWeatherDataEast the ritisWeatherDataEast to set
     */
    public void setRitisWeatherDataEast(ODEAgentMessage ritisWeatherDataEast) {
        if(!this.ritisWeatherDataEast.getMessageId().equals(ritisWeatherDataEast.getMessageId())){
            this.ritisWeatherDataEast = ritisWeatherDataEast;
            recalculateEast();
        }         
        setRitisWeatherLastUpdate(new Date());
    }

    /**
     * @return the ritisWeatherDataWest
     */
    public ODEAgentMessage getRitisWeatherDataWest() {
        return ritisWeatherDataWest;
    }

    /**
     * @param ritisWeatherDataWest the ritisWeatherDataWest to set
     */
    public void setRitisWeatherDataWest(ODEAgentMessage ritisWeatherDataWest) {
        if(!this.ritisWeatherDataWest.getMessageId().equals(ritisWeatherDataWest.getMessageId())){
            this.ritisWeatherDataWest = ritisWeatherDataWest;
            recalculateWest();
        }        
        setRitisWeatherLastUpdate(new Date());
    }

    /**
     * @return the ritisSpeedDataEast
     */
    public ODEAgentMessage getRitisSpeedDataEast() {
        return ritisSpeedDataEast;
    }

    /**
     * @param ritisSpeedDataEast the ritisSpeedDataEast to set
     */
    public void setRitisSpeedDataEast(ODEAgentMessage ritisSpeedDataEast) {
        if(!this.ritisSpeedDataEast.getMessageId().equals(ritisSpeedDataEast.getMessageId())){
            this.ritisSpeedDataEast = ritisSpeedDataEast;
            recalculateEast();
        }
        setRitisSpeedLastUpdate(new Date());
    }

    /**
     * @return the ritisSpeedDataWest
     */
    public ODEAgentMessage getRitisSpeedDataWest() {
        return ritisSpeedDataWest;
    }

    /**
     * @param ritisSpeedDataWest the ritisSpeedDataWest to set
     */
    public void setRitisSpeedDataWest(ODEAgentMessage ritisSpeedDataWest) {
        if(!this.ritisSpeedDataWest.getMessageId().equals(ritisSpeedDataWest.getMessageId())){
            this.ritisSpeedDataWest = ritisSpeedDataWest;
            recalculateWest();
        }
        setRitisSpeedLastUpdate(new Date());
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

    public void addBSMWestData(String ip, BSM data){
        BSM existing = bsmDataWest.get(ip);
        if(existing != null){
            if(existing.getMessageCount() != data.getMessageCount()
                    && !existing.getBsmMessageId().equalsIgnoreCase(data.getBsmMessageId())){
                bsmDataWest.put(ip, data);
                recalculateWest();
            }
        }else{
            bsmDataWest.put(ip, data);
            recalculateWest();
        }
        setBsmLastUpdate(new Date());
    }
    
    public void addBSMEastData(String ip, BSM data){
        BSM existing = bsmDataEast.get(ip);
        if(existing != null){
            if(existing.getMessageCount() != data.getMessageCount()
                    && !existing.getBsmMessageId().equalsIgnoreCase(data.getBsmMessageId())){
                bsmDataEast.put(ip, data);
                recalculateEast();
            }
        }else{
            bsmDataEast.put(ip, data);
            recalculateEast();
        }
        setBsmLastUpdate(new Date());        
    }
    
    
    
    private void recalculateWest(){
        
    }
    
    private void recalculateEast(){
        
    }
    
    
}
