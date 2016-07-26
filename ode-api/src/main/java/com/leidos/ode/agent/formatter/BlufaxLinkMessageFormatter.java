/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.blufax.BluFaxLinkData;
import com.leidos.ode.data.*;
import com.leidos.ode.util.ODEMessageType;
import org.tmdd._3.messages.DateTimeZone;
import org.tmdd._3.messages.LinkStatus;
import org.tmdd._3.messages.LinkStatusList;
import org.tmdd._3.messages.LinkStatusMsg;

import javax.xml.bind.DatatypeConverter;
import java.util.*;

/**
 *
 * @author cassadyja
 */
public class BlufaxLinkMessageFormatter extends ODEMessageFormatter{
    private double latitude;
    private double longitude;
    
    
    @Override
    public Map<ODEMessageType,List<PodeDataDistribution>> formatMessage(ODEAgentMessage agentMessage, ServiceRequest serviceRequst) {
        Map<ODEMessageType,List<PodeDataDistribution>> messages = new HashMap<ODEMessageType,List<PodeDataDistribution>>();
        BluFaxLinkData linkData = (BluFaxLinkData)agentMessage.getFormattedMessage();
        
        PodeSource source = new PodeSource();
        source.setValue(PodeSource.EnumType.blufax);
        Sha256Hash hash = new Sha256Hash(DatatypeConverter.parseHexBinary(agentMessage.getMessageId()));

        List<PodeDataDistribution> travelMessages = new ArrayList<PodeDataDistribution>();
        List<PodeDataDistribution> speedMessages = new ArrayList<PodeDataDistribution>();
        List<PodeDataDistribution> volumeMessages = new ArrayList<PodeDataDistribution>();
        List<PodeDataDistribution> occupancyMessages = new ArrayList<PodeDataDistribution>();
        
        if(linkData.getLinkStatusMsg() != null){
            LinkStatusMsg lsm = linkData.getLinkStatusMsg();
            if(lsm.getLinkStatusItem() != null){
                List<LinkStatus> lsList = lsm.getLinkStatusItem();
                for(LinkStatus linkStatus:lsList){
                    LinkStatus.LinkList linkList = linkStatus.getLinkList();
                    List<LinkStatusList> linkStatusListList = linkList.getLink();
                    for(LinkStatusList lsl:linkStatusListList){
                        travelMessages.add(createMessage(lsl, source, hash, TRAVEL_TIME_MESSAGE,serviceRequst));
                        if(lsl.getSpeedAverage() != null){
                            speedMessages.add(createMessage(lsl, source, hash, SPEED_MESSAGE,serviceRequst));
                        }
                        if(lsl.getVolume() != null){
                            volumeMessages.add(createMessage(lsl, source, hash, VOLUME_MESSAGE,serviceRequst));
                        }
                        if(lsl.getOccupancy() != null){
                            occupancyMessages.add(createMessage(lsl, source, hash, OCCUPANCY_MESSAGE,serviceRequst));
                        }
                    }
                }
            }
        }
        
        messages.put(ODEMessageType.TRAVEL, travelMessages);
        if(speedMessages.size() > 0){
            messages.put(ODEMessageType.SPEED, speedMessages);
        }
        if(volumeMessages.size() > 0){
            messages.put(ODEMessageType.VOLUME, volumeMessages);
        }
        if(occupancyMessages.size() > 0){
            messages.put(ODEMessageType.OCCUPANCY, occupancyMessages);        
        }

        
        
        return messages;
    }    

