package com.leidos.ode.collector.datasource.pull;

import com.leidos.ode.collector.datasource.DataSource;
import com.leidos.ode.collector.datasource.pull.sdc.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Situation Data Clearinghouse data source
 *
 * Communicates with the SDC VSD subscription interface via websockets.
 */
public class SDCDataSource extends DataSource {
    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private WarehouseConfig warehouseConfig;
    private LinkedBlockingQueue<byte[]> messages = new LinkedBlockingQueue<>();
    private SDCQuery query;
    private WarehouseClient wsClient;

    public SDCDataSource(WarehouseConfig warehouseConfig, SDCQuery sdcQuery) {
        this.warehouseConfig = warehouseConfig;
        this.query = sdcQuery;
    }

    private Instant lastActivityAt = Instant.now();
    private static final int RECONNECT_PERIOD = 30;

    public byte[] pollDataSource() {
        logger.info("Polling SDC data source.");

        try {
            byte[] data = messages.poll(5, TimeUnit.MINUTES);

            if (data != null) {
                lastActivityAt = Instant.now();
            }

            Instant now = Instant.now();
            if (Duration.between(lastActivityAt, now).toMinutes() > RECONNECT_PERIOD) {
                // Refresh the connection with the SDC
                cleanUpConnections();
                connectToSDC();
            }

            return data;
        } catch (InterruptedException e) {
            logger.warn("Unable to pull data off of SDC data queue!");
        }

        return null;
    }

    @Override
    protected boolean canPoll() {
        return true;
    }

    @Override
    protected void cleanUpConnections() {
        logger.info("Cleaning up SDC connections.");
        if (wsClient != null) {
            wsClient.close();
        }
    }

    public void startDataSource() {
        logger.info("Starting SDC websockets datasource.");
        executeDataSourceThread();
        connectToSDC();
    }

    private void connectToSDC() {
        try {
            warehouseConfig.postLoadCalculateValues();
            logger.info(warehouseConfig);

            logger.info("Logging into SDC CAS.");
            CASClient casClient = CASClient.configure(warehouseConfig);
            String jSessionID = casClient.login();
            warehouseConfig.jSessionID = jSessionID;

            ResponseHandler handler = new ResponseHandler(warehouseConfig, messages);
            wsClient = WarehouseClient.configure(warehouseConfig, handler, warehouseConfig.jSessionID, query);
            logger.info("Opening WebSocket to " + warehouseConfig.warehouseURL);
            wsClient.connect();
            lastActivityAt = Instant.now();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (CASClient.CASLoginException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:/META-INF/ODE-API-Context.xml");
        SDCDataSource ds =  (SDCDataSource) ctx.getBean("sdcDataSource");
        ds.startDataSource();
    }
}
