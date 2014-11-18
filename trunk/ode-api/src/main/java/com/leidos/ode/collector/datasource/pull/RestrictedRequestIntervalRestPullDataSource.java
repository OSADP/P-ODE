package com.leidos.ode.collector.datasource.pull;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/16/14
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class RestrictedRequestIntervalRestPullDataSource extends RestPullDataSource {

    private List<RestPullDataSource> restPullDataSources;
    private int index;

    @Override
    public void startDataSource() {
        if (getRestPullDataSources() != null && !getRestPullDataSources().isEmpty()) {
            for (RestPullDataSource restPullDataSource : getRestPullDataSources()) {
                restPullDataSource.startDataSource();
            }
        }
    }

    @Override
    protected String buildWfsFilter() {
        return "";
    }

    @Override
    public byte[] pollDataSource() {
        if (getRestPullDataSources() != null && !getRestPullDataSources().isEmpty()) {
            if (index > getRestPullDataSources().size() - 1) {
                index = 0;
            }
            RestPullDataSource currentDataSource = getRestPullDataSources().get(index);
            if (currentDataSource != null) {
                try {
                    index++;
                    byte[] bytes = currentDataSource.pollDataSource();
                    if (bytes != null) {
                        if (currentDataSource.getCollectorDataSourceListener() != null) {
                            currentDataSource.getCollectorDataSourceListener().onDataReceived(bytes);
                            getLogger().debug("Notified data source listener for feed '" + currentDataSource.getFeedName() + "'.");
                        } else {
                            getLogger().warn("Data source listener was 'null' for feed: '" + currentDataSource.getFeedName() + "'. Listener will not be notified.");
                        }
                    } else {
                        getLogger().debug("Data source response bytes were 'null' for feed: '" + getFeedName() + "'.");
                    }
                    Thread.sleep(getRequestLimit());
                    return bytes;
                } catch (InterruptedException e) {
                    getLogger().error(e.getLocalizedMessage());
                }
            }
        }
        return null;
    }

    public List<RestPullDataSource> getRestPullDataSources() {
        return restPullDataSources;
    }

    public void setRestPullDataSources(List<RestPullDataSource> restPullDataSources) {
        this.restPullDataSources = restPullDataSources;
    }

    @Override
    protected void cleanUpConnections() {
        if (getRestPullDataSources() != null && !getRestPullDataSources().isEmpty()) {
            getLogger().debug("Cleaning up data source connections.");
            for (RestPullDataSource restPullDataSource : getRestPullDataSources()) {
                restPullDataSource.cleanUpConnections();
            }
        }
    }
}

