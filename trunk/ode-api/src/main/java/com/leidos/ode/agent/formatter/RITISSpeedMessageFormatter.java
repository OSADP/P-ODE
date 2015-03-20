/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.ritis.RITISSpeedData;
import com.leidos.ode.data.DDateTime;
import com.leidos.ode.data.DDay;
import com.leidos.ode.data.DHour;
import com.leidos.ode.data.DMinute;
import com.leidos.ode.data.DMonth;
import com.leidos.ode.data.DSecond;
import com.leidos.ode.data.DYear;
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
import com.leidos.ode.data.SemiSequenceID;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.data.Sha256Hash;
import com.leidos.ode.data.Speed;
import com.leidos.ode.util.ODEMessageType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.XMLGregorianCalendar;
import org.ritis.schema.tmdd_0_0_0.DateTimeZone;
import org.ritis.schema.tmdd_0_0_0.ZoneDataCollectionPeriodRITIS;
import org.ritis.schema.tmdd_0_0_0.ZoneDataRITIS;
import org.ritis.schema.tmdd_0_0_0.ZoneReportRITIS;


/**
 *
 * @author cassadyja
 */
public class RITISSpeedMessageFormatter extends ODEMessageFormatter{

    private RITISZoneDirectionData ritisZoneDirData = new RITISZoneDirectionData();
    
    public Map<ODEMessageType,List<PodeDataDistribution>> formatMessage(ODEAgentMessage agentMessage, ServiceRequest serviceRequst) {
        Map<ODEMessageType,List<PodeDataDistribution>> messages = new HashMap<ODEMessageType,List<PodeDataDistribution>>();
        RITISSpeedData ritisSpeedData = (RITISSpeedData)agentMessage.getFormattedMessage();
        if(ritisSpeedData.getZoneDetectorDataRITIS() == null 
           || ritisSpeedData.getZoneDetectorDataRITIS().getCollectionPeriod() == null
           || ritisSpeedData.getZoneDetectorDataRITIS().getCollectionPeriod().getCollectionPeriodItem() == null
           || ritisSpeedData.getZoneDetectorDataRITIS().getCollectionPeriod().getCollectionPeriodItem().size() ==0){
            
                return messages;            
        }
        List<ZoneDataCollectionPeriodRITIS> ritisItems = ritisSpeedData.getZoneDetectorDataRITIS().getCollectionPeriod().getCollectionPeriodItem();
        
        PodeSource source = new PodeSource();
        source.setValue(PodeSource.EnumType.ritis);
        
        for(ZoneDataCollectionPeriodRITIS item: ritisItems){
            if(item.getZoneReports() != null && item.getZoneReports().getZoneReport() != null){
                List<ZoneReportRITIS> zoneReports = item.getZoneReports().getZoneReport();
                for(ZoneReportRITIS zoneReport: zoneReports){
                    
                    if(zoneReport.getZoneData() != null && zoneReport.getZoneData().getZoneDataItem() != null){
                        for(ZoneDataRITIS zoneData:zoneReport.getZoneData().getZoneDataItem()){
                            List<PodeDataDistribution> list = new ArrayList<PodeDataDistribution>();
                            list.add(createMessage(agentMessage, item, zoneReport, zoneData, source, SPEED_MESSAGE, serviceRequst));
                            messages.put(ODEMessageType.SPEED,list);
                            list = new ArrayList<PodeDataDistribution>();
                            list.add(createMessage(agentMessage, item, zoneReport, zoneData, source, VOLUME_MESSAGE, serviceRequst));
                            messages.put(ODEMessageType.VOLUME,list);
                            list = new ArrayList<PodeDataDistribution>();
                            list.add(createMessage(agentMessage, item, zoneReport, zoneData, source, OCCUPANCY_MESSAGE, serviceRequst));
                            messages.put(ODEMessageType.OCCUPANCY,list);
                        }
                    }
                }
            }
        }
        
        return messages;
    }
    
    
    
