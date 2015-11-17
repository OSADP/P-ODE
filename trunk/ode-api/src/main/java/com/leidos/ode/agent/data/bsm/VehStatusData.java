package com.leidos.ode.agent.data.bsm;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

@XmlRootElement
public class VehStatusData implements Serializable{
    private int bsmId;
    private int vehStatusLights;
    private int vehStatusLightBar;
    private int wiperStatFront;
    private int wiperRateFront;
    private int wiperStatRear;
    private int wiperRateRear;
    private String brakes;
    private int breakPressure;
    private int coefficientOfFriction;
    private int sunSensor;
    private int rainSensor;
    private int airTemp;
    private int airPressure;
    private double steeringWheelAngle;
    private int steeringWheelAngleConf;
    private int steeringWheelAngleRateOfChange;
    private int drivingWheelAngle;
    private int vehStatusThrottle;
    private int vehStatusHeight;
    private int vehStatusBumperFront;
    private int vehStatusBumperRear;
    private int vehStatusMass;
    private int vehStatusTrailerWeight;
    private int vehStatusType;
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
    private int v2vHeight;
    private int v2vBumperFront;
    private int v2vBumperRear;
    private int v2vMass;
    private int v2vType;

    public int getBsmId() {
        return bsmId;
    }

    public void setBsmId(int bsmId) {
        this.bsmId = bsmId;
    }

    public int getVehStatusLights() {
        return vehStatusLights;
    }

    public void setVehStatusLights(int vehStatusLights) {
        this.vehStatusLights = vehStatusLights;
    }

    public int getVehStatusLightBar() {
        return vehStatusLightBar;
    }

    public void setVehStatusLightBar(int vehStatusLightBar) {
        this.vehStatusLightBar = vehStatusLightBar;
    }

    public int getWiperStatFront() {
        return wiperStatFront;
    }

    public void setWiperStatFront(int wiperStatFront) {
        this.wiperStatFront = wiperStatFront;
    }

    public int getWiperRateFront() {
        return wiperRateFront;
    }

    public void setWiperRateFront(int wiperRateFront) {
        this.wiperRateFront = wiperRateFront;
    }

    public int getWiperStatRear() {
        return wiperStatRear;
    }

    public void setWiperStatRear(int wiperStatRear) {
        this.wiperStatRear = wiperStatRear;
    }

    public int getWiperRateRear() {
        return wiperRateRear;
    }

    public void setWiperRateRear(int wiperRateRear) {
        this.wiperRateRear = wiperRateRear;
    }

    public String getBrakes() {
        return brakes;
    }

    public void setBrakes(String brakes) {
        this.brakes = brakes;
    }

    public int getBreakPressure() {
        return breakPressure;
    }

    public void setBreakPressure(int breakPressure) {
        this.breakPressure = breakPressure;
    }

    public int getCoefficientOfFriction() {
        return coefficientOfFriction;
    }

    public void setCoefficientOfFriction(int coefficientOfFriction) {
        this.coefficientOfFriction = coefficientOfFriction;
    }

    public int getSunSensor() {
        return sunSensor;
    }

    public void setSunSensor(int sunSensor) {
        this.sunSensor = sunSensor;
    }

    public int getRainSensor() {
        return rainSensor;
    }

    public void setRainSensor(int rainSensor) {
        this.rainSensor = rainSensor;
    }

    public int getAirTemp() {
        return airTemp;
    }

    public void setAirTemp(int airTemp) {
        this.airTemp = airTemp;
    }

    public int getAirPressure() {
        return airPressure;
    }

    public void setAirPressure(int airPressure) {
        this.airPressure = airPressure;
    }

    public double getSteeringWheelAngle() {
        return steeringWheelAngle;
    }

    public void setSteeringWheelAngle(double steeringWheelAngle) {
        this.steeringWheelAngle = steeringWheelAngle;
    }

    public int getSteeringWheelAngleConf() {
        return steeringWheelAngleConf;
    }

    public void setSteeringWheelAngleConf(int steeringWheelAngleConf) {
        this.steeringWheelAngleConf = steeringWheelAngleConf;
    }

    public int getSteeringWheelAngleRateOfChange() {
        return steeringWheelAngleRateOfChange;
    }

    public void setSteeringWheelAngleRateOfChange(int steeringWheelAngleRateOfChange) {
        this.steeringWheelAngleRateOfChange = steeringWheelAngleRateOfChange;
    }

    public int getDrivingWheelAngle() {
        return drivingWheelAngle;
    }

    public void setDrivingWheelAngle(int drivingWheelAngle) {
        this.drivingWheelAngle = drivingWheelAngle;
    }

    public int getVehStatusThrottle() {
        return vehStatusThrottle;
    }

    public void setVehStatusThrottle(int vehStatusThrottle) {
        this.vehStatusThrottle = vehStatusThrottle;
    }

    public int getVehStatusHeight() {
        return vehStatusHeight;
    }

    public void setVehStatusHeight(int vehStatusHeight) {
        this.vehStatusHeight = vehStatusHeight;
    }