    private PodeDataDistribution createMessage(LinkStatusList lsl, PodeSource source, Sha256Hash hash, int messageType, ServiceRequest serviceRequst) {
        PodeDataRecord.PodeDataChoiceType data = new PodeDataRecord.PodeDataChoiceType();
        PodeDetectorData detector = new PodeDetectorData();
        
        PodeDetectionMethod method = new PodeDetectionMethod();
        method.setValue(PodeDetectionMethod.EnumType.unknown);
        detector.setDetectMethod(method);
        
        detector.setDetectorID(lsl.getLinkId());
        
        Position3D pos = new Position3D();
        pos.setLat(new Latitude(getLatitudeValue(latitude)));
        pos.setLon(new Longitude(getLongitudeValue(longitude)));
        detector.setPosition(pos);
        
        
        PodeLaneData laneData = new PodeLaneData();
        
        PodeDataElementList laneDataList = createLaneDataList(lsl, messageType);
        
        laneData.setData(laneDataList);
        
        PodeLaneInfo laneInfo = new PodeLaneInfo();
        
        PodeLaneDirection direction = new PodeLaneDirection();
        
        if ("e".equalsIgnoreCase(lsl.getLinkDirection())) {
            direction.setValue(PodeLaneDirection.EnumType.east);
        } else if ("w".equalsIgnoreCase(lsl.getLinkDirection())) {
            direction.setValue(PodeLaneDirection.EnumType.west);
        }
        
        laneInfo.setLaneDirection(direction);
        
        //Hard coded for prototype
        laneInfo.setZoneNum(1);
        
        laneData.setLane(laneInfo);

        List<PodeLaneData> dataList = new ArrayList<>();
        dataList.add(laneData);
        detector.setLaneData(dataList);
        data.selectDetector(detector);
        PodeDataRecord record = new PodeDataRecord();
        record.setPodeData(data);
        DateTimeZone dtz = lsl.getLastUpdateTime();
        
        DDateTime dateTime = getDDateTimeForDateTimeZone(dtz);
        record.setLastupdatetime(dateTime);
        
        record.setMeasduration(lsl.getEventDescriptionTime());
        
        record.setRoutename("I-66");
        record.setSource(source);
        
        PodeDataDistribution message = new PodeDataDistribution();
        message.setPodeData(record);
        
        
        
        PodeDialogID dialog = new PodeDialogID();
        dialog.setValue(PodeDialogID.EnumType.podeDataDistribution);
        message.setDialogID(dialog);
        
        message.setGroupID(serviceRequst.getGroupID());
        message.setRequestID(serviceRequst.getRequestID());
        SemiSequenceID seqId = new SemiSequenceID();
        seqId.setValue(SemiSequenceID.EnumType.data);
        message.setSeqID(seqId);        
        
        message.setMessageID(hash);
        return message;
    }

    private PodeDataElementList createLaneDataList(LinkStatusList lsl, int messageType) {
        if(messageType == TRAVEL_TIME_MESSAGE){
            return createTravelTimeMessage(lsl);
        }else if(messageType == SPEED_MESSAGE){
            return createSpeedMessage(lsl);
        }else if(messageType == VOLUME_MESSAGE){
            return createVolumeMessage(lsl);
        }else if(messageType == OCCUPANCY_MESSAGE){
            return createOccupancyMessage(lsl);
        }
        
        return null;
    }

    private PodeDataElementList createTravelTimeMessage(LinkStatusList lsl) {
        PodeDataElementList laneDataList = createSpeedMessage(lsl);
        PodeTravelTime travelTime = new PodeTravelTime();
        int value = lsl.getTravelTime()/60;
        travelTime.setTravelTime(new DMinute(value));
        laneDataList.setTravelTimeInfo(travelTime);
        return laneDataList;
    }
    
    private PodeDataElementList createSpeedMessage(LinkStatusList lsl) {
        PodeDataElementList laneDataList = new PodeDataElementList();
        if(lsl.getSpeedAverage() != null){
            //speed in mph
            int normalSpeed = lsl.getSpeedAverage();
            //convert to meter per second
            double meterSecond = normalSpeed*.44704;
            //convert to .02 meter per second as per SAE J2735
            double saeSpeed = meterSecond / .02;

            Speed speed = new Speed((int)Math.round(saeSpeed));
            laneDataList.setSpeed(speed);
        }else{
            Speed speed = new Speed(0);
            laneDataList.setSpeed(speed);
        }
        return laneDataList;
    }    

    
    private PodeDataElementList createVolumeMessage(LinkStatusList lsl) {
        PodeDataElementList laneDataList = createSpeedMessage(lsl);
        if(lsl.getVolume() > 25){
            laneDataList.setVolume(25);
        }else if(lsl.getVolume() < 1){
            laneDataList.setVolume(1);
        }else{
            laneDataList.setVolume(lsl.getVolume().intValue());
        }        
        return laneDataList;
    }    
    
    private PodeDataElementList createOccupancyMessage(LinkStatusList lsl) {
        PodeDataElementList laneDataList = createSpeedMessage(lsl);
        laneDataList.setOccupancy(lsl.getOccupancy().intValue());
        return laneDataList;
    }    
    
    
    
    
    private DDateTime getDDateTimeForDateTimeZone(DateTimeZone dtz){
        Calendar cal = Calendar.getInstance();
        
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
    /**
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
}