    private PodeDataDistribution createMessage(ODEAgentMessage agentMessage, ZoneDataCollectionPeriodRITIS item, ZoneReportRITIS zoneReport, ZoneDataRITIS zoneData, PodeSource source, int messageType, ServiceRequest serviceRequst){
        PodeDataDistribution message = new PodeDataDistribution();
        Sha256Hash hash = new Sha256Hash(DatatypeConverter.parseHexBinary(agentMessage.getMessageId()));
        message.setMessageID(hash);
        
        
        PodeDialogID dialog = new PodeDialogID();
        dialog.setValue(PodeDialogID.EnumType.podeDataDistribution);
        message.setDialogID(dialog);
        
        message.setGroupID(serviceRequst.getGroupID());
        message.setRequestID(serviceRequst.getRequestID());
        SemiSequenceID seqId = new SemiSequenceID();
        seqId.setValue(SemiSequenceID.EnumType.data);
        message.setSeqID(seqId);        
        
        PodeDataRecord.PodeDataChoiceType data = new PodeDataRecord.PodeDataChoiceType();
        PodeDetectorData detector = new PodeDetectorData();

        PodeDetectionMethod method = new PodeDetectionMethod();
        method.setValue(PodeDetectionMethod.EnumType.unknown);
        detector.setDetectMethod(method);
        detector.setDetectorID(zoneReport.getDetectorId());

        PodeLaneData laneData = new PodeLaneData();

        
        PodeDataElementList laneDataList = null;
        
        if(messageType == SPEED_MESSAGE){
            laneDataList = createSpeedDataElementList(zoneData);
        }else if(messageType == VOLUME_MESSAGE){
            laneDataList = createVolumeDataElementList(zoneData);
        }else{
            laneDataList = createOccupancyDataElementList(zoneData);
        }
        
        laneData.setData(laneDataList);

        PodeLaneInfo laneInfo = new PodeLaneInfo();

        PodeLaneDirection direction = new PodeLaneDirection();
        
        if(ritisZoneDirData.isInDirectionOfTravelRITIS(zoneData.getZoneNumber(), "E") > 0){
            direction.setValue(PodeLaneDirection.EnumType.east);
        }else{
            direction.setValue(PodeLaneDirection.EnumType.west);
        }
        laneInfo.setLaneDirection(direction);
        
        laneInfo.setZoneNum(zoneData.getZoneNumber());

        laneData.setLane(laneInfo);

        detector.setLaneData(laneData);

        data.selectDetector(detector);

        
        PodeDataRecord record = new PodeDataRecord();
        record.setPodeData(data);

        DDateTime dateTime = getDateTimeForElement(item);
        record.setLastupdatetime(dateTime);


        record.setMeasduration(item.getMeasurementDuration().intValue());
        record.setRoutename("I-66");
        record.setSource(source);

        message.setPodeData(record);

        return message;
        
    }    
    
    private PodeDataElementList createSpeedDataElementList(ZoneDataRITIS zoneData){
        PodeDataElementList laneDataList = new PodeDataElementList();
        //speed in mph
        int normalSpeed = zoneData.getZoneVehicleSpeed();
        //convert to meter per second
        double meterSecond = normalSpeed*.44704;
        //convert to .02 meter per second as per SAE J2735
        double saeSpeed = meterSecond / .02;
        
        Speed speed = new Speed((int)Math.round(saeSpeed));
        laneDataList.setSpeed(speed);
        
        return laneDataList;        
        
    }
    
     private PodeDataElementList createOccupancyDataElementList(ZoneDataRITIS zoneData){
        PodeDataElementList laneDataList = createSpeedDataElementList(zoneData);
        laneDataList.setOccupancy(zoneData.getOccupancy().intValue());
        return laneDataList;        
     }
    
     private PodeDataElementList createVolumeDataElementList(ZoneDataRITIS zoneData){
        PodeDataElementList laneDataList = createSpeedDataElementList(zoneData);
        laneDataList.setVolume(zoneData.getZoneVehicleCount().intValue());
        return laneDataList;        
    }

     
     
    private DDateTime getDateTimeForElement(ZoneDataCollectionPeriodRITIS item){
        DateTimeZone dtz = item.getDetectionTimeStamp();
        XMLGregorianCalendar calendar = dtz.getValue();
        
        
        DDateTime dateTime = new DDateTime();
        DHour hour = new DHour(calendar.getHour());
        dateTime.setHour(hour);
        DMinute min = new DMinute(calendar.getMinute());
        dateTime.setMinute(min);
        DSecond sec = new DSecond(calendar.getSecond());
        dateTime.setSecond(sec);
        DMonth month = new DMonth(calendar.getMonth());
        dateTime.setMonth(month);
        DDay day = new DDay(calendar.getDay());
        dateTime.setDay(day);
        DYear year = new DYear(calendar.getYear());
        dateTime.setYear(year);
        return dateTime;
    }
    
    
}
