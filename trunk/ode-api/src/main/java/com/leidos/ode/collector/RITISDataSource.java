package com.leidos.ode.collector;

import com.leidos.ode.collector.datasource.RestPullDataSource;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

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

    @Override
    public void startDataSource(CollectorDataSourceListener collectorDataSourceListener) throws DataSourceException {
        super.startDataSource(collectorDataSourceListener);
        new Thread(new RITISDataSourceRunnable(collectorDataSourceListener)).start();
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
        //The road filter returns all dat on the specified roads
//        stringBuilder.append("road=I-66");
        return stringBuilder.toString();
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = "api-key=" + apiKey;
    }

    private class RITISDataSourceRunnable extends DataSourceRunnable {

        private RITISDataSourceRunnable(CollectorDataSourceListener collectorDataSourceListener) {
            super(collectorDataSourceListener);
        }

        @Override
        public void run() {
            CloseableHttpResponse response = null;
            try {
                while (!isInterrupted()) {
                    response = getHttpClient().execute(getHttpGet());
                    HttpEntity responseEntity = response.getEntity();
                    byte[] responseBytes = EntityUtils.toByteArray(responseEntity);
                    EntityUtils.consume(responseEntity);
                    getCollectorDataSourceListener().dataReceived(responseBytes);
                    Thread.sleep(getRequestLimit());
                }
            } catch (ClientProtocolException e) {
                logger.error(e.getLocalizedMessage());
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            } catch (InterruptedException e) {
                logger.error(e.getLocalizedMessage());
            } finally {
                if (getHttpClient() != null) {
                    try {
                        getHttpClient().close();
                        if (response != null) {
                            response.close();
                        }
                    } catch (IOException e) {
                        logger.error(e.getLocalizedMessage());
                    }
                }
            }
        }
    }
}
