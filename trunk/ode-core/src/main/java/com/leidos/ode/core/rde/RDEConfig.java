package com.leidos.ode.core.rde;

import org.dot.rdelive.api.Datum;
import org.dot.rdelive.client.api.RDEClientConfig;
import org.dot.rdelive.client.api.RDEClientMode;

import java.util.ResourceBundle;

/**
 * Provides access to the configuration data for the RDE
 */
public class RDEConfig implements RDEClientConfig<Datum<char[]>, char[]> {
    private String filename;
    private ResourceBundle bundle;
    private String url;
    private int port;
    private boolean useSSL;
    private String username;
    private String password;
    private String appName;
    private int timeout;
    private int retries;
    private long retryInterval;
    private RDEClientMode clientMode;
    private int socketTimeout;

    public RDEConfig(String filename) {
        // Open the file
        this.filename = filename;
        bundle = ResourceBundle.getBundle(filename);

        // Load the values from the file into this configuration object
        url = bundle.getString("rde.url");
        port = Integer.parseInt(bundle.getString("rde.port"));
        useSSL = Boolean.parseBoolean(bundle.getString("rde.useSSL"));
        username = bundle.getString("rde.username");
        password = bundle.getString("rde.password");
        appName = bundle.getString("rde.appname");
        timeout = Integer.parseInt(bundle.getString("rde.timeout"));
        retries = Integer.parseInt(bundle.getString("rde.retries"));
        retryInterval = Long.parseLong(bundle.getString("rde.retryinterval"));
        clientMode = RDEClientMode.valueOf(bundle.getString("rde.clientmode"));
        socketTimeout = Integer.parseInt(bundle.getString("rde.sockettimeout"));
    }

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
        socketTimeout = timeout;
    }

    @Override
    public int getSocketTimeout() {
        return socketTimeout;
    }
}
