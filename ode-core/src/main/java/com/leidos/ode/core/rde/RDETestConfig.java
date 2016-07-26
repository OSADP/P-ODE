package com.leidos.ode.core.rde;

import org.dot.rdelive.api.Datum;
import org.dot.rdelive.client.api.RDEClientConfig;
import org.dot.rdelive.client.api.RDEClientMode;
import org.dot.rdelive.impl.GenericDatum;

import java.util.ResourceBundle;

/**
 * Mock config because the test case is having some issues reading the properties bundle.
 */
public class RDETestConfig implements RDEClientConfig<GenericDatum<char[]>, char[]> {
    private String url = "[REDACTED]";
    private int port = 12090;
    private boolean useSSL = true;
    private String username = "[REDACTED]";
    private String password = "[REDACTED]";
    private String appName = "podetest";
    private int timeout = 1000;
    private int retries = 10;
    private long retryInterval = 10;
    private RDEClientMode clientMode = RDEClientMode.DATAOUT;
    private int socketTimeout=1000;

    @Override
    public void setURL(String URL) {
        this.url = URL;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    @Override
    public boolean isUseSSL() {
        return useSSL;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String getAppName() {
        return appName;
    }

    @Override
    public void setTimeout(int timeOut) {
        this.timeout = timeOut;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public void setRetries(int retries) {
        this.retries = retries;
    }

    @Override
    public int getRetries() {
        return retries;
    }

    @Override
    public void setRetryInterval(long millis) {
        this.retryInterval = millis;
    }

    @Override
    public long getRetryInterval() {
        return retryInterval;
    }

    @Override
    public void setRDEClientMode(RDEClientMode mode) {
        this.clientMode = mode;
    }

    @Override
    public RDEClientMode getRDEClientMode() {
        return clientMode;
    }

    @Override
    public void setSocketTimeout(int timeout) {
        this.socketTimeout = timeout;
    }

    @Override
    public int getSocketTimeout() {
        return socketTimeout;
    }

    @Override
    public String toString() {
        return "RDETestConfig{" +
                "url='" + url + '\'' +
                ", port=" + port +
                ", useSSL=" + useSSL +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", appName='" + appName + '\'' +
                ", timeout=" + timeout +
                ", retries=" + retries +
                ", retryInterval=" + retryInterval +
                ", clientMode=" + clientMode +
                ", socketTimeout=" + socketTimeout +
                '}';
    }
}
