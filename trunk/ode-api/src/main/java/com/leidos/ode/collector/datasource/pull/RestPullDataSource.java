package com.leidos.ode.collector.datasource.pull;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

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
    public void startDataSource(CollectorDataSourceListener collectorDataSourceListener) throws DataSourceException {
        initializeHttpResources();
    }

    @Override
    protected void cleanUpConnections() {
        if (getHttpClient() != null) {
            try {
                getHttpClient().close();//Close http client to avoid leaks
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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getHostProtocol());
            stringBuilder.append("://");
            stringBuilder.append(getHostAddress());
            stringBuilder.append("/");
            stringBuilder.append(getBaseUrl());
            requestString = stringBuilder.toString();
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



