package com.leidos.ode.collector.datasource;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 7/14/14
 * Time: 5:39 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class RestPullDataSource extends PullDataSource {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private String baseUrl;
    private String feedName;
    private String requestString;
    private CloseableHttpClient httpClient;
    private HttpGet httpGet;
    private int requestLimit;

    protected String buildRequestString() {
        if (requestString == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getHostProtocol());
            stringBuilder.append("://");
            stringBuilder.append(getHostAddress());
            stringBuilder.append("/");
            stringBuilder.append(getBaseUrl());
            requestString = stringBuilder.toString();
        }
        return new StringBuilder().append(requestString).append(getWFSFilter()).toString();
    }

    @Override
    public void startDataSource() throws DataSourceException {
        String requestString = buildRequestString();
        httpClient = HttpClientBuilder.create().build();
        httpGet = new HttpGet(requestString);
        logger.debug("Getting data from " + requestString);
    }

    protected final CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    protected final HttpGet getHttpGet() {
        return httpGet;
    }

    protected abstract String getWFSFilter();

    public final String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            this.baseUrl = baseUrl;
        }
    }

    public final String getFeedName() {
        return feedName;
    }

    public void setFeedName(String feedName) {
        if (feedName == null) {
            this.feedName = feedName;
        }
    }

    public final int getRequestLimit() {
        return requestLimit;
    }

    public void setRequestLimit(String requestLimit) {
        if (requestLimit == null) {
            this.requestLimit = Integer.parseInt(requestLimit);
        }
    }
}



