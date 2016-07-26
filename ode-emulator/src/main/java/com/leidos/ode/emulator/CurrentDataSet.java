/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.emulator;


import org.apache.log4j.Logger;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;


/**
 * @author cassadyja
 */
@XmlRootElement
public class CurrentDataSet {
    @XmlTransient
    private final String TAG = getClass().getSimpleName();
    @XmlTransient
    private Logger logger = Logger.getLogger(TAG);

    private String calculatedTravelTime;
    private List<DataDisplayElement> speedValue;
    private List<DataDisplayElement> volumeValue;
    private List<DataDisplayElement> occupancyValue;
    private List<DataDisplayElement> travelTimeValue;
    private List<DataDisplayElement> currentWeather;
    private List<IncidentDisplayDataElement> currentIncidents;
    private List<DataDisplayElement> basicSafetyMessages;

    private void recalculate() {
        logger.debug("Recalc Travel Time");
        int speed = 0;
        int count = 0;

        double val = 0.0;
        if (speedValue != null) {
            for (DataDisplayElement d : speedValue) {
                val += Double.parseDouble(d.getDataValue());
                count++;
            }
        }

        if (basicSafetyMessages != null) {
            for (DataDisplayElement d : basicSafetyMessages) {
                val += Double.parseDouble(d.getDataValue());
                count++;
            }
        }
        
        double avg = val/count;
        double tt = 5/avg;
        calculatedTravelTime = tt+"";
    }

    private void addElementToList(List<DataDisplayElement> theList, DataDisplayElement element){
        for(int i=0;i<theList.size();i++){
            if(theList.get(i).getSource().equalsIgnoreCase(element.getSource())){
                theList.remove(i);
                break;
            }
        }
        theList.add(element);        
    }

    private void addElementToList(List<IncidentDisplayDataElement> theList, IncidentDisplayDataElement element){
        for(int i=0;i<theList.size();i++){
            if(theList.get(i).getSource().equalsIgnoreCase(element.getSource())){
                theList.remove(i);
                break;
            }
        }
        theList.add(element);
    }

    /**
     * @param element the currentWeather to set
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

    public void addIncidentValue(IncidentDisplayDataElement element) {
        if (currentIncidents == null) {
            currentIncidents = new ArrayList<IncidentDisplayDataElement>();
        }

        addElementToList(currentIncidents, element);
    }

    public void addSpeedValue(DataDisplayElement element) {
        if (speedValue == null) {
            speedValue = new ArrayList<DataDisplayElement>();
        }

        if ("BSM".equals(element.getDataType())) {
            if (basicSafetyMessages == null) {
                basicSafetyMessages = new ArrayList<DataDisplayElement>();
            }
            addElementToList(basicSafetyMessages, element);
        } else {
            addElementToList(speedValue, element);
        }

        recalculate();
    }

    public void addVolumeValue(DataDisplayElement element) {
        if (volumeValue == null) {
            volumeValue = new ArrayList<DataDisplayElement>();
        }

        addElementToList(volumeValue, element);

    }

    public void addOccupancyValue(DataDisplayElement element) {
        if (occupancyValue == null) {
            occupancyValue = new ArrayList<DataDisplayElement>();
        }

        addElementToList(occupancyValue, element);

    }

    public void addTravelTimeValue(DataDisplayElement element) {
        if (travelTimeValue == null) {
            travelTimeValue = new ArrayList<DataDisplayElement>();
        }

        addElementToList(travelTimeValue, element);

    }

    public String getCalculatedTravelTime() {
        return calculatedTravelTime;
    }

    public List<DataDisplayElement> getSpeedValue() {
        return speedValue;
    }

    public List<DataDisplayElement> getVolumeValue() {
        return volumeValue;
    }

    public List<DataDisplayElement> getOccupancyValue() {
        return occupancyValue;
    }

    public List<DataDisplayElement> getTravelTimeValue() {
        return travelTimeValue;
    }

    public List<DataDisplayElement> getCurrentWeather() {
        return currentWeather;
    }

    public List<IncidentDisplayDataElement> getCurrentIncidents() {
        return currentIncidents;
    }

    public List<DataDisplayElement> getBasicSafetyMessages() {
        return basicSafetyMessages;
    }
}
