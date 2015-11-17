package com.leidos.ode.agent.data.rtms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/17/14
 * Time: 7:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class RTMSData {
    private List<RTMSDataElement> rtmsDataElements;

    public RTMSData(){
        rtmsDataElements = new ArrayList<RTMSDataElement>();
    }

    public List<RTMSDataElement> getRtmsDataElements() {
        return rtmsDataElements;
    }

    public void setRtmsDataElements(List<RTMSDataElement> rtmsDataElements) {
        this.rtmsDataElements = rtmsDataElements;
    }

    public static class RTMSDataElement {
        private int rtmsNetworkId;
        private String rtmsName = "";
        private int zone;
        private String zoneLabel = "";
        private String stationName = "";
        private int speed;
        private int fwdlkSpeed;
        private int volume;
        private int volMid;
        private int volLong;
        private int volExtraLong;
        private String occupancy = "";
        private int msgNumber;
        private Date dateTimeStamp;
        private float sensorErrRate;
        private int healthByte;
        private String speedUnits = "";
        private int volMid2;
        private int volLong2;

        public Date getDateTimeStamp() {
            return dateTimeStamp;
        }

        public void setDateTimeStamp(Date dateTimeStamp) {
            this.dateTimeStamp = dateTimeStamp;
        }

        public int getFwdlkSpeed() {
            return fwdlkSpeed;
        }

        public void setFwdlkSpeed(int fwdlkSpeed) {
            this.fwdlkSpeed = fwdlkSpeed;
        }

        public int getHealthByte() {
            return healthByte;
        }

        public void setHealthByte(int healthByte) {
            this.healthByte = healthByte;
        }

        public int getMsgNumber() {
            return msgNumber;
        }

        public void setMsgNumber(int msgNumber) {
            this.msgNumber = msgNumber;
        }

        public String getOccupancy() {
            return occupancy;
        }

        public void setOccupancy(String occupancy) {
            this.occupancy = occupancy;
        }

        public String getRtmsName() {
            return rtmsName;
        }

        public void setRtmsName(String rtmsName) {
            this.rtmsName = rtmsName;
        }

        public int getRtmsNetworkId() {
            return rtmsNetworkId;
        }

        public void setRtmsNetworkId(int rtmsNetworkId) {
            this.rtmsNetworkId = rtmsNetworkId;
        }

        public float getSensorErrRate() {
            return sensorErrRate;
        }

        public void setSensorErrRate(float sensorErrRate) {
            this.sensorErrRate = sensorErrRate;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public String getSpeedUnits() {
            return speedUnits;
        }

        public void setSpeedUnits(String speedUnits) {
            this.speedUnits = speedUnits;
        }

        public String getStationName() {
            return stationName;
        }

        public void setStationName(String stationName) {
            this.stationName = stationName;
        }

        public int getVolExtraLong() {
            return volExtraLong;
        }

        public void setVolExtraLong(int volExtraLong) {
            this.volExtraLong = volExtraLong;
        }

        public int getVolLong2() {
            return volLong2;
        }

        public void setVolLong2(int volLong2) {
            this.volLong2 = volLong2;
        }

        public int getVolLong() {
            return volLong;
        }

        public void setVolLong(int volLong) {
            this.volLong = volLong;
        }

        public int getVolMid2() {
            return volMid2;
        }

        public void setVolMid2(int volMid2) {
            this.volMid2 = volMid2;
        }

        public int getVolMid() {
            return volMid;
        }

        public void setVolMid(int volMid) {
            this.volMid = volMid;
        }

        public int getVolume() {
            return volume;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }

        public int getZone() {
            return zone;
        }

        public void setZone(int zone) {
            this.zone = zone;
        }

        public String getZoneLabel() {
            return zoneLabel;
        }

        public void setZoneLabel(String zoneLabel) {
            this.zoneLabel = zoneLabel;
        }
    }
}
