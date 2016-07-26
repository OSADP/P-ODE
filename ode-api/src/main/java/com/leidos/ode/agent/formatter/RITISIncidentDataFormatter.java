/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.formatter;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.ritis.RITISIncidentData;
import com.leidos.ode.data.Area;
import com.leidos.ode.data.DDateTime;
import com.leidos.ode.data.DDay;
import com.leidos.ode.data.DHour;
import com.leidos.ode.data.DMinute;
import com.leidos.ode.data.DMonth;
import com.leidos.ode.data.DSecond;
import com.leidos.ode.data.DYear;
import com.leidos.ode.data.IncidentEvent;
import com.leidos.ode.data.IncidentLane;
import com.leidos.ode.data.IncidentLocation;
import com.leidos.ode.data.IncidentType;
import com.leidos.ode.data.LaneStatus;
import com.leidos.ode.data.Latitude;
import com.leidos.ode.data.Longitude;
import com.leidos.ode.data.PodeDataDistribution;
import com.leidos.ode.data.PodeDataRecord;
import com.leidos.ode.data.PodeDialogID;
import com.leidos.ode.data.PodeIncidentData;
import com.leidos.ode.data.PodeLaneDirection;
import com.leidos.ode.data.PodeLaneType;
import com.leidos.ode.data.PodeSource;
import com.leidos.ode.data.Position3D;
import com.leidos.ode.data.RoadType;
import com.leidos.ode.data.SemiSequenceID;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.data.Sha256Hash;
import com.leidos.ode.util.ODEMessageType;

import edu.umd.cattlab.schema.atis.im.LaneDescription;
import edu.umd.cattlab.schema.atis.lrms.PointLocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.ritis.schema.atis_3_0_76.EventInformation;
import org.ritis.schema.atis_3_0_76.IncidentInformation;
import org.ritis.schema.atis_3_0_76.ResponseGroup;

/**
 *
 * @author Fang Zhou
 */
public class RITISIncidentDataFormatter extends ODEMessageFormatter{
	private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    

    @Override
    public Map<ODEMessageType, List<PodeDataDistribution>> formatMessage(ODEAgentMessage agentMessage, ServiceRequest serviceRequst) {
        Map<ODEMessageType, List<PodeDataDistribution>> retMap = new HashMap<ODEMessageType, List<PodeDataDistribution>>();
        RITISIncidentData ritisIncidentData = (RITISIncidentData)agentMessage.getFormattedMessage();
        
        if(ritisIncidentData.getAdvisoryInformation() == null
        	&& ritisIncidentData.getAdvisoryInformation().getResponseGroup() == null
        	&& ritisIncidentData.getAdvisoryInformation().getResponseGroup().size() == 0){
        	return retMap;            
        }             
        List<ResponseGroup> ritisItems = ritisIncidentData.getAdvisoryInformation().getResponseGroup();

        PodeSource source = new PodeSource();
        source.setValue(PodeSource.EnumType.ritis);

        List<PodeDataDistribution> eventANDincidentList = new ArrayList<PodeDataDistribution>();
        
             for(ResponseGroup item: ritisItems){
                 if(item.getIncident().size() != 0){
                	 List<IncidentInformation> incidentsInfo = item.getIncident();                	 
                	 for (IncidentInformation incidentInfo : incidentsInfo){
                		 eventANDincidentList.add(createMessage(agentMessage, incidentInfo, source, INCIDENT_MESSAGE, serviceRequst));
                	 }
                 }
                 if(item.getEvent().size() != 0){
                	 List<EventInformation> eventsInfo = item.getEvent();
                	 for (EventInformation eventInfo : eventsInfo){
                		 eventANDincidentList.add(createMessage(agentMessage, eventInfo, source, INCIDENT_MESSAGE, serviceRequst));
                	 }
                 }
             }
             logger.debug("RITIS message contained [" + eventANDincidentList.size() + "] Event and Incident Messages");
             retMap.put(ODEMessageType.INCIDENT, eventANDincidentList);
        
        return retMap;
    }
    
