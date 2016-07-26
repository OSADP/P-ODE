/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.vdot.VDOTIncidentData;
import com.leidos.ode.agent.data.vdot.VDOTIncidentData.VDOTIncidentLane;
import com.leidos.ode.data.*;
import com.leidos.ode.util.ODEMessageType;
import org.apache.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.util.*;

/**
 *
 * @author Fang Zhou
 */
public class VDOTIncidentMessageFormatter  extends ODEMessageFormatter{

	private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    
    @Override
    public Map<ODEMessageType, List<PodeDataDistribution>> formatMessage(ODEAgentMessage agentMessage, ServiceRequest serviceRequst) {
    	Map<ODEMessageType,List<PodeDataDistribution>> messages = new HashMap<ODEMessageType,List<PodeDataDistribution>>();
    	VDOTIncidentData vdotIncidentData = (VDOTIncidentData)agentMessage.getFormattedMessage();
    	
    	PodeSource source = new PodeSource();
        source.setValue(PodeSource.EnumType.vdot);
        
        if(vdotIncidentData.getIncidentDataElements()!= null){
            List<PodeDataDistribution> incidentList = new ArrayList<PodeDataDistribution>();
            
            for(VDOTIncidentData.VDOTIncidentDataElement element: vdotIncidentData.getIncidentDataElements()){
            	incidentList.add(createMessage(agentMessage, element, source, INCIDENT_MESSAGE,serviceRequst));
            }
            logger.debug("VDOT message contained ["+incidentList.size()+"] Incident Messages");
            messages.put(ODEMessageType.INCIDENT,incidentList);
            
        }
        
        return messages;
    }
    
    private PodeDataDistribution createMessage(ODEAgentMessage agentMessage, VDOTIncidentData.VDOTIncidentDataElement element, PodeSource source, int messageType, ServiceRequest serviceRequst){
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
        PodeIncidentData incident = new PodeIncidentData();

        incident.setIssueAgency(element.getIssueAgency());
        incident.setEventID(element.getIncidentID());
        incident.setDescription(element.getDescription());
        
        //incident location data: geoLocation, direction, area, rodeType;                             
        Position3D pos = new Position3D();
        pos.setLat(new Latitude(getLatitudeValue(element.getGeometry()[0])));
        pos.setLon(new Longitude(getLongitudeValue(element.getGeometry()[1])));
        
        PodeLaneDirection direction = new PodeLaneDirection();
        direction = checkDirection(element.getTravelDirection());
        
        Area area = new Area();
        area.setJurisdiction(element.getJurisdiction());
        if(element.getRegion()!= null){
        	area.setRegion(element.getRegion());
        }
        if(element.getState()!= null){
        	area.setRegion(element.getState());
        }
        
        RoadType roadType = new RoadType();
        roadType = checkRoadType(element.getRoadType()); 
        
        IncidentLane laneInfo = new IncidentLane();
        List<IncidentLane> lanesAffectedList = new ArrayList<IncidentLane>();
        VDOTIncidentLane currentLane = new VDOTIncidentLane();
        for(int i = 0; i < element.getLanesAffected().size(); i++){
        	currentLane = element.getLanesAffected().get(i);
        	laneInfo.setDirection(checkDirection(currentLane.getLaneDirection()));
        	laneInfo.setLaneType(checkLaneType(currentLane.getLaneType()));
        	laneInfo.setStatus(checkLaneStatus(currentLane.getLaneStatus()));
        	lanesAffectedList.add(laneInfo);
        }      
        
        IncidentLocation incidentLoc = new IncidentLocation();
        incidentLoc.setGeoLocation(pos);
        incidentLoc.setDirection(direction);
        incidentLoc.setArea(area);
        incidentLoc.setRoadType(roadType);
        incidentLoc.setLaneCount(element.getLaneCount());
        incidentLoc.setNoOfLanesAffected(clamp(element.getNoOfLanesAffected(), 1, 20));
        if (lanesAffectedList.size() > 0) {
            incidentLoc.setLanesAffected(lanesAffectedList);
        }
        incident.setLocation(incidentLoc);
        
        //IncidentEvent: IncidentType, delayType, delayMiles
        IncidentType incidentType = new IncidentType();
        if(element.getIncidentType().equalsIgnoreCase("Congestion/Delay")){
        	incidentType.setValue(IncidentType.EnumType.congestion_Delay);
        }else if(element.getIncidentType().equalsIgnoreCase("Disabled Vehicle")){
        	incidentType.setValue(IncidentType.EnumType.disabledVehicle);
        }else if(element.getIncidentType().equalsIgnoreCase("Vehicle Accident")){
        	incidentType.setValue(IncidentType.EnumType.vehicleAccident);
        }else if(element.getIncidentType().equalsIgnoreCase("RoadWork")){
        	incidentType.setValue(IncidentType.EnumType.roadWork);
        }else{
        	incidentType.setValue(IncidentType.EnumType.other);
        }
        
        DelayType delayType = new DelayType();
        if(element.getDelayType().equalsIgnoreCase("Potential")){
        	delayType.setValue(DelayType.EnumType.potential);
        }else if(element.getDelayType().equalsIgnoreCase("Minor")){
        	delayType.setValue(DelayType.EnumType.minor);
        }else if(element.getDelayType().equalsIgnoreCase("Major")){
        	delayType.setValue(DelayType.EnumType.major);
        }else{
        	delayType.setValue(DelayType.EnumType.other);
        }
        
        IncidentEvent incidentEvent = new IncidentEvent();
        incidentEvent.setIncidentType(incidentType);
        incidentEvent.setDelayType(delayType);
        incidentEvent.setDelayMiles(element.getDelayMiles());
             
        incident.setEvent(incidentEvent);
        
        data.selectIncident(incident);
        
        PodeDataRecord record = new PodeDataRecord();
        record.setPodeData(data);
        
        DDateTime dateTime = getDateTimeForElement(element);
        record.setLastupdatetime(dateTime);
        //set the label name fro VDOT Incident Data
        record.setRoutename("VDOTIncident");
        record.setSource(source);
        
        message.setPodeData(record);
        
        return message;      
    }
    
