/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.vdot.VDOTSpeedData;
import com.leidos.ode.data.DDateTime;
import com.leidos.ode.data.DDay;
import com.leidos.ode.data.DHour;
import com.leidos.ode.data.DMinute;
import com.leidos.ode.data.DMonth;
import com.leidos.ode.data.DSecond;
import com.leidos.ode.data.DYear;
import com.leidos.ode.data.PodeDataDelivery;
import com.leidos.ode.data.PodeDataElementList;
import com.leidos.ode.data.PodeDataRecord;
import com.leidos.ode.data.PodeDetectionMethod;
import com.leidos.ode.data.PodeDetectorData;
import com.leidos.ode.data.PodeLaneData;
import com.leidos.ode.data.PodeLaneDirection;
import com.leidos.ode.data.PodeLaneInfo;
import com.leidos.ode.data.PodeLaneType;
import com.leidos.ode.data.PodeSource;
import com.leidos.ode.data.Speed;
import com.leidos.ode.util.ODEMessageType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cassadyja
 */
public class VDOTSpeedMessageFormatter extends ODEMessageFormatter{


    
    
    public Map<ODEMessageType,PodeDataDelivery> formatMessage(ODEAgentMessage agentMessage) {
        Map<ODEMessageType,PodeDataDelivery> messages = new HashMap<ODEMessageType,PodeDataDelivery>();
        VDOTSpeedData vdotSpeedData = (VDOTSpeedData)agentMessage.getFormattedMessage();

        PodeSource source = new PodeSource();
        source.setValue(PodeSource.EnumType.vdot);

        
        if(vdotSpeedData.getVdotSpeedDataElements() != null){
            for(VDOTSpeedData.VDOTSpeedDataElement element: vdotSpeedData.getVdotSpeedDataElements()){
                PodeDataDelivery speedMessage = createMessage(element, source, SPEED_MESSAGE);
                PodeDataDelivery volumeMessage = createMessage(element, source, VOLUME_MESSAGE);
                PodeDataDelivery occupancyMessage = createMessage(element, source, OCCUPANCY_MESSAGE);
                
                messages.put(ODEMessageType.SPEED,speedMessage);
                messages.put(ODEMessageType.VOLUME,volumeMessage);
                messages.put(ODEMessageType.OCCUPANCY,occupancyMessage);
                
            }
        }
        
        return messages;
    }
    
    
    
    private PodeDataDelivery createMessage(VDOTSpeedData.VDOTSpeedDataElement element, PodeSource source, int messageType){
        PodeDataDelivery message = new PodeDataDelivery();

        PodeDataRecord.PodeDataChoiceType data = new PodeDataRecord.PodeDataChoiceType();
        PodeDetectorData detector = new PodeDetectorData();

        PodeDetectionMethod method = new PodeDetectionMethod();
        method.setValue(PodeDetectionMethod.EnumType.unknown);
        detector.setDetectMethod(method);
        detector.setDetectorID(element.getDetectorId());

        PodeLaneData laneData = new PodeLaneData();

        
        PodeDataElementList laneDataList = null;
        if(messageType == SPEED_MESSAGE){
            laneDataList = createSpeedDataElementList(element, source);
        }else if(messageType == VOLUME_MESSAGE){
            laneDataList = createVolumeDataElementList(element, source);
        }else{
            laneDataList = createOccupancyDataElementList(element, source);
        }
        
        laneData.setData(laneDataList);

        PodeLaneInfo laneInfo = new PodeLaneInfo();

        PodeLaneDirection direction = new PodeLaneDirection();
        if(element.getLaneDirection().equalsIgnoreCase("E")){
            direction.setValue(PodeLaneDirection.EnumType.east);
        }else{
            direction.setValue(PodeLaneDirection.EnumType.west);
        }
        laneInfo.setLaneDirection(direction);
        laneInfo.setLaneNumber(element.getLaneNum());
        //hardcoded for Prototype.
        laneInfo.setZoneNum(1);

        laneData.setLane(laneInfo);

        detector.setLaneData(laneData);

        data.selectDetector(detector);

        
        PodeDataRecord record = new PodeDataRecord();
        record.setPodeData(data);

        DDateTime dateTime = getDateTimeForElement(element);
        record.setLastupdatetime(dateTime);

        //TODO: set this to real value.
//        record.setMeasduration(0);
        record.setRoutename("I-66");
        record.setSource(source);
        
        
        message.setPodeData(record);

        return message;
        
    }
    
    
     private PodeDataElementList createSpeedDataElementList(VDOTSpeedData.VDOTSpeedDataElement element, PodeSource source){
        PodeDataElementList laneDataList = new PodeDataElementList();
        //speed in mph
        int normalSpeed = element.getSpeed();
        //convert to meter per second
        double meterSecond = normalSpeed*.44704;
        //convert to .02 meter per second as per SAE J2735
        double saeSpeed = meterSecond / .02;
        
        Speed speed = new Speed((int)Math.round(saeSpeed));
        laneDataList.setSpeed(speed);
        
        return laneDataList;
    }   
    
    
    private PodeDataElementList createOccupancyDataElementList(VDOTSpeedData.VDOTSpeedDataElement element, PodeSource source){
        PodeDataElementList laneDataList = createSpeedDataElementList(element, source);
        
        laneDataList.setOccupancy(element.getOccupancy());
        
        return laneDataList;
    }
    
    
    private PodeDataElementList createVolumeDataElementList(VDOTSpeedData.VDOTSpeedDataElement element, PodeSource source){
        PodeDataElementList laneDataList = createSpeedDataElementList(element, source);
        if(element.getVolume() > 25){
            laneDataList.setVolume(25);
        }else{
            laneDataList.setVolume(element.getVolume());
        }
        
        
        return laneDataList;
    }    
    
    private DDateTime getDateTimeForElement(VDOTSpeedData.VDOTSpeedDataElement element){
        Date date = element.getLastUpdated();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        DDateTime dateTime = new DDateTime();
        DHour hour = new DHour(cal.get(Calendar.HOUR_OF_DAY));
        dateTime.setHour(hour);
        DMinute min = new DMinute(cal.get(Calendar.MINUTE));
        dateTime.setMinute(min);
        DSecond sec = new DSecond(cal.get(Calendar.SECOND));
        dateTime.setSecond(sec);
        DMonth month = new DMonth(cal.get(Calendar.MONTH)+1);
        dateTime.setMonth(month);
        DDay day = new DDay(cal.get(Calendar.DAY_OF_MONTH));
        dateTime.setDay(day);
        DYear year = new DYear(cal.get(Calendar.YEAR));
        dateTime.setYear(year);
        return dateTime;
    }
    
}
