package com.leidos.ode.collector;

import com.leidos.ode.agent.BasicODEAgent;
import com.leidos.ode.agent.ODEAgent;
import com.leidos.ode.agent.datatarget.ODEDataTarget.DataTargetException;
import com.leidos.ode.collector.datasource.CollectorDataSource;
import com.leidos.ode.collector.datasource.CollectorDataSource.CollectorDataSourceListener;
import com.leidos.ode.collector.datasource.CollectorDataSource.DataSourceException;
import org.apache.log4j.Logger;

public class ODECollector implements CollectorDataSourceListener {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private BasicODEAgent agent;
    private CollectorDataSource dataSource;
    private ODEAgent.MessageListener messageListener;

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
     * @param messageListener
     * @throws DataSourceException
     * @throws DataTargetException
     */
    public void startUp(ODEAgent.MessageListener messageListener) throws DataSourceException, DataTargetException {
        this.messageListener = messageListener;
        startCollector();
    }

    protected void startCollector() throws DataSourceException, DataTargetException {
        if (getAgent() != null) {
            getAgent().startUp(messageListener);
            if (getDataSource() != null) {
                getDataSource().startDataSource();
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

    public BasicODEAgent getAgent() {
        return agent;
    }

    public void setAgent(BasicODEAgent agent) {
        this.agent = agent;
    }

    public CollectorDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(CollectorDataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Logger getLogger() {
        return logger;
    }

    @Override
    public void onDataReceived(byte[] receivedData) {
        if (getAgent() != null) {
            getAgent().processMessage(receivedData);
        }
    }
}
