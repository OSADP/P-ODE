/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.collector.datasource.pull;


import com.leidos.ode.collector.datasource.push.PushDataSource;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author cassadyja
 */

//public class VDOTBSMDataSource {
public class VDOTBSMDataSource extends PushDataSource implements Runnable{
    private final String TAG = getClass().getSimpleName();
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TAG);
    
    private static final String WEB_SOCKET_PROTOCOL="wss://";
    private static final String HTTP_PROTOCOL="https://";
    private String baseURL = "vcc-api.vtti.vt.edu/";
    private String keyURL = "api/bsm/key";
    private String bsmURL = "ws/bsm?rse_id=1,2,3,4,5,6,7&key=";
    private String userCreds = "MXdzckdQaTExQU1tMlB3a3BLM1ZMYzdLd1VKcjJCRTk2aEJXeHJHTmhGTmlSS3NhTGpRb3hBcnY=";

    public String getUserCreds() {
        return userCreds;
    }

    public void setUserCreds(String userCreds) {
        this.userCreds = userCreds;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getKeyURL() {
        return keyURL;
    }

    public void setKeyURL(String keyURL) {
        this.keyURL = keyURL;
    }

    public String getBsmURL() {
        return bsmURL;
    }

    public void setBsmURL(String bsmURL) {
        this.bsmURL = bsmURL;
    }
    
    
    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
    
    private boolean connected = false;
    private VDOTWebSocketHandler handler;
    
    
    public static void main(String[] args){
        VDOTBSMDataSource datasource = new VDOTBSMDataSource();
        datasource.startDataSource();
//        new Thread(datasource).start();
        
        
        
    }
    
    @Override
    public void run(){
        startDataSource();
    }
    
    
    private class VDOTWebSocketHandler implements WebSocketListener{
        private Session session;
        private final CountDownLatch closeLatch = new CountDownLatch(1);

        public Session getSession(){
            return session;
        }
        
        public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
            return this.closeLatch.await(duration, unit);
        }    
        
        @OnWebSocketError
        public void onError(Session session, Throwable t){
            System.out.println("Error: "+t.toString());
        }


        public void onWebSocketBinary(byte[] bytes, int i, int i1) {
            System.out.println("Binary Received");
        }

        public void onWebSocketText(String string) {
            logger.debug("Received VDOT BSMs: "+string);

            if (!"{ \"bsm\": [] }".equals(string)) {
                queue.add(string);
                logger.debug("queue size ["+queue.size()+"]");
            } else {
                logger.debug("No BSMs reported by VDOT BSM source.");
            }

        }

        public void onWebSocketClose(int i, String string) {
            System.out.println("Closing socket");
        }

        public void onWebSocketConnect(Session sn) {
            this.session = sn;
            connected = true;
            logger.debug("Have VDOT web socket Session");
            executeDataSourceThread();
        }

        public void onWebSocketError(Throwable thrwbl) {
            logger.error("Web Socket Error",thrwbl);
        }
        
        
        
    }

    @Override
    public byte[] pollDataSource() {
        logger.debug("Polling VDOT BSM Data source, queue size ["+queue.size()+"]");
        String current = null;

        try {
            current = queue.poll(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.warn("VDOT BSM source interrupted while waiting for message!");
        }
        
        if(current != null){
            logger.debug("Returning BSMs, queue size ["+queue.size()+"]");
            return current.getBytes();
        }else{
            logger.debug("Returning nothing");
            return null;
        }
    }

    @Override
    protected boolean canPoll() {
        return connected;
    }

    @Override
    protected void cleanUpConnections() {
        if(handler != null && handler.getSession() != null){
            handler.getSession().close();
        }
    }

    public void startDataSource() {

//        String destUri = "wss://vcc-api.vtti.vt.edu";
        
        SslContextFactory ssl = new SslContextFactory();
        WebSocketClient client = new WebSocketClient(ssl);
        handler = new VDOTWebSocketHandler();
        try {
            logger.debug("Getting VDOT BSM Key");
            CloseableHttpClient httpClient;
            HttpGet httpGet;
            httpClient = HttpClientBuilder.create().build();
            httpGet = new HttpGet(HTTP_PROTOCOL+baseURL+keyURL);
            Header header = new BasicHeader("Authorization", "Basic "+userCreds);
            httpGet.addHeader(header);
            CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpGet);
            HttpEntity responseEntity = closeableHttpResponse.getEntity();
            byte[] responseBytes = EntityUtils.toByteArray(responseEntity);
            EntityUtils.consume(responseEntity);            
            String key = new String(responseBytes);
            
            String destUri = WEB_SOCKET_PROTOCOL+baseURL+bsmURL;
            
            client.start();
            URI echoUri = new URI(destUri+key);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            
            
            
            request.setHeader("Authorization", "Basic "+userCreds);
            client.connect(handler, echoUri, request);
            logger.debug("Connecting to : %s%n "+ echoUri);
            
        } catch (Throwable t) {
            logger.error("Error starting data source",t);
        } finally {
        }        
    }
    
}
