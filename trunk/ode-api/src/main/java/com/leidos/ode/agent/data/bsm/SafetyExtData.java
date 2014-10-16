package com.leidos.ode.agent.data.bsm;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SafetyExtData  implements Serializable{
	private int bsmId;
	private Integer pathHistoryLength;
	private Integer itemCount;
	private String pathHistory;
	private Integer curveRadius;
	private Integer curveConfidence;
	private Integer vehStatusLights;
	private Integer vehStatusLightBar;
	private Integer wiperStatFront;
	private Integer wiperRateFront;
	private Integer wiperStatRear;
	private Integer wiperRateRear;
	private String brakes;
	private Integer breakPressure;
	private Integer coefficientOfFriction;
	private Integer sunSensor;
	private Integer rainSensor;
	private Integer airTemp;
	private Integer airPressure;
	private Double steeringWheelAngle;
	private Integer steeringWheelAngleConf;
	private Integer steeringWheelAngleRateOfChange;
	private Integer drivingWheelAngle;
	private Integer vehStatusThrottle;
	private Integer vehStatusHeight;
	private Integer vehStatusBumperFront;
	private Integer vehStatusBumperRear;
	private Integer vehStatusMass;
	private Integer vehStatusTrailerWeight;
	private Integer vehStatusType;
	private Integer initPosYear;
	private Integer initPosMonth;
	private Integer initPosDay;
	private Integer initPosHour;
	private Integer initPosMinute;
	private Integer initPosSecond;
	private Double initPosLat;
	private Double initPosLong;
	private Double elevation;
	private Double heading;
	private Integer trans;
	private Double speed;
	private String posAccuracy;
	private String timeConfidence;
	private String posConfidence;
	private String speedConfidence;
	private Integer v2vHeight;
	private Integer v2vBumperFront;
	private Integer v2vBumperRear;
	private Integer v2vMass;
	private Integer v2vType;
	
	public int getBsmId() {
		return bsmId;
	}
	public void setBsmId(int bsmId) {
		this.bsmId = bsmId;
	}
	public Integer getPathHistoryLength() {
		return pathHistoryLength;
	}
	public void setPathHistoryLength(Integer pathHistoryLength) {
		this.pathHistoryLength = pathHistoryLength;
	}
	public Integer getItemCount() {
		return itemCount;
	}
	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}
	public String getPathHistory() {
		return pathHistory;
	}
	public void setPathHistory(String pathHistory) {
		this.pathHistory = pathHistory;
	}
	public Integer getCurveRadius() {
		return curveRadius;
	}
	public void setCurveRadius(Integer curveRadius) {
		this.curveRadius = curveRadius;
	}
	public Integer getCurveConfidence() {
		return curveConfidence;
	}
	public void setCurveConfidence(Integer curveConfidence) {
		this.curveConfidence = curveConfidence;
	}
	public Integer getVehStatusLights() {
		return vehStatusLights;
	}
	public void setVehStatusLights(Integer vehStatusLights) {
		this.vehStatusLights = vehStatusLights;
	}
	public Integer getVehStatusLightBar() {
		return vehStatusLightBar;
	}
	public void setVehStatusLightBar(Integer vehStatusLightBar) {
		this.vehStatusLightBar = vehStatusLightBar;
	}
	public Integer getWiperStatFront() {
		return wiperStatFront;
	}
	public void setWiperStatFront(Integer wiperStatFront) {
		this.wiperStatFront = wiperStatFront;
	}
	public Integer getWiperRateFront() {
		return wiperRateFront;
	}
	public void setWiperRateFront(Integer wiperRateFront) {
		this.wiperRateFront = wiperRateFront;
	}
	public Integer getWiperStatRear() {
		return wiperStatRear;
	}
	public void setWiperStatRear(Integer wiperStatRear) {
		this.wiperStatRear = wiperStatRear;
	}
	public Integer getWiperRateRear() {
		return wiperRateRear;
	}
	public void setWiperRateRear(Integer wiperRateRear) {
		this.wiperRateRear = wiperRateRear;
	}
	public String getBrakes() {
		return brakes;
	}
	public void setBrakes(String brakes) {
		this.brakes = brakes;
	}
	public Integer getBreakPressure() {
		return breakPressure;
	}
	public void setBreakPressure(Integer breakPressure) {
		this.breakPressure = breakPressure;
	}
	public Integer getCoefficientOfFriction() {
		return coefficientOfFriction;
	}
	public void setCoefficientOfFriction(Integer coefficientOfFriction) {
		this.coefficientOfFriction = coefficientOfFriction;
	}
	public Integer getSunSensor() {
		return sunSensor;
	}
	public void setSunSensor(Integer sunSensor) {
		this.sunSensor = sunSensor;
	}
	public Integer getRainSensor() {
		return rainSensor;
	}
	public void setRainSensor(Integer rainSensor) {
		this.rainSensor = rainSensor;
	}
	public Integer getAirTemp() {
		return airTemp;
	}
	public void setAirTemp(Integer airTemp) {
		this.airTemp = airTemp;
	}
	public Integer getAirPressure() {
		return airPressure;
	}
	public void setAirPressure(Integer airPressure) {
		this.airPressure = airPressure;
	}
	public Double getSteeringWheelAngle() {
		return steeringWheelAngle;
	}
	public void setSteeringWheelAngle(Double steeringWheelAngle) {
		this.steeringWheelAngle = steeringWheelAngle;
	}
	public Integer getSteeringWheelAngleConf() {
		return steeringWheelAngleConf;
	}
	public void setSteeringWheelAngleConf(Integer steeringWheelAngleConf) {
		this.steeringWheelAngleConf = steeringWheelAngleConf;
	}
	public Integer getSteeringWheelAngleRateOfChange() {
		return steeringWheelAngleRateOfChange;
	}
	public void setSteeringWheelAngleRateOfChange(
			Integer steeringWheelAngleRateOfChange) {
		this.steeringWheelAngleRateOfChange = steeringWheelAngleRateOfChange;
	}
	public Integer getDrivingWheelAngle() {
		return drivingWheelAngle;
	}
	public void setDrivingWheelAngle(Integer drivingWheelAngle) {
		this.drivingWheelAngle = drivingWheelAngle;
	}
	public Integer getVehStatusThrottle() {
		return vehStatusThrottle;
	}
	public void setVehStatusThrottle(Integer vehStatusThrottle) {
		this.vehStatusThrottle = vehStatusThrottle;
	}
	public Integer getVehStatusHeight() {
		return vehStatusHeight;
	}
	public void setVehStatusHeight(Integer vehStatusHeight) {
		this.vehStatusHeight = vehStatusHeight;
	}
	public Integer getVehStatusBumperFront() {
		return vehStatusBumperFront;
	}
	public void setVehStatusBumperFront(Integer vehStatusBumperFront) {
		this.vehStatusBumperFront = vehStatusBumperFront;
	}
	public Integer getVehStatusBumperRear() {
		return vehStatusBumperRear;
	}
	public void setVehStatusBumperRear(Integer vehStatusBumperRear) {
		this.vehStatusBumperRear = vehStatusBumperRear;
	}
	public Integer getVehStatusMass() {
		return vehStatusMass;
	}
	public void setVehStatusMass(Integer vehStatusMass) {
		this.vehStatusMass = vehStatusMass;
	}
	public Integer getVehStatusTrailerWeight() {
		return vehStatusTrailerWeight;
	}
	public void setVehStatusTrailerWeight(Integer vehStatusTrailerWeight) {
		this.vehStatusTrailerWeight = vehStatusTrailerWeight;
	}
	public Integer getVehStatusType() {
		return vehStatusType;
	}
	public void setVehStatusType(Integer vehStatusType) {
		this.vehStatusType = vehStatusType;
	}
	public Integer getInitPosYear() {
		return initPosYear;
	}
	public void setInitPosYear(Integer initPosYear) {
		this.initPosYear = initPosYear;
	}
	public Integer getInitPosMonth() {
		return initPosMonth;
	}
	public void setInitPosMonth(Integer initPosMonth) {
		this.initPosMonth = initPosMonth;
	}
	public Integer getInitPosDay() {
		return initPosDay;
	}
	public void setInitPosDay(Integer initPosDay) {
		this.initPosDay = initPosDay;
	}
	public Integer getInitPosHour() {
		return initPosHour;
	}
	public void setInitPosHour(Integer initPosHour) {
		this.initPosHour = initPosHour;
	}
	public Integer getInitPosMinute() {
		return initPosMinute;
	}
	public void setInitPosMinute(Integer initPosMinute) {
		this.initPosMinute = initPosMinute;
	}
	public Integer getInitPosSecond() {
		return initPosSecond;
	}
	public void setInitPosSecond(Integer initPosSecond) {
		this.initPosSecond = initPosSecond;
	}
	public Double getInitPosLat() {
		return initPosLat;
	}
	public void setInitPosLat(Double initPosLat) {
		this.initPosLat = initPosLat;
	}
	public Double getInitPosLong() {
		return initPosLong;
	}
	public void setInitPosLong(Double initPosLong) {
		this.initPosLong = initPosLong;
	}
	public Double getElevation() {
		return elevation;
	}
	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}
	public Double getHeading() {
		return heading;
	}
	public void setHeading(Double heading) {
		this.heading = heading;
	}
	public Integer getTrans() {
		return trans;
	}
	public void setTrans(Integer trans) {
		this.trans = trans;
	}
	public Double getSpeed() {
		return speed;
	}
	public void setSpeed(Double speed) {
		this.speed = speed;
	}
	public String getPosAccuracy() {
		return posAccuracy;
	}
	public void setPosAccuracy(String posAccuracy) {
		this.posAccuracy = posAccuracy;
	}
	public String getTimeConfidence() {
		return timeConfidence;
	}
	public void setTimeConfidence(String timeConfidence) {
		this.timeConfidence = timeConfidence;
	}
	public String getPosConfidence() {
		return posConfidence;
	}
	public void setPosConfidence(String posConfidence) {
		this.posConfidence = posConfidence;
	}
	public String getSpeedConfidence() {
		return speedConfidence;
	}
	public void setSpeedConfidence(String speedConfidence) {
		this.speedConfidence = speedConfidence;
	}
	public Integer getV2vHeight() {
		return v2vHeight;
	}
	public void setV2vHeight(Integer v2vHeight) {
		this.v2vHeight = v2vHeight;
	}
	public Integer getV2vBumperFront() {
		return v2vBumperFront;
	}
	public void setV2vBumperFront(Integer v2vBumperFront) {
		this.v2vBumperFront = v2vBumperFront;
	}
	public Integer getV2vBumperRear() {
		return v2vBumperRear;
	}
	public void setV2vBumperRear(Integer v2vBumperRear) {
		this.v2vBumperRear = v2vBumperRear;
	}
	public Integer getV2vMass() {
		return v2vMass;
	}
	public void setV2vMass(Integer v2vMass) {
		this.v2vMass = v2vMass;
	}
	public Integer getV2vType() {
		return v2vType;
	}
	public void setV2vType(Integer v2vType) {
		this.v2vType = v2vType;
	}	
	
	
	public String toString(){
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		
		pw.println("\t-Safety EXT Data-");
		pw.println("\t pathHistoryLength: "+pathHistoryLength);
		pw.println("\t itemCount: "+itemCount);
		pw.println("\t pathHistory: "+pathHistory);
		
		pw.println("\t curveRadius: "+curveRadius);
		pw.println("\t curveConfidence: "+curveConfidence);
		
		
		pw.println("\t vehStatusLights: "+vehStatusLights);
		pw.println("\t vehStatusLightBar: "+vehStatusLightBar);
		pw.println("\t wiperStatFront: "+wiperStatFront);
		pw.println("\t wiperRateFront: "+wiperRateFront);
		pw.println("\t wiperStatRear: "+wiperStatRear);
		pw.println("\t wiperRateRear: "+wiperRateRear);
		pw.println("\t brakes: "+brakes);
		pw.println("\t breakPressure: "+breakPressure);
		pw.println("\t coefficientOfFriction: "+coefficientOfFriction);
		pw.println("\t sunSensor: "+sunSensor);
		pw.println("\t rainSensor: "+rainSensor);
		pw.println("\t airTemp: "+airTemp);
		pw.println("\t airPressure: "+airPressure);
		pw.println("\t steeringWheelAngle: "+steeringWheelAngle);
		pw.println("\t steeringWheelAngleConf: "+steeringWheelAngleConf);
		pw.println("\t steeringWheelAngleRateOfChange: "+steeringWheelAngleRateOfChange);
		pw.println("\t drivingWheelAngle: "+drivingWheelAngle);
		pw.println("\t vehStatusThrottle: "+vehStatusThrottle);
		pw.println("\t vehStatusHeight: "+vehStatusHeight);
		pw.println("\t vehStatusBumperFront: "+vehStatusBumperFront);
		pw.println("\t vehStatusBumperRear: "+vehStatusBumperRear);
		pw.println("\t vehStatusMass: "+vehStatusMass);
		pw.println("\t vehStatusTrailerWeight: "+vehStatusTrailerWeight);
		pw.println("\t vehStatusType: "+vehStatusType);
		pw.println("\t initPosYear: "+initPosYear);
		pw.println("\t initPosMonth: "+initPosMonth);
		pw.println("\t initPosDay: "+initPosDay);
		pw.println("\t initPosHour: "+initPosHour);
		pw.println("\t initPosMinute: "+initPosMinute);
		pw.println("\t initPosSecond: "+initPosSecond);
		pw.println("\t initPosLat: "+initPosLat);
		pw.println("\t initPosLong: "+initPosLong);
		pw.println("\t elevation: "+elevation);
		pw.println("\t heading: "+heading);
		pw.println("\t trans: "+trans);
		pw.println("\t speed: "+speed);
		pw.println("\t posAccuracy: "+posAccuracy);
		pw.println("\t timeConfidence: "+timeConfidence);
		pw.println("\t posConfidence: "+posConfidence);
		pw.println("\t speedConfidence: "+speedConfidence);
		pw.println("\t v2vHeight: "+v2vHeight);
		pw.println("\t v2vBumperFront: "+v2vBumperFront);
		pw.println("\t v2vBumperRear: "+v2vBumperRear);
		pw.println("\t v2vMass: "+v2vMass);
		pw.println("\t v2vType: "+v2vType);
		
		
		return sw.getBuffer().toString();
	}
	
	
}