    //identify the road type
    private RoadType checkRoadType(String roadType){
    	RoadType myRoadType = new RoadType();
    	if(roadType.equalsIgnoreCase("Interstate")){
    		myRoadType.setValue(RoadType.EnumType.interstate);
        }else{
        	myRoadType.setValue(RoadType.EnumType.other);
    	}
    	return myRoadType;
    }
    
    //identify the lane direction
    private PodeLaneDirection checkDirection(String laneDirection){
    	PodeLaneDirection myDirection = new PodeLaneDirection();
    	if(laneDirection.equalsIgnoreCase("E")){
    		myDirection.setValue(PodeLaneDirection.EnumType.east);
        }else if(laneDirection.equalsIgnoreCase("W")){
        	myDirection.setValue(PodeLaneDirection.EnumType.west);
        }else if(laneDirection.equalsIgnoreCase("S")){
        	myDirection.setValue(PodeLaneDirection.EnumType.south);
        }else if(laneDirection.equalsIgnoreCase("N")){
        	myDirection.setValue(PodeLaneDirection.EnumType.north);
        }else{
        	//??? do not know why the default is east
        	myDirection.setValue(PodeLaneDirection.EnumType.east);
        }
    	return myDirection;
    }
    
    //identify the lane type
    private PodeLaneType checkLaneType(String laneType){
    	PodeLaneType myLaneType = new PodeLaneType();
    	if(laneType.equalsIgnoreCase("unknown")){
    		myLaneType.setValue(PodeLaneType.EnumType.unknown);
    	}else if(laneType.equalsIgnoreCase("offRamp")){
    		myLaneType.setValue(PodeLaneType.EnumType.offRamp);
    	}else if(laneType.equalsIgnoreCase("onRamp")){
    		myLaneType.setValue(PodeLaneType.EnumType.onRamp);
    	}else if(laneType.equalsIgnoreCase("normal")){
    		myLaneType.setValue(PodeLaneType.EnumType.normal);
    	}else if(laneType.equalsIgnoreCase("shoulder_lane")){
    		myLaneType.setValue(PodeLaneType.EnumType.shoulder);
    	}else if(laneType.equalsIgnoreCase("left_shoulder")){
    		myLaneType.setValue(PodeLaneType.EnumType.left_shoulder);
    	}else if(laneType.equalsIgnoreCase("left_lane")){
    		myLaneType.setValue(PodeLaneType.EnumType.left);
    	}else if(laneType.equalsIgnoreCase("left_center_lane")){
    		myLaneType.setValue(PodeLaneType.EnumType.left_center);
    	}else if(laneType.equalsIgnoreCase("center_lane")){
    		myLaneType.setValue(PodeLaneType.EnumType.center);
    	}else if(laneType.equalsIgnoreCase("right_center_lane")){
    		myLaneType.setValue(PodeLaneType.EnumType.right_center);
    	}else if(laneType.equalsIgnoreCase("right_lane")){
    		myLaneType.setValue(PodeLaneType.EnumType.right);
    	}else if(laneType.equalsIgnoreCase("right_shoulder")){
    		myLaneType.setValue(PodeLaneType.EnumType.right_shoulder);
    	} else {
    		myLaneType.setValue(PodeLaneType.EnumType.other);
    	}  	
    	return myLaneType;
    }
    
    //identify the lane status
    private LaneStatus checkLaneStatus(String laneStatus){
    	LaneStatus myLaneStatus = new LaneStatus();
    	if(laneStatus.equalsIgnoreCase("open")){
    		myLaneStatus.setValue(LaneStatus.EnumType.open);
    	}else if(laneStatus.equalsIgnoreCase("closed")){
    		myLaneStatus.setValue(LaneStatus.EnumType.closed);
    	}
    	return myLaneStatus;
    }
    
    private DDateTime getDateTimeForElement(VDOTIncidentData.VDOTIncidentDataElement element){
        Date date = element.getLastTimeUpdated();
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


    /**
     * Clamp the value between a specified range
     * @param val The value to be clamped
     * @param min The minimum value
     * @param max The maximum value
     * @return A value guaranteed to be in the range of [min, max]
     */
    private int clamp(int val, int min, int max ) {
        return Math.max(Math.min(val, max), min);
    }
}