    private PodeDataDistribution createMessage(ODEAgentMessage agentMessage, IncidentInformation incidentInfo, PodeSource source, int messageType, ServiceRequest serviceRequst){
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
        PodeIncidentData incident = new PodeIncidentData();

        PointLocation pointLocation = incidentInfo.getLocation().getPointLocation();
        
        incident.setIssueAgency(incidentInfo.getHead().getIssuingAgency());
        incident.setEventID(incidentInfo.getHead().getId());
        List<JAXBElement<String>> itisOrText = incidentInfo.getDescription().getItisOrText();
        for (int i = 0; i < itisOrText.size(); i++){
        	if(itisOrText.get(i).getName().toString().equals("text")){
            	incident.setDescription(itisOrText.get(i).getValue());
            	break;
            } else {
            	incident.setDescription("No description");
            }
        }

        //incident location data: geoLocation, direction, area, rodeType;
        Position3D pos = new Position3D();
        pos.setLat(new Latitude(pointLocation.getCrossStreetsPoint().getGeoLocation().getLatitude()));
        pos.setLon(new Longitude(pointLocation.getCrossStreetsPoint().getGeoLocation().getLongitude()));

        PodeLaneDirection direction = checkDirection(pointLocation.getCrossStreetsPoint().getDirection());

        Area area = new Area();
        List<JAXBElement<? extends Serializable>> cityOrCountyOrState = pointLocation.getCrossStreetsPoint().getAdminAreas().getCityOrCountyOrState();
        for(int i = 0; i < cityOrCountyOrState.size(); i++){
        	if(area.getJurisdiction() == null
        			&& cityOrCountyOrState.get(i).getName().toString().equals("city")){
        		area.setJurisdiction(cityOrCountyOrState.get(i).getValue().toString() + " (city)");
        	}else if(area.getJurisdiction() == null
        			&& cityOrCountyOrState.get(i).getName().toString().equals("county")){
        		area.setJurisdiction(cityOrCountyOrState.get(i).getValue().toString() + " (county)");
        	}else if(cityOrCountyOrState.get(i).getName().toString().equals("state")){
        		area.setState(cityOrCountyOrState.get(i).getValue().toString());
        	}
        }

        RoadType roadType = new RoadType();
        List<String> streetPrefix = pointLocation.getCrossStreetsPoint().getOnStreetInfo().getPrefix();
        if(streetPrefix != null && streetPrefix.size() != 0){
            roadType = checkRoadType(streetPrefix.get(0)); 
        } else {
            roadType.setValue(RoadType.EnumType.other);
        }
                       
        IncidentLocation incidentLoc = new IncidentLocation();
        incidentLoc.setGeoLocation(pos);
        incidentLoc.setDirection(direction);
        incidentLoc.setArea(area);
        if(roadType != null) {
            incidentLoc.setRoadType(roadType);
        }
        
        incident.setLocation(incidentLoc);
        
        //IncidentEvent: IncidentType
        IncidentType incidentType = new IncidentType();
        incidentType = checkEventType(incidentInfo.getTypeEvent().getEventType().getName());
        
        IncidentEvent incidentEvent = new IncidentEvent();
        incidentEvent.setIncidentType(incidentType);
             
        incident.setEvent(incidentEvent);
        
        data.selectIncident(incident);
        
        PodeDataRecord record = new PodeDataRecord();
        record.setPodeData(data);
        
        DDateTime dateTime = getDateTimeForElement(incidentInfo.getHead().getUpdateTime());
        record.setLastupdatetime(dateTime);
        //set the label name for VDOT Incident Data
        record.setRoutename("RITISIncident");
        record.setSource(source);
        
        message.setPodeData(record);
        
        return message;      
    }    
    
