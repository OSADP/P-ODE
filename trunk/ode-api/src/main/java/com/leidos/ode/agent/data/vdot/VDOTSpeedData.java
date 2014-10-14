package com.leidos.ode.agent.data.vdot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/8/14
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class VDOTSpeedData {

    private List<VDOTSpeedDataElement> vdotSpeedDataElements;

    public VDOTSpeedData(){
        vdotSpeedDataElements = new ArrayList<VDOTSpeedDataElement>();
    }

    public List<VDOTSpeedDataElement> getVdotSpeedDataElements() {
        return vdotSpeedDataElements;
    }

    public void setVdotSpeedDataElements(List<VDOTSpeedDataElement> vdotSpeedDataElements) {
        this.vdotSpeedDataElements = vdotSpeedDataElements;
    }

    public static class VDOTSpeedDataElement {
        private String detectorId;
        private String stationId;
        private String deviceName;
        private String laneDirection;
        private String laneType;
        private int laneNum;
        private Date lastUpdated;
        private double mileMarker;
        private int occupancy;
        private String routeName;
        private int speed;
        private float[] geometry;
        private int volume;
        private int nodeId;

        public String getDetectorId() {
            return detectorId;
        }

        public void setDetectorId(String detectorId) {
            this.detectorId = detectorId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public float[] getGeometry() {
            return geometry;
        }

        public void setGeometry(float[] geometry) {
            this.geometry = geometry;
        }

        public String getLaneDirection() {
            return laneDirection;
        }

        public void setLaneDirection(String laneDirection) {
            this.laneDirection = laneDirection;
        }

        public int getLaneNum() {
            return laneNum;
        }

        public void setLaneNum(int laneNum) {
            this.laneNum = laneNum;
        }

        public String getLaneType() {
            return laneType;
        }

        public void setLaneType(String laneType) {
            this.laneType = laneType;
        }

        public Date getLastUpdated() {
            return lastUpdated;
        }

        public void setLastUpdated(Date lastUpdated) {
            this.lastUpdated = lastUpdated;
        }

        public double getMileMarker() {
            return mileMarker;
        }

        public void setMileMarker(double mileMarker) {
            this.mileMarker = mileMarker;
        }

        public int getNodeId() {
            return nodeId;
        }

        public void setNodeId(int nodeId) {
            this.nodeId = nodeId;
        }

        public int getOccupancy() {
            return occupancy;
        }

        public void setOccupancy(int occupancy) {
            this.occupancy = occupancy;
        }

        public String getRouteName() {
            return routeName;
        }

        public void setRouteName(String routeName) {
            this.routeName = routeName;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public String getStationId() {
            return stationId;
        }

        public void setStationId(String stationId) {
            this.stationId = stationId;
        }

        public int getVolume() {
            return volume;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }
    }
}
