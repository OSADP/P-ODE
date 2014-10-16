package com.leidos.ode.agent.data.vdot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/8/14
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
public class VDOTTravelTimeData  implements Serializable{

    private List<VDOTTravelTimeDataElement> vdotTravelTimeDataElements;

    public VDOTTravelTimeData() {
        vdotTravelTimeDataElements = new ArrayList<VDOTTravelTimeDataElement>();
    }

    public List<VDOTTravelTimeDataElement> getVdotTravelTimeDataElements() {
        return vdotTravelTimeDataElements;
    }

    public void setVdotTravelTimeDataElements(List<VDOTTravelTimeDataElement> vdotTravelTimeDataElements) {
        this.vdotTravelTimeDataElements = vdotTravelTimeDataElements;
    }

    public static class VDOTTravelTimeDataElement {
        private String segmentId;
        private Date lastTimeUpdated;
        private String segmentName;
        private int travelTime;
        private float[] geometry;

        public float[] getGeometry() {
            return geometry;
        }

        public void setGeometry(float[] geometry) {
            this.geometry = geometry;
        }

        public Date getLastTimeUpdated() {
            return lastTimeUpdated;
        }

        public void setLastTimeUpdated(Date lastTimeUpdated) {
            this.lastTimeUpdated = lastTimeUpdated;
        }

        public String getSegmentId() {
            return segmentId;
        }

        public void setSegmentId(String segmentId) {
            this.segmentId = segmentId;
        }

        public String getSegmentName() {
            return segmentName;
        }

        public void setSegmentName(String segmentName) {
            this.segmentName = segmentName;
        }

        public int getTravelTime() {
            return travelTime;
        }

        public void setTravelTime(int travelTime) {
            this.travelTime = travelTime;
        }
    }
}
