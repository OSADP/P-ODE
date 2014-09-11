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
    private Logger logger = Logger.getLogger(TAG);
    private String hostProtocol;
    private String hostAddress;
    private int hostPort;
    private String username;
    private String password;
    private boolean interrupted;
    private Thread dataSourceThread;

    public String getHostProtocol() {
        return hostProtocol;
    }

    public void setHostProtocol(String hostProtocol) {
        this.hostProtocol = hostProtocol;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public int getHostPort() {
        return hostPort;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private boolean isInterrupted() {
        return interrupted;
    }

    @Override
    public void stopDataSource() {
        stopDataSourceThread();
    }

    /**
     * Returns a byte array containing the data retrieve from the source. This method
     * should not be called directly! For use by the DataSourceRunnable only. Data
     * sources should implement this method with logic for retrieving data from the
     * source.
     *
     * @return the data from the source
     */
    protected abstract byte[] pollDataSource();

    /**
     * Returns logger for the data source.
     *
     * @return
     */
    protected abstract Logger getLogger();

    /**
     * Creates a new DataSource thread for retrieving data from the source;
     * then executes the thread. Only one thread per DataSource is allowed. Asynchronously
     * sends received data to the specified listener.
     *
     * @param collectorDataSourceListener the listener for data received
     */
    protected final void executeDataSourceThread(CollectorDataSourceListener collectorDataSourceListener) {
        stopDataSourceThread();
        dataSourceThread = new Thread(new DataSourceRunnable(collectorDataSourceListener));
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
            interrupted = true;
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
                    getCollectorDataSourceListener().onDataReceived(bytes);
                } else {
                    logger.debug("Data source response bytes was null!.");
                }
            }
        }
    }
}
