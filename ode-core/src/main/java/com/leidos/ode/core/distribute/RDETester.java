/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.core.distribute;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.leidos.ode.data.DFullTime;
import com.leidos.ode.data.GeoRegion;
import com.leidos.ode.data.PodeReplayData;
import com.leidos.ode.data.Position3D;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.dot.rdeapi.client.websocket.sockjs.ClientWebSocketHandler;
import org.dot.rdeapi.client.websocket.sockjs.RDESockJsClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompEncoder;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Base64Utils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.sockjs.client.SockJsClient;

/**
 *
 * @author cassadyja
 */
public class RDETester {
    // java -Djavax.net.ssl.trustStoreType=PKCS12 -Djavax.net.ssl.trustStore="C:/Jim/ode/rde.indrasoft.net.p12" -Djavax.net.ssl.trustStorePassword=rde_2015 -cp ode-core-1.0-SNAPSHOT.jar;../lib/rdeapiclient-3.1.32-SNAPSHOT-jar-with-dependencies.jar com.leidos.ode.core.distribute.RDETester



    //-Djavax.net.ssl.trustStoreType=PKCS12 -Djavax.net.ssl.trustStore="C:\Jim\ode\rde.indrasoft.net.p12"
        // Logging
    private static final String TAG = RDETester.class.getSimpleName();
    protected static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TAG);
    
    private static ClientWebSocketHandler clientWebSocketHandler = null;
    private static final int PACKET_TX_DELAY = 100;
//    private static final String RDE_QUERY_RESULT_DESTINATION = "/query/result/";
//    private static final String RDE_QUERY_DESTINATION = "/app/api/";
    private static final String RDE_QUERY_RESULT_DESTINATION = "/query/result/P-ODE";
    private static final String RDE_QUERY_DESTINATION = "/query/result/P-ODE";
    private static final String RDE_API_BASE_URL = "wss://rde.indrasoft.net/api";    
    
    
    private static String subscriptionId = "ABC123";
    
    public static void main(String args[]){
        
        runTest();
        
        
        
        
    }
    
    
    public static void runTest(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RDESockJsClient.class);
        clientWebSocketHandler = context.getBean(ClientWebSocketHandler.class);
        // Construct and send the query to the RDE over STOMP
        SockJsClient sockJsClient = context.getBean(SockJsClient.class);
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add(WebSocketHttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString("podeuser:P0dep@ssword".getBytes()));
        try {
            log.debug("Sending RDE Handshake");
//            ListenableFuture<WebSocketSession> future = sockJsClient.doHandshake(this, headers, new URI(RDE_API_BASE_URL));
            ListenableFuture<WebSocketSession> future = sockJsClient.doHandshake(clientWebSocketHandler, headers, new URI(RDE_API_BASE_URL));
            // Handshake with the Websocket server
            
            log.debug("Getting webSocketSession");
            WebSocketSession webSocketSession = future.get();
            log.debug("Sending RDE Subscription Message");
            webSocketSession.sendMessage(new TextMessage(createSubscribeMessage()));
            
            

            Thread.sleep(5000); // Unsure if this is necessary

            log.debug("Sending RDE Query");
            // Send the query
//            webSocketSession.sendMessage(new TextMessage(createQueryMessage()));
            log.info("ReplayDataDistributor " + subscriptionId + " query sent to RDE.");

            
            // Wait until we've received all the messages
            while(!clientWebSocketHandler.isRecievedMessage()){
                Thread.sleep(100);
            }
                        
            
            
            
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    

    private static byte[] createSubscribeMessage() {
        StompHeaderAccessor headers = StompHeaderAccessor
                .create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId(subscriptionId);
        headers.setDestination(RDE_QUERY_RESULT_DESTINATION);
        log.debug("RDE Subscription Headers: "+headers.toString());
        Message<byte[]> message1 = MessageBuilder
                .withPayload(ByteBuffer.allocate(0).array())
                .setHeaders(headers).build();
        return new StompEncoder().encode(message1);
    }    
    
    
    
}
