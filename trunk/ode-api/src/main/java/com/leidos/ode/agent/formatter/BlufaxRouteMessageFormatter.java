/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.formatter;

import com.fastlanesw.bfw.RouteStatusExt;
import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.blufax.BluFaxRouteData;
import static com.leidos.ode.agent.formatter.ODEMessageFormatter.TRAVEL_TIME_MESSAGE;
import com.leidos.ode.data.DDateTime;
import com.leidos.ode.data.DDay;
import com.leidos.ode.data.DHour;
import com.leidos.ode.data.DMinute;
import com.leidos.ode.data.DMonth;
import com.leidos.ode.data.DSecond;
import com.leidos.ode.data.DYear;
import com.leidos.ode.data.Latitude;
import com.leidos.ode.data.Longitude;
import com.leidos.ode.data.PodeDataDelivery;
import com.leidos.ode.data.PodeDataDistribution;
import com.leidos.ode.data.PodeDataElementList;
import com.leidos.ode.data.PodeDataRecord;
import com.leidos.ode.data.PodeDetectionMethod;
import com.leidos.ode.data.PodeDetectorData;
import com.leidos.ode.data.PodeDialogID;
import com.leidos.ode.data.PodeLaneData;
import com.leidos.ode.data.PodeLaneDirection;
import com.leidos.ode.data.PodeLaneInfo;
import com.leidos.ode.data.PodeSource;
import com.leidos.ode.data.PodeTravelTime;
import com.leidos.ode.data.Position3D;
import com.leidos.ode.data.SemiSequenceID;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.data.Sha256Hash;
import com.leidos.ode.data.Speed;
import com.leidos.ode.util.ODEMessageType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
import org.tmdd._3.messages.DateTimeZone;
import org.tmdd._3.messages.LinkStatusList;
import org.tmdd._3.messages.RouteStatus;
import org.tmdd._3.messages.RouteStatusList;

/**
 *
 * @author cassadyja
 */