    public int getVehStatusBumperFront() {
        return vehStatusBumperFront;
    }

    public void setVehStatusBumperFront(int vehStatusBumperFront) {
        this.vehStatusBumperFront = vehStatusBumperFront;
    }

    public int getVehStatusBumperRear() {
        return vehStatusBumperRear;
    }

    public void setVehStatusBumperRear(int vehStatusBumperRear) {
        this.vehStatusBumperRear = vehStatusBumperRear;
    }

    public int getVehStatusMass() {
        return vehStatusMass;
    }

    public void setVehStatusMass(int vehStatusMass) {
        this.vehStatusMass = vehStatusMass;
    }

    public int getVehStatusTrailerWeight() {
        return vehStatusTrailerWeight;
    }

    public void setVehStatusTrailerWeight(int vehStatusTrailerWeight) {
        this.vehStatusTrailerWeight = vehStatusTrailerWeight;
    }

    public int getVehStatusType() {
        return vehStatusType;
    }

    public void setVehStatusType(int vehStatusType) {
        this.vehStatusType = vehStatusType;
    }

    public int getV2vHeight() {
        return v2vHeight;
    }

    public void setV2vHeight(int v2vHeight) {
        this.v2vHeight = v2vHeight;
    }

    public int getV2vBumperFront() {
        return v2vBumperFront;
    }

    public void setV2vBumperFront(int v2vBumperFront) {
        this.v2vBumperFront = v2vBumperFront;
    }

    public int getV2vBumperRear() {
        return v2vBumperRear;
    }

    public void setV2vBumperRear(int v2vBumperRear) {
        this.v2vBumperRear = v2vBumperRear;
    }

    public int getV2vMass() {
        return v2vMass;
    }

    public void setV2vMass(int v2vMass) {
        this.v2vMass = v2vMass;
    }

    public int getV2vType() {
        return v2vType;
    }

    public void setV2vType(int v2vType) {
        this.v2vType = v2vType;
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

    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("\t-Vehicle Status Data-");
        pw.println("\t vehStatusLights: " + vehStatusLights);
        pw.println("\t vehStatusLightBar: " + vehStatusLightBar);
        pw.println("\t wiperStatFront: " + wiperStatFront);
        pw.println("\t wiperRateFront: " + wiperRateFront);
        pw.println("\t wiperStatRear: " + wiperStatRear);
        pw.println("\t wiperRateRear: " + wiperRateRear);
        pw.println("\t brakes: " + brakes);
        pw.println("\t breakPressure: " + breakPressure);
        pw.println("\t coefficientOfFriction: " + coefficientOfFriction);
        pw.println("\t sunSensor: " + sunSensor);
        pw.println("\t rainSensor: " + rainSensor);
        pw.println("\t airTemp: " + airTemp);
        pw.println("\t airPressure: " + airPressure);
        pw.println("\t steeringWheelAngle: " + steeringWheelAngle);
        pw.println("\t steeringWheelAngleConf: " + steeringWheelAngleConf);
        pw.println("\t steeringWheelAngleRateOfChange: " + steeringWheelAngleRateOfChange);
        pw.println("\t drivingWheelAngle: " + drivingWheelAngle);
        pw.println("\t vehStatusThrottle: " + vehStatusThrottle);
        pw.println("\t vehStatusHeight: " + vehStatusHeight);
        pw.println("\t vehStatusBumperFront: " + vehStatusBumperFront);
        pw.println("\t vehStatusBumperRear: " + vehStatusBumperRear);
        pw.println("\t vehStatusMass: " + vehStatusMass);
        pw.println("\t vehStatusTrailerWeight: " + vehStatusTrailerWeight);
        pw.println("\t vehStatusType: " + vehStatusType);
        pw.println("\t initPosYear: " + initPosYear);
        pw.println("\t initPosMonth: " + initPosMonth);
        pw.println("\t initPosDay: " + initPosDay);
        pw.println("\t initPosHour: " + initPosHour);
        pw.println("\t initPosMinute: " + initPosMinute);
        pw.println("\t initPosSecond: " + initPosSecond);
        pw.println("\t initPosLat: " + initPosLat);
        pw.println("\t initPosLong: " + initPosLong);
        pw.println("\t elevation: " + elevation);
        pw.println("\t heading: " + heading);
        pw.println("\t trans: " + trans);
        pw.println("\t speed: " + speed);
        pw.println("\t posAccuracy: " + posAccuracy);
        pw.println("\t timeConfidence: " + timeConfidence);
        pw.println("\t posConfidence: " + posConfidence);
        pw.println("\t speedConfidence: " + speedConfidence);
        pw.println("\t v2vHeight: " + v2vHeight);
        pw.println("\t v2vBumperFront: " + v2vBumperFront);
        pw.println("\t v2vBumperRear: " + v2vBumperRear);
        pw.println("\t v2vMass: " + v2vMass);
        pw.println("\t v2vType: " + v2vType);

        return sw.getBuffer().toString();
    }
}
