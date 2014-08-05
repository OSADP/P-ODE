package com.leidos.ode.collector.datasource;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 7/14/14
 * Time: 5:46 PM
 * To change this template use File | Settings | File Templates.
 */

public class TestVDOTRestPullDataSource {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);

    private static RestPullDataSource restPullDataSource;

    @BeforeClass
    public static void setup(){
        restPullDataSource = new RestPullDataSource();
        restPullDataSource.setSourceAddress("http://www.vdotdatasharing.org");
        restPullDataSource.setUser("leidos");
        restPullDataSource.setPass("ijeret58");
    }

    @Test
    public void testTrafficSensorStations() {
        retrieveData("TssDetectorStatus");
    }

    @Test
    public void testTravelTimeSegments() {
        retrieveData("TravelTimeData");
    }

    @Test
    public void testEnvironmentalSensorStations() {
        retrieveData("ESSMessage");
    }

    private void retrieveData(String recordName){
        restPullDataSource.setRequestURI(getRequestURI(recordName));
        try {
            restPullDataSource.startDataSource();
            byte[] data = restPullDataSource.getDataFromSource();
            System.out.println("Received vdotdata of length: " + data.length);
        } catch (DataSourceException e) {
            logger.equals(e.getLocalizedMessage());
        }
    }

    private String getRequestURI(String recordName){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("xmldb").append("/").append(recordName).append("/");
        return stringBuilder.toString();
    }

}