    private PodeDataDistribution createMessage(ODEAgentMessage agentMessage, EventInformation eventInfo, PodeSource source, int messageType, ServiceRequest serviceRequst){
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
        PodeIncidentData event = new PodeIncidentData();

        PointLocation pointLocation = eventInfo.getLocation().getPointLocation();
        
        event.setIssueAgency(eventInfo.getHead().getIssuingAgency());
        event.setEventID(eventInfo.getHead().getId());
        List<JAXBElement<String>> itisOrText = eventInfo.getDescription().getItisOrText();
        for (int i = 0; i < itisOrText.size(); i++){
        	if(itisOrText.get(i).getName().toString().equals("text")){
        		event.setDescription(itisOrText.get(i).getValue());
            	break;
            } else {
            	event.setDescription("No description");
            }
        }
        
        //incident location data: geoLocation, direction, area, rodeType;                             
        Position3D pos = new Position3D();
        pos.setLat(new Latitude(pointLocation.getCrossStreetsPoint().getGeoLocation().getLatitude()));
        pos.setLon(new Longitude(pointLocation.getCrossStreetsPoint().getGeoLocation().getLongitude()));
        
        PodeLaneDirection direction = checkDirection(pointLocation.getCrossStreetsPoint().getDirection());
        
        Area area = new Area();
        List<JAXBElement<? extends Serializable>> cityOrCountyOrState = pointLocation.getCrossStreetsPoint().getAdminAreas().getCityOrCountyOrState();
        for(int i = 0; i < cityOrCountyOrState.size(); i++){
        	if(area.getJurisdiction() == null 
        			&& cityOrCountyOrState.get(i).getName().toString().equals("city")){
        		area.setJurisdiction(cityOrCountyOrState.get(i).getValue().toString() + " (city)");
        	}else if(area.getJurisdiction() == null 
        			&& cityOrCountyOrState.get(i).getName().toString().equals("county")){
        		area.setJurisdiction(cityOrCountyOrState.get(i).getValue().toString() + " (county)");
        	}else if(cityOrCountyOrState.get(i).getName().toString().equals("state")){
        		area.setState(cityOrCountyOrState.get(i).getValue().toString());
        	}
        }
        
        RoadType roadType = new RoadType();
        List<String> streetPrefix = pointLocation.getCrossStreetsPoint().getOnStreetInfo().getPrefix();
        if(streetPrefix != null && streetPrefix.size() != 0){
            roadType = checkRoadType(streetPrefix.get(0));
        } else {
            roadType.setValue(RoadType.EnumType.other);
        }
        
        List<LaneDescription> lanesInfoList = eventInfo.getAffectedLanes();
        List<IncidentLane> lanesAffectedList = new ArrayList<IncidentLane>();
        IncidentLane laneAffected;
        int laneAffectedCnt = 0;
        int laneTotalCnt = 0;
        if(lanesInfoList != null && lanesInfoList.size() != 0){
        	for(int i = 0; i < lanesInfoList.size(); i++){
        		LaneDescription laneInfo = lanesInfoList.get(i);

        		laneAffectedCnt = laneAffectedCnt + laneInfo.getLanesAffected();
        		laneTotalCnt = laneTotalCnt + laneInfo.getLaneTotalCnt();
                int lanTypeCnt = laneInfo.getTypes().getType().size();
        		for(int j = 0; j < laneInfo.getLanesAffected(); j++) {
                    //If the number of affected lanes is larger than 3, RITIS Incident XML file
                    //will combine the LaneTypes of the center lanes as "middle lanes",
                    //meaning the list of LaneTypes only have 3 items.
                    laneAffected = new IncidentLane();
                    if (j == 0) {
                        laneAffected.setLaneType(checkLaneType(laneInfo.getTypes().getType().get(0)));
                    } else if (j == lanesInfoList.size() - 1){
                        laneAffected.setLaneType(checkLaneType(laneInfo.getTypes().getType().get(lanTypeCnt - 1)));
                    } else {
                        laneAffected.setLaneType(checkLaneType(laneInfo.getTypes().getType().get(1)));
                    }
                    laneAffected.setDirection(checkDirection(laneInfo.getLocation()));
                    laneAffected.setStatus(checkLaneStatus(laneInfo.getCondition()));
                    lanesAffectedList.add(j,laneAffected);
                }
        	}

        }
        
        IncidentLocation eventLoc = new IncidentLocation();
        eventLoc.setGeoLocation(pos);
        eventLoc.setDirection(direction);
        eventLoc.setArea(area);
        if(roadType != null) {
            eventLoc.setRoadType(roadType);
        }
        if(lanesAffectedList.size() > 0){
        	eventLoc.setLaneCount(laneTotalCnt);
            eventLoc.setNoOfLanesAffected(laneAffectedCnt);
            eventLoc.setLanesAffected(lanesAffectedList);
        }
               
        event.setLocation(eventLoc);
        
        //IncidentEvent: IncidentType
        IncidentType incidentType = new IncidentType();
        incidentType = checkEventType(eventInfo.getTypeEvent().getEventType().getName());
        
        IncidentEvent incidentEvent = new IncidentEvent();
        incidentEvent.setIncidentType(incidentType);
                     
        event.setEvent(incidentEvent);
        
        data.selectIncident(event);
        
        PodeDataRecord record = new PodeDataRecord();
        record.setPodeData(data);
        
        DDateTime dateTime = getDateTimeForElement(eventInfo.getHead().getUpdateTime());
        record.setLastupdatetime(dateTime);
        //set the label name for VDOT Event Data
        record.setRoutename("RITISIncident");
        record.setSource(source);
        
        message.setPodeData(record);
        
        return message;      
    }    
    
