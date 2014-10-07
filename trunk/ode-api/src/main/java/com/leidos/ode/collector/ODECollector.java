package com.leidos.ode.collector;

import com.leidos.ode.agent.ODEAgent;
import com.leidos.ode.agent.datatarget.ODEDataTarget.DataTargetException;
import com.leidos.ode.collector.datasource.CollectorDataSource;
import com.leidos.ode.collector.datasource.CollectorDataSource.CollectorDataSourceListener;
import com.leidos.ode.collector.datasource.CollectorDataSource.DataSourceException;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;

public class ODECollector {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private CollectorDataSource dataSource;
    private ODEAgent agent;
    private CollectorDataSourceListener odeCollectorDataSourceListener;
    private CollectorDataSourceListener externalCollectorDataSourceListener;

    @PostConstruct
    private void initialize() {
        odeCollectorDataSourceListener = new CollectorDataSourceListener() {
            @Override
            public void onDataReceived(byte[] receivedData) {
                if (getAgent() != null) {
                    getAgent().processMessage(receivedData);
                }
                if (externalCollectorDataSourceListener != null) {
                    externalCollectorDataSourceListener.onDataReceived(receivedData);
                }
            }
        };
    }

    /**
     * Starts the collector. Data callbacks are only available to the collector.
     *
     * @throws DataSourceException
     * @throws DataTargetException
     */
    public void startUp() throws DataSourceException, DataTargetException {
        startUp(null);
    }

    /**
     * Starts the collector with an external listener for callbacks.
     *
     * @param externalCollectorDataSourceListener
     *
     * @throws DataSourceException
     * @throws DataTargetException
     */
    public void startUp(CollectorDataSourceListener externalCollectorDataSourceListener) throws DataSourceException, DataTargetException {
        this.externalCollectorDataSourceListener = externalCollectorDataSourceListener;
        startCollector();
    }

    private void startCollector() throws DataSourceException, DataTargetException {
        if (getAgent() != null) {
            getAgent().startUp();
            if (getDataSource() != null) {
                getDataSource().startDataSource(odeCollectorDataSourceListener);
                getLogger().debug("Started collector.");
            } else {
                getLogger().error("Unable to start collector. Data source is null!");
            }
        } else {
            getLogger().error("Unable to start collector. Agent is null!");
        }
    }

    public void stop() {
        if (getDataSource() != null) {
            getDataSource().stopDataSource();
            getLogger().debug("Stopped data source.");
        }
    }

    public CollectorDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(CollectorDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ODEAgent getAgent() {
        return agent;
    }

    public void setAgent(ODEAgent agent) {
        this.agent = agent;
    }

    private Logger getLogger() {
        return logger;
    }
}
