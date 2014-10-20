package com.leidos.ode.collector.datasource;


import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 9/5/14
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DataSource implements CollectorDataSource {

    private final String TAG = getClass().getSimpleName();
    private final Logger logger = Logger.getLogger(TAG);
    private String hostProtocol;
    private String hostAddress;
    private int hostPort;
    private String username;
    private String password;
    private boolean interrupted;
    private Thread dataSourceThread;
    private CollectorDataSourceListener collectorDataSourceListener;

    protected final String getHostProtocol() {
        return hostProtocol;
    }

    public final void setHostProtocol(String hostProtocol) {
        this.hostProtocol = hostProtocol;
    }

    protected final String getHostAddress() {
        return hostAddress;
    }

    public final void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    protected final int getHostPort() {
        return hostPort;
    }

    public final void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    protected final String getUsername() {
        return username;
    }

    public final void setUsername(String username) {
        this.username = username;
    }

    protected final String getPassword() {
        return password;
    }

    public final void setPassword(String password) {
        this.password = password;
    }

    protected final boolean isInterrupted() {
        return interrupted;
    }

    public final CollectorDataSourceListener getCollectorDataSourceListener() {
        return collectorDataSourceListener;
    }

    public final void setCollectorDataSourceListener(CollectorDataSourceListener collectorDataSourceListener) {
        if (getCollectorDataSourceListener() == null) {
            this.collectorDataSourceListener = collectorDataSourceListener;
            getLogger().debug("Set collector data source listener for this data source.");
        } else {
            getLogger().warn("Cannot set listener. Listener has already been set for this data source.");
        }
    }

    @Override
    public final void stopDataSource() {
        interrupted = true;
        stopDataSourceThread();
    }

    protected abstract void cleanUpConnections();

    /**
     * Returns a byte array containing the data retrieve from the source. Data
     * sources should implement this method with logic for retrieving data from the
     * source. This method should not be called directly, unless by a
     * RestrictedRequestIntervalRestPullDataSource. Otherwise, for use by the DataSourceRunnable only.
     *
     * @return the data from the source
     */
    public abstract byte[] pollDataSource();

    /**
     * Returns logger for the data source.
     *
     * @return
     */
    protected final Logger getLogger() {
        return logger;
    }

    /**
     * Creates a new DataSource thread for retrieving data from the source;
     * then executes the thread. Only one thread per DataSource is allowed. Asynchronously
     * sends received data to the specified listener.
     */
    protected final void executeDataSourceThread() {
        stopDataSourceThread();
        dataSourceThread = new Thread(new DataSourceRunnable(getCollectorDataSourceListener()));
        interrupted = false;
        dataSourceThread.start();
        logger.debug("Started data source thread.");
    }

    /**
     * Stops the DataSource thread.
     */
    private void stopDataSourceThread() {
        if (dataSourceThread != null) {
            logger.debug("Stopping data source thread.");
            dataSourceThread.interrupt();
            dataSourceThread = null;
        }
    }

    /**
     * Class to handle polling of data source. Notifies listener of data received.
     */
    private class DataSourceRunnable implements Runnable {
        private CollectorDataSourceListener collectorDataSourceListener;

        public DataSourceRunnable(CollectorDataSourceListener collectorDataSourceListener) {
            this.collectorDataSourceListener = collectorDataSourceListener;
        }

        public CollectorDataSourceListener getCollectorDataSourceListener() {
            return collectorDataSourceListener;
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                byte[] bytes = pollDataSource();
                if (bytes != null) {
                    if (getCollectorDataSourceListener() != null) {
                        getCollectorDataSourceListener().onDataReceived(bytes);
                    } else {
                        logger.warn("Data source listener was null! Listener will not be notified.");
                    }
                } else {
                    logger.debug("Data source response bytes was null!.");
                }
            }
            logger.debug("Cleaning up connections.");
            cleanUpConnections();
        }
    }
}
