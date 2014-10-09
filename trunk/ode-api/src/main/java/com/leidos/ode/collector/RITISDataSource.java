package com.leidos.ode.collector;

import com.leidos.ode.collector.datasource.RestPullDataSource;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * RITIS data source, that includes a geographical bounding box filter for the emulator. Handles
 * requests to RITIS for data using the api key provided to us by RITIS.
 * Polls for RITIS data using Apache HTTP libraries.
 *
 * @author lamde
 */
public class RITISDataSource extends RestPullDataSource {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private String apiKey;

    @Override
    public void startDataSource(CollectorDataSourceListener collectorDataSourceListener) {
        super.startDataSource(collectorDataSourceListener);
        executeDataSourceThread(collectorDataSourceListener);
    }

    @Override
    protected byte[] pollDataSource() {
        try {
            CloseableHttpResponse closeableHttpResponse = getHttpClient().execute(getHttpGet());
            HttpEntity responseEntity = closeableHttpResponse.getEntity();
            byte[] responseBytes = EntityUtils.toByteArray(responseEntity);
            EntityUtils.consume(responseEntity);
            Thread.sleep(getRequestLimit());
            return responseBytes;
        } catch (ClientProtocolException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (IOException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            getLogger().error(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    protected String getWFSFilter() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getFeedName());
        stringBuilder.append("?");
        stringBuilder.append(getApiKey());
        stringBuilder.append("&");
        //The system from which we query data (note detector requests only support 1 system).
        //Possible values
        if (getFeedName().equals("incidentevent") || getFeedName().equals("dms") || getFeedName().equals("detector")) {
            stringBuilder.append("system=vdot_nova");
            stringBuilder.append("&");
            //Latitude of the lower left corner
            stringBuilder.append("box-lat=38.856259");
            stringBuilder.append("&");
            //Longitude of the lower left corner
            stringBuilder.append("box-lon=-77.35548");
            stringBuilder.append("&");
            //The width (in meters) of the box
            stringBuilder.append("width=8321");
            stringBuilder.append("&");
            //The height (in meters) of the box
            stringBuilder.append("height=2952");
        }
        if (getFeedName().equals("weather")){
            stringBuilder.append("system=nws");
            stringBuilder.append("&");
            stringBuilder.append("type=alerts");
            //For some reason, no results are ever returned when using the following state filter
//            stringBuilder.append("&");
//            stringBuilder.append("state=Virginia");
        }

        //The road filter returns all data on the specified roads
//        stringBuilder.append("road=I-66");
        return stringBuilder.toString();
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = "api-key=" + apiKey;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
