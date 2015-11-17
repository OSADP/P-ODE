/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.emulator;


import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;


/**
 * @author cassadyja
 */
@XmlRootElement
public class CurrentDataSet {

    private String eastBoundTravelTime;
    private String westBoundTravelTime;
    private String eastBoundBSMTime;
    private String westBoundBSMTime;

    private Date speedLastUpdate;
    private List<DataDisplayElement> speedEastValue;
    private List<DataDisplayElement> speedWestValue;
    
    private Date volumeLastUpdate;
    private List<DataDisplayElement> volumeEastValue;
    private List<DataDisplayElement> volumeWestValue;
    
    private Date occupancyLastUpdate;
    private List<DataDisplayElement> occupancyEastValue;
    private List<DataDisplayElement> occupancyWestValue;
    
    private Date travelTimeLastUpdate;
    private List<DataDisplayElement> travelTimeEast;
    private List<DataDisplayElement> travelTimeWest;
    
    private Date weatherLastUpdate;
    private List<DataDisplayElement> currentWeather;
    
    
    

 



    
//    private double getBSMAverageSpeedInMilesPerHour(Map<String, BSM> bsmData){
//        Iterator<String> it = bsmData.keySet().iterator();
//        double speed = 0;
//        int count = 0;
//        while(it.hasNext()){
//            String key = it.next();
//            BSM bsm = getBsmDataEast().get(key);
//            if(bsm != null){
//                speed += bsm.getSpeed();
//                count++;
//            }
//        }
//        if(count > 0){
//            double speedAvg = (double)speed/count;
//            //Convert meters per second into miles per hour
//            speedAvg = speedAvg * 2.237;
//            return speedAvg;
//        }
//        return -1;
//    }
    
    
    
//    
//    private void recalculateWest() {
//        int speed = 0;
//        int count = 0;
//        if(ritisSpeedDataWestValue != null){
//            speed += ritisSpeedDataWestValue.getZoneDetectorDataRITIS().getCollectionPeriod().getCollectionPeriodItem().get(0).getZoneReports()
//                        .getZoneReport().get(0).getZoneData().getZoneDataItem().get(0).getZoneVehicleSpeed();
//            count++;
//        }
//        
//        if(vdotSpeedDataWestValue != null){
//            speed += vdotSpeedDataWestValue.getVdotSpeedDataElements().get(0).getSpeed();
//            count++;
//        }
//        
//        if(blufaxLinkWest != null){
//            speed += blufaxLinkWest.getSpeedAverage();
//            count++;
//        }
//        
//        if(blufaxRouteWest != null){
//            speed += blufaxRouteWest.getSpeedAverage();
//            count++;
//        }
//        
//        double bsmWestSpeed = getBSMAverageSpeedInMilesPerHour(bsmDataWest);
//        if(bsmWestSpeed > -1){
//            speed += bsmWestSpeed;
//            count++;
//        }
//        
//        if(count > 0){
//            double speedAvg = (double)speed/count;
//            double tt = 5/speedAvg;
//            westBoundTravelTime = NumberFormat.getInstance().format(tt*60);
//        }
//    }
//
//    private void recalculateEast() {
//        int speed = 0;
//        int count = 0;
//        if(ritisSpeedDataEastValue != null){
//            speed += ritisSpeedDataEastValue.getZoneDetectorDataRITIS().getCollectionPeriod().getCollectionPeriodItem().get(0).getZoneReports()
//                        .getZoneReport().get(0).getZoneData().getZoneDataItem().get(0).getZoneVehicleSpeed();
//            count++;
//        }
//        
//        if(vdotSpeedDataEastValue != null){
//            speed += vdotSpeedDataEastValue.getVdotSpeedDataElements().get(0).getSpeed();
//            count++;
//        }
//        if(blufaxLinkEast != null){
//            speed += blufaxLinkEast.getSpeedAverage();
//            count++;
//        }
//        
//        if(blufaxRouteEast != null){
//            speed += blufaxRouteEast.getSpeedAverage();
//            count++;
//        }
//        
//        double bsmEastSpeed = getBSMAverageSpeedInMilesPerHour(bsmDataEast);
//        if(bsmEastSpeed > -1){
//            speed += bsmEastSpeed;
//            count++;
//        }        
//        
//        
//        if(count > 0){
//            double speedAvg = (double)speed/count;
//            double tt = 5/speedAvg;
//            eastBoundTravelTime = NumberFormat.getInstance().format(tt*60);
//        }
//    }

    
    private void addElementToList(List<DataDisplayElement> theList, DataDisplayElement element){
        for(int i=0;i<theList.size();i++){
            if(theList.get(i).getSource().equalsIgnoreCase(element.getSource())){
                theList.remove(i);
                break;
            }
        }
        theList.add(element);        
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
     * @return the eastBoundBSMTime
     */
    public String getEastBoundBSMTime() {
        return eastBoundBSMTime;
    }

    /**
     * @param eastBoundBSMTime the eastBoundBSMTime to set
     */
    public void setEastBoundBSMTime(String eastBoundBSMTime) {
        this.eastBoundBSMTime = eastBoundBSMTime;
    }

    /**
     * @return the westBoundBSMTime
     */
    public String getWestBoundBSMTime() {
        return westBoundBSMTime;
    }

    /**
     * @param westBoundBSMTime the westBoundBSMTime to set
     */
    public void setWestBoundBSMTime(String westBoundBSMTime) {
        this.westBoundBSMTime = westBoundBSMTime;
    }

    /**
     * @return the speedLastUpdate
     */
    public Date getSpeedLastUpdate() {
        return speedLastUpdate;
    }

    /**
     * @param speedLastUpdate the speedLastUpdate to set
     */
    public void setSpeedLastUpdate(Date speedLastUpdate) {
        this.speedLastUpdate = speedLastUpdate;
    }

    
    public void addSpeedEastValue(DataDisplayElement element){
        if(speedEastValue == null){
            speedEastValue = new ArrayList<DataDisplayElement>();
        }
        addElementToList(speedEastValue, element);
    }
    
    /**
     * @return the speedEastValue
     */
    public List<DataDisplayElement> getSpeedEastValue() {
        return speedEastValue;
    }

    /**
     * @param speedEastValue the speedEastValue to set
     */
    public void setSpeedEastValue(List<DataDisplayElement> speedEastValue) {
        this.speedEastValue = speedEastValue;
    }

    public void addSpeedWestValue(DataDisplayElement element){
        if(speedWestValue == null){
            speedWestValue = new ArrayList<DataDisplayElement>();
        }
        addElementToList(speedWestValue, element);
        
    }
    
    

    
    /**
     * @return the speedWestValue
     */
    public List<DataDisplayElement> getSpeedWestValue() {
        return speedWestValue;
    }

    /**
     * @param speedWestValue the speedWestValue to set
     */
    public void setSpeedWestValue(List<DataDisplayElement> speedWestValue) {
        this.speedWestValue = speedWestValue;
    }

    /**
     * @return the volumeLastUpdate
     */
    public Date getVolumeLastUpdate() {
        return volumeLastUpdate;
    }

    /**
     * @param volumeLastUpdate the volumeLastUpdate to set
     */
    public void setVolumeLastUpdate(Date volumeLastUpdate) {
        this.volumeLastUpdate = volumeLastUpdate;
    }

    /**
     * @return the volumeEastValue
     */
    public List<DataDisplayElement> getVolumeEastValue() {
        return volumeEastValue;
    }

    
    public void addVolumeEastValue(DataDisplayElement element){
        if(volumeEastValue == null){
            volumeEastValue = new ArrayList<DataDisplayElement>();
        }
        addElementToList(volumeEastValue, element);        
    }
    
    
    /**
     * @param volumeEastValue the volumeEastValue to set
     */
    public void setVolumeEastValue(List<DataDisplayElement> volumeEastValue) {
        this.volumeEastValue = volumeEastValue;
    }

    /**
     * @return the volumeWestValue
     */
    public List<DataDisplayElement> getVolumeWestValue() {
        return volumeWestValue;
    }
    
    public void addVolumeWestValue(DataDisplayElement element){
        if(volumeWestValue == null){
            volumeWestValue = new ArrayList<DataDisplayElement>();
        }
        addElementToList(volumeWestValue, element);             
    }
    

    /**
     * @param volumeWestValue the volumeWestValue to set
     */
    public void setVolumeWestValue(List<DataDisplayElement> volumeWestValue) {
        this.volumeWestValue = volumeWestValue;
    }

    /**
     * @return the occupancyLastUpdate
     */
    public Date getOccupancyLastUpdate() {
        return occupancyLastUpdate;
    }

    /**
     * @param occupancyLastUpdate the occupancyLastUpdate to set
     */
    public void setOccupancyLastUpdate(Date occupancyLastUpdate) {
        this.occupancyLastUpdate = occupancyLastUpdate;
    }

    /**
     * @return the occupancyEastValue
     */
    public List<DataDisplayElement> getOccupancyEastValue() {
        return occupancyEastValue;
    }

    public void addOccupancyEastValue(DataDisplayElement element){
        if(occupancyEastValue == null){
            occupancyEastValue = new ArrayList<DataDisplayElement>();
        }
        addElementToList(occupancyEastValue, element);                
    }
    
    
    /**
     * @param occupancyEastValue the occupancyEastValue to set
     */
    public void setOccupancyEastValue(List<DataDisplayElement> occupancyEastValue) {
        this.occupancyEastValue = occupancyEastValue;
    }

    /**
     * @return the occupancyWestValue
     */
    public List<DataDisplayElement> getOccupancyWestValue() {
        return occupancyWestValue;
    }

    public void addOccupancyWestValue(DataDisplayElement element){
        if(occupancyWestValue == null){
            occupancyWestValue = new ArrayList<DataDisplayElement>();
        }
        addElementToList(occupancyWestValue, element);           
    }

    /**
     * @param occupancyWestValue the occupancyWestValue to set
     */
    public void setOccupancyWestValue(List<DataDisplayElement> occupancyWestValue) {
        this.occupancyWestValue = occupancyWestValue;
    }

    /**
     * @return the travelTimeLastUpdate
     */
    public Date getTravelTimeLastUpdate() {
        return travelTimeLastUpdate;
    }

    /**
     * @param travelTimeLastUpdate the travelTimeLastUpdate to set
     */
    public void setTravelTimeLastUpdate(Date travelTimeLastUpdate) {
        this.travelTimeLastUpdate = travelTimeLastUpdate;
    }

    /**
     * @return the travelTimeEast
     */
    public List<DataDisplayElement> getTravelTimeEast() {
        return travelTimeEast;
    }

    public void addTravelTimeEastValue(DataDisplayElement element){
        if(travelTimeEast == null){
            travelTimeEast = new ArrayList<DataDisplayElement>();
        }
        addElementToList(travelTimeEast, element);          
    }
    
    
    /**
     * @param travelTimeEast the travelTimeEast to set
     */
    public void setTravelTimeEast(List<DataDisplayElement> travelTimeEast) {
        this.travelTimeEast = travelTimeEast;
    }

    /**
     * @return the travelTimeWest
     */
    public List<DataDisplayElement> getTravelTimeWest() {
        return travelTimeWest;
    }

    public void addTravelTimeWestValue(DataDisplayElement element){
        if(travelTimeWest == null){
            travelTimeWest = new ArrayList<DataDisplayElement>();
        }
        addElementToList(travelTimeWest, element);        
    }    
    
    /**
     * @param travelTimeWest the travelTimeWest to set
     */
    public void setTravelTimeWest(List<DataDisplayElement> travelTimeWest) {
        this.travelTimeWest = travelTimeWest;
    }

    /**
     * @return the weatherLastUpdate
     */
    public Date getWeatherLastUpdate() {
        return weatherLastUpdate;
    }

    /**
     * @param weatherLastUpdate the weatherLastUpdate to set
     */
    public void setWeatherLastUpdate(Date weatherLastUpdate) {
        this.weatherLastUpdate = weatherLastUpdate;
    }

    /**
     * @return the currentWeather
     */
    public List<DataDisplayElement> getCurrentWeather() {
        return currentWeather;
    }

    /**
     * @param currentWeather the currentWeather to set
     */
    public void setCurrentWeather(List<DataDisplayElement> currentWeather) {
        this.currentWeather = currentWeather;
    }

    /**
     * @param currentWeather the currentWeather to set
     */
    public void addCurrentWeather(DataDisplayElement element) {
        if(currentWeather == null){
            currentWeather = new ArrayList<DataDisplayElement>();
        }
        for(int i=0;i<currentWeather.size();i++){
            if(currentWeather.get(i).getDataType().equals(element.getDataType())){
                currentWeather.remove(i);
                break;
            }
        }
        currentWeather.add(element);
        
    }    
    

}
