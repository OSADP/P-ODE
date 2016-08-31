/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.ritis.RITISSpeedData;
import com.leidos.ode.data.*;
import com.leidos.ode.util.ODEMessageType;
import org.apache.log4j.Logger;
import org.ritis.schema.tmdd_0_0_0.DateTimeZone;
import org.ritis.schema.tmdd_0_0_0.ZoneDataCollectionPeriodRITIS;
import org.ritis.schema.tmdd_0_0_0.ZoneDataRITIS;
import org.ritis.schema.tmdd_0_0_0.ZoneReportRITIS;

import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author cassadyja
 */
public class RITISSpeedMessageFormatter extends ODEMessageFormatter{
    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);

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

        List<PodeDataDistribution> speedList = new ArrayList<PodeDataDistribution>();
        List<PodeDataDistribution> volumeList = new ArrayList<PodeDataDistribution>();
        List<PodeDataDistribution> occList = new ArrayList<PodeDataDistribution>();
        for(ZoneDataCollectionPeriodRITIS item: ritisItems){
            if(item.getZoneReports() != null && item.getZoneReports().getZoneReport() != null){
                List<ZoneReportRITIS> zoneReports = item.getZoneReports().getZoneReport();
                for(ZoneReportRITIS zoneReport: zoneReports){
                    
                    if(zoneReport.getZoneData() != null && zoneReport.getZoneData().getZoneDataItem() != null){
                        for(ZoneDataRITIS zoneData:zoneReport.getZoneData().getZoneDataItem()){
                            speedList.add(createMessage(agentMessage, item, zoneReport, zoneData, source, SPEED_MESSAGE, serviceRequst));
                            volumeList.add(createMessage(agentMessage, item, zoneReport, zoneData, source, VOLUME_MESSAGE, serviceRequst));
                            occList.add(createMessage(agentMessage, item, zoneReport, zoneData, source, OCCUPANCY_MESSAGE, serviceRequst));
                        }
                    }
                }
            }
        }
        logger.debug("RITIS message contained ["+speedList.size()+"] Speed Messages");
        messages.put(ODEMessageType.SPEED,speedList);
        logger.debug("RITIS message contained ["+volumeList.size()+"] Volume Messages");        
        messages.put(ODEMessageType.VOLUME,volumeList);
        logger.debug("RITIS message contained ["+occList.size()+"] Occupancy Messages");        
        messages.put(ODEMessageType.OCCUPANCY,occList);

        
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

        List<PodeLaneData> dataList = new ArrayList<>();
        dataList.add(laneData);
        detector.setLaneData(dataList);
        data.selectDetector(detector);

        
        PodeDataRecord record = new PodeDataRecord();
        record.setPodeData(data);

        DDateTime dateTime = getDateTimeForElement(item);
        record.setLastupdatetime(dateTime);


        if(item.getMeasurementDuration() != null){
            record.setMeasduration(item.getMeasurementDuration().intValue());
        }
        
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
        
        int value = zoneData.getZoneVehicleCount().intValue();
        if(value > 2500){
            value = 2500;
        }else if (value < 1){
            value = 1;
        }
        laneDataList.setVolume(value);
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
