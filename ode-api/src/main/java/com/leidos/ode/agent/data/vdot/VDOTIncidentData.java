package com.leidos.ode.agent.data.vdot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VDOTIncidentData implements Serializable {
		
	private List<VDOTIncidentDataElement> vdotIncidentDataElements;

    public VDOTIncidentData() {
    	vdotIncidentDataElements = new ArrayList<VDOTIncidentDataElement>();
    }

    public List<VDOTIncidentDataElement> getIncidentDataElements() {
        return vdotIncidentDataElements;
    }

    public void setVdotIncidentDataElements(List<VDOTIncidentDataElement> vdotIncidentDataElements) {
        this.vdotIncidentDataElements = vdotIncidentDataElements;
    }

    public static class VDOTIncidentDataElement {
    	private Date lastTimeUpdated;
    	private String issueAgency;
    	private String incidentID;
    	private String description;
    	//location
    	private float[] geometry;			//location of the Incident
    	private String travelDirection;	//direction of travel affected by the Incident 
    	//incidentArea: jurisdiction, region -OPTIONAL, state -OPTIONAL
    	private String jurisdiction;		//county, region, state the Incident is in
    	private String region;
    	private String state;
    	private String roadType;				//type of road affected
    	private int laneCount;				//# of Lanes in the reporting location
    	private int	noOfLanesAffected;		//# of lanes affected by the Incident
    	private List<VDOTIncidentLane> lanesAffected = new ArrayList<VDOTIncidentLane>();		//specific Lanes affected by the Incident
    	//incident event
    	private String incidentType;
    	private String delayType;
    	private int delayMiles;			//# of miles of delay
        
    	public Date getLastTimeUpdated(){
    		return lastTimeUpdated;
    	}
    	
    	public void setLastTimeUpdated(Date lastTimeUpdated){
    		this.lastTimeUpdated = lastTimeUpdated;
    	}
    	
    	public String getIssueAgency(){
    		return issueAgency;
    	}
    	
    	public void setIssueAgency(String issueAgency){
    		this.issueAgency = issueAgency;
    	}
    	
    	public String getIncidentID(){
    		return incidentID;
    	}
    	
    	public void setIncidentID(String incidentID){
    		this.incidentID = incidentID;
    	}
    	
    	public String getDescription(){
    		return description;
    	}
    	public void setDescription(String description){
    		this.description = description;
    	}
    	
        public float[] getGeometry() {
            return geometry;
        }

        public void setGeometry(float[] geometry) {
            this.geometry = geometry;
        }
   	
        public String getTravelDirection(){
        	return travelDirection;
        }
        
        public void setTravelDirection(String travelDirection){
        	this.travelDirection = travelDirection;
        }
        
        public String getJurisdiction(){
        	return jurisdiction;
        }
        
        public void setJurisdiction(String jurisdiction){
        	this.jurisdiction = jurisdiction;
        }
        
        public String getRegion(){
        	return region;
        }
        
        public void setRegion(String region){
        	this.region = region;
        }
        
        public String getState(){
        	return state;
        }
        
        public void setState(String state){
        	this.state = state;
        }
        
        public String getRoadType(){
        	return roadType;
        }
        
        public void setRoadType(String roadType){
        	this.roadType = roadType;
        }
        
        public int getLaneCount(){
        	return laneCount;
        }
        
        public void setLaneCount(int laneCount){
        	this.laneCount = laneCount;
        }
        
        public int getNoOfLanesAffected(){
        	return noOfLanesAffected;
        }
        
        public void setNoOfLanesAffected(int noOfLanesAffected){
        	this.noOfLanesAffected = noOfLanesAffected;
        }
        
        public List<VDOTIncidentLane> getLanesAffected(){
        	return lanesAffected;
        }
        
        public void setLineAffected(List<VDOTIncidentLane> lanesAffected){
        	this.lanesAffected = lanesAffected;
        }
        
        public String getIncidentType(){
        	return incidentType;
        }
        
        public void setIncidentType(String incidentType){
        	this.incidentType = incidentType;
        }
        
        public String getDelayType(){
        	return delayType;
        }
        
        public void setDelayType(String delayType){
        	this.delayType = delayType;
        }
        
    	public int getDelayMiles(){
    		return delayMiles;
    	}
    	
    	public void setDelayMiles(int delayMiles){
    		this.delayMiles = delayMiles;
    	}
    }
    
    public static class VDOTIncidentLane {
    	private String laneType;		//type of lane
    	private String laneDirection;	//direction of travel affected by the Incident
    	private String laneStatus;			//Open(0) or closed(1)
    	
    	public String getLaneType(){
    		return laneType;
    	}
    	
    	public void setLaneType(String laneType){
    		this.laneType = laneType;
    	}
    	
    	public String getLaneDirection(){
    		return laneDirection;
    	}
    	
    	public void setLaneDirection(String laneDirection){
    		this.laneDirection = laneDirection;
    	}
    	
    	public String getLaneStatus(){
    		return laneStatus;
    	}
    	
    	public void setLaneStatus(String laneStatus){
    		this.laneStatus = laneStatus;
    	}
    }
}
