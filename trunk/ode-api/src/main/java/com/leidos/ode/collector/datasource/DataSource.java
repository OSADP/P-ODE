package com.leidos.ode.collector.datasource;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 9/5/14
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DataSource implements CollectorDataSource {

    private String hostProtocol;
    private String hostAddress;
    private int hostPort;
    private String username;
    private String password;

    private boolean interrupted;

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

    protected final boolean isInterrupted() {
        return interrupted;
    }

    @Override
    public final void stopDataSource() {
        interrupted = true;
    }

    protected abstract class DataSourceRunnable implements Runnable{
        private CollectorDataSourceListener collectorDataSourceListener;

        protected DataSourceRunnable(CollectorDataSourceListener collectorDataSourceListener) {
            this.collectorDataSourceListener = collectorDataSourceListener;
        }

        protected final CollectorDataSourceListener getCollectorDataSourceListener(){
            return collectorDataSourceListener;
        }
    }
}
