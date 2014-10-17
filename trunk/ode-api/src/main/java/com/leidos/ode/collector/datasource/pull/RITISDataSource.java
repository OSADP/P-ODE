package com.leidos.ode.collector.datasource.pull;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * RITIS data source, that includes a geographical bounding box filter for the emulator. Handles
 * requests to RITIS for data using the api key provided to us by RITIS.
 * Polls for RITIS data using Apache HTTP libraries.
 *
 * @author lamde
 */
public class RITISDataSource extends RestPullDataSource {

    private String apiKey;

    @Override
    public void startDataSource() {
        super.startDataSource();
        /* The following line of code is @Deprecated. Due to the request interval
        restriction imposed by RITIS, each RITISDataSource is no longer responsible
        for its own polling
        executeDataSourceThread(collectorDataSourceListener);
        */
    }

    @Override
    public byte[] pollDataSource() {
        try {
            getLogger().debug("Polling data source for feed: '" + getFeedName() + "'.");
            CloseableHttpResponse closeableHttpResponse = getHttpClient().execute(getHttpGet());
            HttpEntity responseEntity = closeableHttpResponse.getEntity();
            byte[] responseBytes = EntityUtils.toByteArray(responseEntity);
            EntityUtils.consume(responseEntity);
            return responseBytes;
        } catch (ClientProtocolException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (IOException e) {
            getLogger().error(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    protected String buildWfsFilter() {
        return new StringBuilder()
                .append(getFeedName())
                .append("?")
                .append("api-key=")
                .append(getApiKey())
                .append(getWfsFilter())
                .toString();
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
