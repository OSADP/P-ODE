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
    private String trustStoreType;
    private String trustStore;
    private String trustStorePassword;
    private boolean loadCert;

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
        trustStore = bundle.getString("rde.truststore");
        trustStoreType = bundle.getString("rde.truststoretype");
        trustStorePassword = bundle.getString("rde.truststorepassword");
        loadCert = Boolean.parseBoolean(bundle.getString("rde.loadcert"));
    }
    
    public void setURL(String URL) {
        this.url = URL;
    }

    
    public String getURL() {
        return url;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }
    
    public boolean isUseSSL() {
        return useSSL;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    
    public String getUsername() {
        return username;
    }

    
    public void setPassword(String password) {
        this.password = password;
    }

    
    public String getPassword() {
        return password;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setTimeout(int timeOut) {
        this.timeout = timeOut;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetryInterval(long millis) {
        this.retryInterval = millis;
    }

    public long getRetryInterval() {
        return retryInterval;
    }

    public void setRDEClientMode(RDEClientMode mode) {
        this.clientMode = mode;
    }

    public RDEClientMode getRDEClientMode() {
        return clientMode;
    }

    public void setSocketTimeout(int timeout) {
        socketTimeout = timeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public String getTrustStoreType() {
        return trustStoreType;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public Boolean getLoadCert() {
        return loadCert;
    }

}
