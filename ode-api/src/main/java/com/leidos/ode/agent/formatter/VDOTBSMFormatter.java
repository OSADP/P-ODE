/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.vdotbsm.VDOTBSMData;
import com.leidos.ode.agent.data.vdotbsm.VDOTBSMList;
import com.leidos.ode.data.*;
import com.leidos.ode.util.ODEMessageType;

import javax.xml.bind.DatatypeConverter;
import java.sql.Timestamp;
import java.util.*;

/**
 *
 * @author cassadyja
 */
public class VDOTBSMFormatter extends ODEMessageFormatter{
    private final String TAG = getClass().getSimpleName();
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TAG);

    @Override
    public Map<ODEMessageType, List<PodeDataDistribution>> formatMessage(ODEAgentMessage agentMessage, ServiceRequest serviceRequst) {
        Map<ODEMessageType, List<PodeDataDistribution>> retMap = new HashMap<ODEMessageType, List<PodeDataDistribution>>();
        logger.debug("Formatting VDOT BSMs");
        if(agentMessage.getFormattedMessage() != null){
                
            VDOTBSMList bsmList = (VDOTBSMList)agentMessage.getFormattedMessage();
            Sha256Hash hash = new Sha256Hash(DatatypeConverter.parseHexBinary(agentMessage.getMessageId()));            
            List<PodeDataDistribution> speedList = new ArrayList<PodeDataDistribution>();

            for(VDOTBSMData data:bsmList.getBsm()){
                speedList.add(createMessage(data, hash, serviceRequst));
            }
            logger.debug("Found "+speedList.size()+" Speed messages in BSMs");
            retMap.put(ODEMessageType.SPEED, speedList);
            return retMap;
        }else{
            return null;
        }
    }
    
    
    private PodeDataDistribution createMessage(VDOTBSMData bsm, Sha256Hash hash, ServiceRequest serviceRequst){
        PodeDataDistribution message = new PodeDataDistribution();
        
        
        PodeDialogID dialog = new PodeDialogID();
        dialog.setValue(PodeDialogID.EnumType.podeDataDistribution);
        message.setDialogID(dialog);
        
        message.setGroupID(serviceRequst.getGroupID());
        message.setRequestID(serviceRequst.getRequestID());
        SemiSequenceID seqId = new SemiSequenceID();
        seqId.setValue(SemiSequenceID.EnumType.data);
        message.setSeqID(seqId);             
        
        PodeSource source = new PodeSource();
        source.setValue(PodeSource.EnumType.vdot);
        
        message.setMessageID(hash);
        PodeDataRecord.PodeDataChoiceType data = new PodeDataRecord.PodeDataChoiceType();
        PodeDetectorData detector = new PodeDetectorData();
        
        Position3D position = new Position3D();
        position.setLat(new Latitude(getLatitudeValue(bsm.getLatitude())));
        position.setLon(new Longitude(getLongitudeValue(bsm.getLongitude())));
        
        detector.setPosition(position);
        
        
        PodeDetectionMethod method = new PodeDetectionMethod();
        method.setValue(PodeDetectionMethod.EnumType.vehicleProbe);
        detector.setDetectMethod(method);
        detector.setDetectorID(bsm.getRseID()+"");

        PodeLaneData laneData = new PodeLaneData();

        
        PodeDataElementList laneDataList = new PodeDataElementList();
        Speed speed = new Speed((int)Math.round(bsm.getSpeed()));
        laneDataList.setSpeed(speed);        
        laneData.setData(laneDataList);

        PodeLaneInfo laneInfo = new PodeLaneInfo();

        PodeLaneDirection direction = new PodeLaneDirection();
        //For Prototype only concerned with East or West.
        if(bsm.getHeading() >=0 && bsm.getHeading() < 180){
            direction.setValue(PodeLaneDirection.EnumType.east);
        }else{
            direction.setValue(PodeLaneDirection.EnumType.west);
        }
        laneInfo.setLaneDirection(direction);
        //hardcoded for Prototype.
        laneInfo.setZoneNum(1);

        laneData.setLane(laneInfo);

        List<PodeLaneData> dataList = new ArrayList<>();
        dataList.add(laneData);
        detector.setLaneData(dataList);

        data.selectDetector(detector);

        
        PodeDataRecord record = new PodeDataRecord();
        record.setPodeData(data);
        
        DDateTime dateTime = getDateTimeForTimeStamp(new Timestamp(System.currentTimeMillis()));
        record.setLastupdatetime(dateTime);

        
        
        record.setRoutename(bsm.getRseID()+"");
        record.setSource(source);
        
        
        message.setPodeData(record);
        
        return message;
        
    }

    
    
    private DDateTime getDateTimeForTimeStamp(Timestamp dateReceived) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateReceived);
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
