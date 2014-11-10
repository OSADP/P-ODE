package com.leidos.ode.agent.data.wxde;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/10/14
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
public class WXDEData implements Serializable {

    private List<WXDEDataElement> wxdeDataElements;

    public WXDEData() {
        wxdeDataElements = new ArrayList<WXDEDataElement>();
    }

    public List<WXDEDataElement> getWxdeDataElements() {
        return wxdeDataElements;
    }

    public void setWxdeDataElements(List<WXDEDataElement> wxdeDataElements) {
        this.wxdeDataElements = wxdeDataElements;
    }

    public static class WXDEDataElement {

        private String sourceId;
        private String obsTypeId;
        private String obsTypeName;
        private String sensorId;
        private String sensorIndex;
        private String platformId;
        private String siteId;
        private String category;
        private String contribId;
        private String contributor;
        private String platformCode;
        private Date timestamp;
        private double latitude;
        private double longitude;
        private double elevation;
        private double observation;
        private String units;
        private double englishValue;
        private String englishUnits;
        private double confValue;
        private String flags;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public double getConfValue() {
            return confValue;
        }

        public void setConfValue(double confValue) {
            this.confValue = confValue;
        }

        public String getContribId() {
            return contribId;
        }

        public void setContribId(String contribId) {
            this.contribId = contribId;
        }

        public String getContributor() {
            return contributor;
        }

        public void setContributor(String contributor) {
            this.contributor = contributor;
        }

        public double getElevation() {
            return elevation;
        }

        public void setElevation(double elevation) {
            this.elevation = elevation;
        }

        public String getEnglishUnits() {
            return englishUnits;
        }

        public void setEnglishUnits(String englishUnits) {
            this.englishUnits = englishUnits;
        }

        public double getEnglishValue() {
            return englishValue;
        }

        public void setEnglishValue(double englishValue) {
            this.englishValue = englishValue;
        }

        public String getFlags() {
            return flags;
        }

        public void setFlags(String flags) {
            this.flags = flags;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getObservation() {
            return observation;
        }

        public void setObservation(double observation) {
            this.observation = observation;
        }

        public String getObsTypeId() {
            return obsTypeId;
        }

        public void setObsTypeId(String obsTypeId) {
            this.obsTypeId = obsTypeId;
        }

        public String getObsTypeName() {
            return obsTypeName;
        }

        public void setObsTypeName(String obsTypeName) {
            this.obsTypeName = obsTypeName;
        }

        public String getPlatformCode() {
            return platformCode;
        }

        public void setPlatformCode(String platformCode) {
            this.platformCode = platformCode;
        }

        public String getPlatformId() {
            return platformId;
        }

        public void setPlatformId(String platformId) {
            this.platformId = platformId;
        }

        public String getSensorId() {
            return sensorId;
        }

        public void setSensorId(String sensorId) {
            this.sensorId = sensorId;
        }

        public String getSensorIndex() {
            return sensorIndex;
        }

        public void setSensorIndex(String sensorIndex) {
            this.sensorIndex = sensorIndex;
        }

        public String getSiteId() {
            return siteId;
        }

        public void setSiteId(String siteId) {
            this.siteId = siteId;
        }

        public String getSourceId() {
            return sourceId;
        }

        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }
    }
}
