package com.leidos.ode.collector.datasource.pull;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 7/14/14
 * Time: 5:39 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class RestPullDataSource extends PullDataSource {

    private String baseUrl;
    private String feedName;
    private String requestString;
    private CloseableHttpClient httpClient;
    private HttpGet httpGet;
    private int requestLimit;
    private String wfsFilter;

    @Override
    public void startDataSource() {
        initializeHttpResources();
        executeDataSourceThread();
    }

    @Override
    public byte[] pollDataSource() {
        try {
            getLogger().debug("Polling data source for feed: '" + getFeedName() + "'.");
            CloseableHttpResponse closeableHttpResponse = getHttpClient().execute(getHttpGet());
            HttpEntity responseEntity = closeableHttpResponse.getEntity();
            byte[] responseBytes = EntityUtils.toByteArray(responseEntity);
            EntityUtils.consume(responseEntity);
            closeableHttpResponse.close();
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
    protected boolean canPoll() {
        return httpClient != null;
    }

    @Override
    protected void cleanUpConnections() {
        if (httpClient != null) {
            try {
                httpClient.close();//Close http client to avoid leaks
                httpClient = null;
            } catch (IOException e) {
                getLogger().error(e.getLocalizedMessage());
            }
        }
    }

    private void initializeHttpResources() {
        if (httpClient == null) {
            String requestString = buildRequestString();
            httpClient = HttpClientBuilder.create().build();
            httpGet = new HttpGet(requestString);
            getLogger().debug("Getting data from " + requestString);
        } else {
            getLogger().warn("Cannot initialize http resources. This can only be done once.");
        }
    }

    private String buildRequestString() {
        if (requestString == null) {
            requestString = new StringBuilder()
                    .append(getHostProtocol())
                    .append("://")
                    .append(getHostAddress())
                    .append("/")
                    .append(getBaseUrl())
                    .toString();
        }
        return new StringBuilder().append(requestString).append(buildWfsFilter()).toString();
    }

    protected final CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public final String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public final String getFeedName() {
        return feedName;
    }

    public void setFeedName(String feedName) {
        this.feedName = feedName;
    }

    protected final HttpGet getHttpGet() {
        return httpGet;
    }

    public final int getRequestLimit() {
        return requestLimit;
    }

    public void setRequestLimit(String requestLimit) {
        this.requestLimit = Integer.parseInt(requestLimit);
    }

    protected abstract String buildWfsFilter();

    public String getWfsFilter() {
        //Handle wfsFilter never being set
        return wfsFilter != null ? wfsFilter : "";
    }

    public void setWfsFilter(String[] wfsFilterParams) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String wfsFilterParam : wfsFilterParams) {
            stringBuilder.append("&");
            stringBuilder.append(wfsFilterParam);
        }
        this.wfsFilter = stringBuilder.toString();
    }
}



