/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.vdot.VDOTSpeedData;
import com.leidos.ode.data.*;
import com.leidos.ode.util.ODEMessageType;
import org.apache.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.util.*;

/**
 *
 * @author cassadyja
 */
public class VDOTSpeedMessageFormatter extends ODEMessageFormatter{
    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);


    
    
    public Map<ODEMessageType,List<PodeDataDistribution>> formatMessage(ODEAgentMessage agentMessage, ServiceRequest serviceRequst) {
        Map<ODEMessageType,List<PodeDataDistribution>> messages = new HashMap<ODEMessageType,List<PodeDataDistribution>>();
        VDOTSpeedData vdotSpeedData = (VDOTSpeedData)agentMessage.getFormattedMessage();

        PodeSource source = new PodeSource();
        source.setValue(PodeSource.EnumType.vdot);

        
        if(vdotSpeedData.getVdotSpeedDataElements() != null){
            List<PodeDataDistribution> speedList = new ArrayList<PodeDataDistribution>();
            List<PodeDataDistribution> volumeList = new ArrayList<PodeDataDistribution>();
            List<PodeDataDistribution> occList = new ArrayList<PodeDataDistribution>();
            
            for(VDOTSpeedData.VDOTSpeedDataElement element: vdotSpeedData.getVdotSpeedDataElements()){
                speedList.add(createMessage(agentMessage, element, source, SPEED_MESSAGE,serviceRequst));
                volumeList.add(createMessage(agentMessage, element, source, VOLUME_MESSAGE,serviceRequst));
                occList.add(createMessage(agentMessage, element, source, OCCUPANCY_MESSAGE,serviceRequst));
            }
            logger.debug("VDOT message contained ["+speedList.size()+"] Speed Messages");
            messages.put(ODEMessageType.SPEED,speedList);
            logger.debug("VDOT message contained ["+volumeList.size()+"] Volume Messages");            
            messages.put(ODEMessageType.VOLUME,volumeList);
            logger.debug("VDOT message contained ["+occList.size()+"] Occupancy Messages");            
            messages.put(ODEMessageType.OCCUPANCY,occList);
            
        }
        
        return messages;
    }
    
    
    
    private PodeDataDistribution createMessage(ODEAgentMessage agentMessage, VDOTSpeedData.VDOTSpeedDataElement element, PodeSource source, int messageType, ServiceRequest serviceRequst){
        PodeDataDistribution message = new PodeDataDistribution();
        
        PodeDialogID dialog = new PodeDialogID();
        dialog.setValue(PodeDialogID.EnumType.podeDataDistribution);
        message.setDialogID(dialog);
        
        message.setGroupID(serviceRequst.getGroupID());
        message.setRequestID(serviceRequst.getRequestID());
        SemiSequenceID seqId = new SemiSequenceID();
        seqId.setValue(SemiSequenceID.EnumType.data);
        message.setSeqID(seqId);
        
        
        Sha256Hash hash = new Sha256Hash(DatatypeConverter.parseHexBinary(agentMessage.getMessageId()));
        message.setMessageID(hash);
        
        PodeDataRecord.PodeDataChoiceType data = new PodeDataRecord.PodeDataChoiceType();
        PodeDetectorData detector = new PodeDetectorData();

        PodeDetectionMethod method = new PodeDetectionMethod();
        method.setValue(PodeDetectionMethod.EnumType.unknown);
        detector.setDetectMethod(method);
        
        detector.setDetectorID(element.getDetectorId());
        
        Position3D pos = new Position3D();
        pos.setLat(new Latitude(getLatitudeValue(element.getGeometry()[0])));
        pos.setLon(new Longitude(getLongitudeValue(element.getGeometry()[1])));
        detector.setPosition(pos);
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
        }else if(element.getLaneDirection().equalsIgnoreCase("W")){
            direction.setValue(PodeLaneDirection.EnumType.west);
        }else if(element.getLaneDirection().equalsIgnoreCase("S")){
            direction.setValue(PodeLaneDirection.EnumType.south);
        }else if(element.getLaneDirection().equalsIgnoreCase("N")){
            direction.setValue(PodeLaneDirection.EnumType.north);
        }else{
            direction.setValue(PodeLaneDirection.EnumType.east);
        }
        
        laneInfo.setLaneDirection(direction);
        laneInfo.setLaneNumber(element.getLaneNum());
        //hardcoded for Prototype.
        laneInfo.setZoneNum(1);

        laneData.setLane(laneInfo);

        List<PodeLaneData> dataList = new ArrayList<>();
        dataList.add(laneData);
        detector.setLaneData(dataList);

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
        int value = element.getOccupancy();
        if(element.getOccupancy() > 100){
            value = 100;
        }else if (element.getOccupancy() < 1){
            value = 1;
        }
            
        laneDataList.setOccupancy(value);
        
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
