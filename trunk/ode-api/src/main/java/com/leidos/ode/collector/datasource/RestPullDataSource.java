package com.leidos.ode.collector.datasource;

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



