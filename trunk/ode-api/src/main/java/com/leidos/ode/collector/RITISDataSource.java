package com.leidos.ode.collector;

import com.leidos.ode.collector.datasource.DataSourceException;
import com.leidos.ode.collector.datasource.RestPullDataSource;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 8/18/14
 * Time: 1:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class RITISDataSource extends RestPullDataSource {

    private final String TAG = getClass().getSimpleName();

    private Logger logger = Logger.getLogger(TAG);

    private String apiKey;
    private int requestLimit;
    private String incidentEventUrl;
    private String detectorUrl;

    public RITISDataSource() {

    }

    public void testGetIncidentEvents() {
        retrieveData(getIncidentEventUrl(), getApiKey(), "system=vdot_nova");
    }


    public void testGetVolume() {
        retrieveData(getDetectorUrl(), getApiKey(), "system=vdot_nova", "volume=60");
    }


    public void testGetOccupancy() {
        retrieveData(getDetectorUrl(), getApiKey(), "system=vdot_nova", "occupancy=60");
    }


    public void testGetBluFaxTravelTime() {
        retrieveData(getDetectorUrl(), getApiKey(), "system=blufax", "travelTime=2000");
    }

    private void waitForApiRequestLimit() {
        try {
            Thread.sleep(getRequestLimit());
        } catch (InterruptedException e) {
            logger.equals(e.getLocalizedMessage());
        }
    }

    private void retrieveData(String recordName, String... params) {
        //Check if the previous request was for the same sensor data. If it was, then wait to prevent over api limit error.
        String previousDetectorFilter = getSourceAddress();
        if (previousDetectorFilter != null && previousDetectorFilter.equals(recordName)) {
            waitForApiRequestLimit();
        }
        setSourceAddress(recordName);
        setRequestParams(params);

        try {
            startDataSource();
            byte[] data = getDataFromSource();
        } catch (DataSourceException e) {
            logger.equals(e.getLocalizedMessage());
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = "api-key=" + apiKey;
    }

    public int getRequestLimit() {
        return requestLimit;
    }

    public void setRequestLimit(String requestLimit) {
        this.requestLimit = Integer.parseInt(requestLimit);
    }

    public String getIncidentEventUrl() {
        return incidentEventUrl;
    }

    public void setIncidentEventUrl(String incidentEventUrl) {
        this.incidentEventUrl = incidentEventUrl;
    }

    public String getDetectorUrl() {
        return detectorUrl;
    }

    public void setDetectorUrl(String detectorUrl) {
        this.detectorUrl = detectorUrl;
    }

}