    private IncidentType checkEventType(QName eventName){
    	IncidentType eventType = new IncidentType();

    	switch (eventName.toString()) {
        case "accidentsAndIncidents":
        	eventType.setValue(IncidentType.EnumType.vehicleAccident);
            break;
        case "trafficConditions":
        	eventType.setValue(IncidentType.EnumType.congestion_Delay);
            break;
        case "roadwork":
        	eventType.setValue(IncidentType.EnumType.roadWork);
            break;
        case "vehicleGroupAffected":
        	eventType.setValue(IncidentType.EnumType.disabledVehicle);
            break;
        default:
        	eventType.setValue(IncidentType.EnumType.other);
            break;
    	}   	
    	return eventType;
    }
    
     //identify the road type
     private RoadType checkRoadType(String roadType){
     	RoadType myRoadType = new RoadType();
     	if(roadType.equalsIgnoreCase("I")){
     		myRoadType.setValue(RoadType.EnumType.interstate);
         }else{
         	myRoadType.setValue(RoadType.EnumType.other);
     	}
     	return myRoadType;
     }
     
     //identify the lane direction
     private PodeLaneDirection checkDirection(String laneDirection){
     	PodeLaneDirection myDirection = new PodeLaneDirection();
     	if(laneDirection.equalsIgnoreCase("east")){
     		myDirection.setValue(PodeLaneDirection.EnumType.east);
         }else if(laneDirection.equalsIgnoreCase("west")){
         	myDirection.setValue(PodeLaneDirection.EnumType.west);
         }else if(laneDirection.equalsIgnoreCase("south")){
         	myDirection.setValue(PodeLaneDirection.EnumType.south);
         }else if(laneDirection.equalsIgnoreCase("north")){
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
     	}else if(laneType.equalsIgnoreCase("shoulder lane")){
     		myLaneType.setValue(PodeLaneType.EnumType.shoulder);
     	}else if(laneType.equalsIgnoreCase("left shoulder")){
     		myLaneType.setValue(PodeLaneType.EnumType.left_shoulder);
     	}else if(laneType.equalsIgnoreCase("left lane")){
     		myLaneType.setValue(PodeLaneType.EnumType.left);
     	}else if(laneType.equalsIgnoreCase("left center lane")){
     		myLaneType.setValue(PodeLaneType.EnumType.left_center);
     	}else if(laneType.equalsIgnoreCase("middle lanes")){
     		myLaneType.setValue(PodeLaneType.EnumType.center);
     	}else if(laneType.equalsIgnoreCase("right center lane")){
     		myLaneType.setValue(PodeLaneType.EnumType.right_center);
     	}else if(laneType.equalsIgnoreCase("right lane")){
     		myLaneType.setValue(PodeLaneType.EnumType.right);
     	}else if(laneType.equalsIgnoreCase("right shoulder")){
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
     
    private DDateTime getDateTimeForElement(XMLGregorianCalendar calendar){
               
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
