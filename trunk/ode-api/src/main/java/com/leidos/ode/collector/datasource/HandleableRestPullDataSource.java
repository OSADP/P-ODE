package com.leidos.ode.collector.datasource;

import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/16/14
 * Time: 11:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class HandleableRestPullDataSource extends RestPullDataSource {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private List<RestPullDataSource> restPullDataSources;
    private int index;

    @Override
    public void startDataSource(CollectorDataSourceListener collectorDataSourceListener) throws DataSourceException {
        if (getRestPullDataSources() != null && !getRestPullDataSources().isEmpty()) {
            for (RestPullDataSource restPullDataSource : getRestPullDataSources()) {
                restPullDataSource.startDataSource(null);
            }
        }
        /*Each data source in the list is responsible for its own listener, so execute
        this source's thread with a listener.
         */
        executeDataSourceThread();
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
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void cleanUpConnections() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