public class BlufaxRouteMessageFormatter extends ODEMessageFormatter{
    private double latitude;
    private double longitude;
    
    
    @Override
    public Map<ODEMessageType,List<PodeDataDistribution>> formatMessage(ODEAgentMessage agentMessage, ServiceRequest serviceRequst) {
        Map<ODEMessageType,List<PodeDataDistribution>> messages = new HashMap<ODEMessageType,List<PodeDataDistribution>>();
        BluFaxRouteData routeData = (BluFaxRouteData) agentMessage.getFormattedMessage();
        
        PodeSource source = new PodeSource();
        source.setValue(PodeSource.EnumType.blufax);
        Sha256Hash hash = new Sha256Hash(DatatypeConverter.parseHexBinary(agentMessage.getMessageId()));

        List<PodeDataDistribution> travelMessages = new ArrayList<PodeDataDistribution>();
        List<PodeDataDistribution> speedMessages = new ArrayList<PodeDataDistribution>();
        List<PodeDataDistribution> volumeMessages = new ArrayList<PodeDataDistribution>();
        List<PodeDataDistribution> occupancyMessages = new ArrayList<PodeDataDistribution>();        

        
        if(routeData.getRouteStatusMsg() != null && routeData.getRouteStatusMsg().getRouteStatusItem() != null){
            for(RouteStatus routeStatus:routeData.getRouteStatusMsg().getRouteStatusItem()){
                if(routeStatus.getRouteList() != null && routeStatus.getRouteList().getRoute() != null){
                    for(RouteStatusList rsl:routeStatus.getRouteList().getRoute()){
                        
                        travelMessages.add(createMessage(rsl, source, hash, TRAVEL_TIME_MESSAGE,serviceRequst));
                        if(rsl.getSpeedAverage() != null){
                            speedMessages.add(createMessage(rsl, source, hash, SPEED_MESSAGE,serviceRequst));
                        }
                        if(rsl.getVolume() != null){
                            volumeMessages.add(createMessage(rsl, source, hash, VOLUME_MESSAGE,serviceRequst));
                        }
                        if(rsl.getOccupancy() != null){
                            occupancyMessages.add(createMessage(rsl, source, hash, OCCUPANCY_MESSAGE,serviceRequst));                        
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
    
    
    private PodeDataDistribution createMessage(RouteStatusList rsl, PodeSource source, Sha256Hash hash, int messageType, ServiceRequest serviceRequest) {
        PodeDataRecord.PodeDataChoiceType data = new PodeDataRecord.PodeDataChoiceType();
        PodeDetectorData detector = new PodeDetectorData();
        
        
        
        PodeDetectionMethod method = new PodeDetectionMethod();
        method.setValue(PodeDetectionMethod.EnumType.unknown);
        detector.setDetectMethod(method);
        
        Position3D pos = new Position3D();
        pos.setLat(new Latitude(getLatitudeValue(latitude)));
        pos.setLon(new Longitude(getLongitudeValue(longitude)));
        detector.setPosition(pos);        
        
        detector.setDetectorID(rsl.getRouteId());
        
        PodeLaneData laneData = new PodeLaneData();
        
        PodeDataElementList laneDataList = createLaneDataList(rsl, messageType);
        
        laneData.setData(laneDataList);
        
        PodeLaneInfo laneInfo = new PodeLaneInfo();
        
        PodeLaneDirection direction = new PodeLaneDirection();
        Object o = rsl.getAny();
        if(o != null && o instanceof RouteStatusExt){
            RouteStatusExt ext = (RouteStatusExt)o;
            if("e".equalsIgnoreCase(ext.getRouteDirection())){
                direction.setValue(PodeLaneDirection.EnumType.east);
            } else if ("w".equalsIgnoreCase(ext.getRouteDirection())) {
                direction.setValue(PodeLaneDirection.EnumType.west);
            }
        }
        
        laneInfo.setLaneDirection(direction);
        
        //Hard coded for prototype
        laneInfo.setZoneNum(1);
        
        laneData.setLane(laneInfo);
        
        detector.setLaneData(laneData);
        data.selectDetector(detector);
        PodeDataRecord record = new PodeDataRecord();
        record.setPodeData(data);
        DateTimeZone dtz = rsl.getLastUpdateTime();
        
        DDateTime dateTime = getDDateTimeForDateTimeZone(dtz);
        record.setLastupdatetime(dateTime);
        
        record.setMeasduration(rsl.getEventDescriptionTime());
        
        record.setRoutename("I-66");
        record.setSource(source);
        
        PodeDataDistribution message = new PodeDataDistribution();
        message.setPodeData(record);
        
        
        PodeDialogID dialog = new PodeDialogID();
        dialog.setValue(PodeDialogID.EnumType.podeDataDistribution);
        message.setDialogID(dialog);
        
        message.setGroupID(serviceRequest.getGroupID());
        message.setRequestID(serviceRequest.getRequestID());
        SemiSequenceID seqId = new SemiSequenceID();
        seqId.setValue(SemiSequenceID.EnumType.data);
        message.setSeqID(seqId);        
        
        message.setMessageID(hash);
        return message;
        
    }

    
    private PodeDataElementList createLaneDataList(RouteStatusList lsl, int messageType) {
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

    private PodeDataElementList createTravelTimeMessage(RouteStatusList lsl) {
        PodeDataElementList laneDataList = createSpeedMessage(lsl);
        PodeTravelTime travelTime = new PodeTravelTime();
        int value = lsl.getTravelTime()/60;
        travelTime.setTravelTime(new DMinute(value));
        laneDataList.setTravelTimeInfo(travelTime);
        return laneDataList;
    }
    
    private PodeDataElementList createSpeedMessage(RouteStatusList lsl) {
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

    
    private PodeDataElementList createVolumeMessage(RouteStatusList lsl) {
        PodeDataElementList laneDataList = createSpeedMessage(lsl);
        if(lsl.getVolume() > 25){
            laneDataList.setVolume(25);
        }else if(lsl.getVolume() < 1){
            laneDataList.setVolume(1);
        }else {
            laneDataList.setVolume(lsl.getVolume().intValue());
        }        
        return laneDataList;
    }    
    
    private PodeDataElementList createOccupancyMessage(RouteStatusList lsl) {
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
