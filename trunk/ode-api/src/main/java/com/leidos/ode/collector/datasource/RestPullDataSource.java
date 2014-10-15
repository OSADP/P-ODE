package com.leidos.ode.collector.datasource;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;

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
    private String wfsFilter;

    @Override
    public void startDataSource(CollectorDataSourceListener collectorDataSourceListener) throws DataSourceException {
        initializeHttpResources();
    }

    @Override
    public void stopDataSource() {
        super.stopDataSource();//Must call super here to stop the thread
        if (getHttpClient() != null) {
            try {
                getHttpClient().close();//Close http client to avoid leaks
            } catch (IOException e) {
                getLogger().error(e.getLocalizedMessage());
            }
        }
    }

    private void initializeHttpResources() {
        String requestString = buildRequestString();
        httpClient = HttpClientBuilder.create().build();
        httpGet = new HttpGet(requestString);
        logger.debug("Getting data from " + requestString);
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

    protected final HttpGet getHttpGet() {
        return httpGet;
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

    public final String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Sets the base url for this data source.
     *
     * @param baseUrl
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public final String getFeedName() {
        return feedName;
    }

    /**
     * Sets the feed name for this data source.
     *
     * @param feedName
     */
    public void setFeedName(String feedName) {
        this.feedName = feedName;
    }

    public final int getRequestLimit() {
        return requestLimit;
    }

    /**
     * Sets the request interval limit for this data source.
     *
     * @param requestLimit
     */
    public void setRequestLimit(String requestLimit) {
        this.requestLimit = Integer.parseInt(requestLimit);
    }
}



