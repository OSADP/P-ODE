package com.leidos.ode.collector.datasource;

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

    private String protocol;
    private String baseUrl;
    private String feedName;

    private String requestString;

    @Override
    public void startDataSource() throws DataSourceException {
    }

    protected String buildRequestString() {
        if (requestString == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getProtocol());
            stringBuilder.append("://");
            stringBuilder.append(getSourceAddress());
            stringBuilder.append("/");
            stringBuilder.append(getBaseUrl());
            requestString = stringBuilder.toString();
        }
        return new StringBuilder().append(requestString).append(getWFSFilter()).toString();
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }

    protected abstract String getWFSFilter();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getFeedName() {
        return feedName;
    }

    public void setFeedName(String feedName) {
        this.feedName = feedName;
    }
}



