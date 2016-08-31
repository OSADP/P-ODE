package com.leidos.ode.core.rde;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.leidos.ode.data.*;
import com.leidos.ode.util.ByteUtils;
import org.dot.rdeapi.client.websocket.sockjs.ClientWebSocketHandler;
import org.dot.rdeapi.client.websocket.sockjs.PodeQueryResult;
import org.dot.rdeapi.client.websocket.sockjs.RDEQueryResult;
import org.dot.rdeapi.client.websocket.sockjs.RDESockJsClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompDecoder;
import org.springframework.messaging.simp.stomp.StompEncoder;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.Base64Utils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.sockjs.client.SockJsClient;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Replay Data Distributor for RDE query/replay data.
 *
 * Communicates with the RDE servers via Websockets and SockJS then returns the data
 *
 * TODO: Clean up error handling
 * TODO: Validate packet delay mechanism
 * TODO: Integration test
 * TODO: Verify SockJsClient async model
 * TODO: Base64 encode RDE messages in RDEDistributor.java
 */
public abstract class ReplayDataDistributor extends TextWebSocketHandler implements Runnable {
    // Constants
    private static final int PACKET_TX_DELAY = 100;
    private static final String RDE_QUERY_RESULT_DESTINATION = "/query/result/P-ODE";
    private static final String RDE_QUERY_DESTINATION = "/app/api/P-ODE";
    private static final String RDE_API_BASE_URL = "ws://its-rde.net/rdeapi/api";


    // PODE Subscription Data
    protected PodeSubscriptionRequest subRequest;
    protected ServiceRequest srvRequest;
    protected String subscriptionId;

    private Socket sock;

    // STOMP parsers/decoders
    private StompDecoder decoder;
    private ObjectReader resultReader;

    private List<PodeQueryResult> resultList;
    private boolean allMessagesReceived = false;
    private ClientWebSocketHandler clientWebSocketHandler = null;
    // For interrupting the thread
    private boolean isInterrupted;
    
    private BlockingQueue<RDEQueryResult> messageQueue;
    private WebSocketSession webSocketSession;
    // Logging
    private final String TAG = getClass().getSimpleName();
    protected final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TAG);

    
    private class RDEQueueListener implements Runnable{
        public void run() {
            resultList = new ArrayList<PodeQueryResult>();
            log.debug("Waiting for RDE messages.");
            while(!clientWebSocketHandler.isRecievedMessage()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    log.warn("Error sleeping",ex);
                }
            }
            log.debug("Received Messages from RDE... Processing");
            int i = 0;
            RDEQueryResult result = null;
            do{
                result = messageQueue.poll();
                if(result != null){
                    resultList.addAll(result.getFoundItems());
                    i++;
                }
            }while(result != null && !result.isLastMessage());

            try {
                closeRDEConnection();
            } catch (IOException ex) {
                log.warn("Error closing RDE Connection",ex);
            } catch (InterruptedException ex) {
                log.warn("Error closing RDE Connection",ex);
            }
            
            
            log.debug("Retrieved "+i+" message(s) from the RDE.");
            log.debug("\t Message(s) contained "+resultList.size()+" found items.");
            processRDEResults();
        }
        
        private void processRDEResults(){
//        // Ensure the results are sorted in time order
            Collections.sort(resultList, new Comparator<PodeQueryResult>() {
                public int compare(PodeQueryResult o1, PodeQueryResult o2) {
                    return Long.valueOf(o1.getDate()).compareTo(Long.valueOf(o2.getDate()));
                }
            });            
            
            // Transmit query results to waiting client with simulated delays


            if (resultList.size() == 0) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("RDE Results list: ");
            for (PodeQueryResult pqr : resultList) {
                sb.append("{");
                sb.append(pqr.toString());
                sb.append("},");
            }

            log.info(sb.toString());

            // Send first result immediately
            PodeQueryResult prev = pop();
            // Delay the first message until it actually occured in the query window
            if (subRequest != null) {
                PodeReplayData replayData = subRequest.getType().getReplayData();
                DFullTime startTime = replayData.getStartTime();
                Calendar convCal = new GregorianCalendar();

                convCal.set(startTime.getYear().getValue().intValue(), startTime.getMonth().getValue().intValue() - 1,
                        startTime.getDay().getValue().intValue(), startTime.getHour().getValue().intValue(),
                        startTime.getMinute().getValue().intValue());
                long startTimestamp = convCal.getTime().getTime();
                log.info("Delaying inital message " + (prev.getDate() - startTimestamp) + "ms");
                delayMessage(prev, startTimestamp, prev.getDate());
            }
            sendMessage(prev);

            // Iterate and delay the message appropriately
            PodeQueryResult cur;
            while (!isInterrupted && !resultList.isEmpty()) {
                cur = pop();

                delayMessage(cur, prev.getDate(), cur.getDate());
                // Delegate to our abstract method to send the message via whatever means
                sendMessage(cur);


                // Record our current message so we can appropriately delay the next message
                prev = cur;
            }
            cleanupConnection();
            log.info("ReplayDataDistributor " + subscriptionId + " finished sending data to client.");

        }
        
    }

    /**
     * Delay the message by the appropriate amount before sending it on to the client.
     *
     * @param result The message being delayed
     * @param prevDate Unix timestamp (ms resolution) of the previously sent message
     * @param curDate Unix timestamp (ms resolution) of the message being delayed.
     */
    protected void delayMessage(PodeQueryResult result, Long prevDate, Long curDate) {
        // Delay the message to match how it was originally recorded
        try {
            log.info("Sleeping "  + (Long.valueOf(curDate) - Long.valueOf(prevDate)) + "ms to deliver next message.");
            Thread.sleep(Long.valueOf(curDate) - Long.valueOf(prevDate));
        } catch (InterruptedException e) {
            log.error("Unable to delay replay data correctly.");
        }
    }

    public ReplayDataDistributor(String subscriptionId) {
        this.subscriptionId = subscriptionId;

        decoder = new StompDecoder();
        ObjectMapper objectMapper = new ObjectMapper();
        resultReader = objectMapper.reader(RDEQueryResult.class);
        resultList = new ArrayList<PodeQueryResult>();

        log.info("New ReplayDataDistributor created with id " + subscriptionId + ".");
    }

    public ReplayDataDistributor(PodeSubscriptionRequest subRequest, ServiceRequest srvRequest) {
        this.subRequest = subRequest;
        this.srvRequest = srvRequest;

        this.subscriptionId = ByteUtils.convertBytesToHex(subRequest.getRequestID());

        decoder = new StompDecoder();
        ObjectMapper objectMapper = new ObjectMapper();
        resultReader = objectMapper.reader(RDEQueryResult.class);
        resultList = new ArrayList<PodeQueryResult>();

        log.info("New ReplayDataDistributor created with id " + subscriptionId + ".");
    }

    /**
     * Interrupt the ReplayDataDistributor while it's sending packets to the client.
     * @param inter The interrupt status of the ReplayDataDistributor
     */
    public void setInterrupted(boolean inter) {
        this.isInterrupted = inter;
    }

    
    
    public void run() {
        log.info("ReplayDataDistributor " + subscriptionId + " running.");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RDESockJsClient.class);
        clientWebSocketHandler = context.getBean(ClientWebSocketHandler.class);
        messageQueue = (BlockingQueue<RDEQueryResult>) context.getBean("resultQueue");
        
        
        
        try {
            
            // Construct and send the query to the RDE over STOMP
            SockJsClient sockJsClient = context.getBean(SockJsClient.class);
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            headers.add(WebSocketHttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString("[REDACTED]".getBytes()));
            
            log.debug("Sending RDE Handshake");
//            ListenableFuture<WebSocketSession> future = sockJsClient.doHandshake(this, headers, new URI(RDE_API_BASE_URL));
            ListenableFuture<WebSocketSession> future = sockJsClient.doHandshake(clientWebSocketHandler, headers, new URI(RDE_API_BASE_URL));
            // Handshake with the Websocket server
            
            log.debug("Getting webSocketSession");
            webSocketSession = future.get();
            
            log.debug("WebSocketSession Local Address: "+webSocketSession.getLocalAddress());
            log.debug("WebSocketSession URI: "+webSocketSession.getUri().toString());
            
            
            log.debug("Sending RDE Subscription Message");
            webSocketSession.sendMessage(new TextMessage(createSubscribeMessage()));
            new Thread(new RDEQueueListener()).start();
            Thread.sleep(5000); // Unsure if this is necessary

            log.debug("Sending RDE Query");
            // Send the query
            log.info("ReplayDataDistributor query generation complete for " + subscriptionId + ": " + formatQueryRequest());
            webSocketSession.sendMessage(new TextMessage(createQueryMessage()));
            log.info("ReplayDataDistributor " + subscriptionId + " query sent to RDE.");
            log.info("ReplayDataDistributor " + subscriptionId + " finished query. Waiting for results" );
            
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException ex) {
            Logger.getLogger(ReplayDataDistributor.class.getName()).log(Level.SEVERE, null, ex);
        }


        // Connect to the client

        //TODO handle sending the messages somewhere else
        // Get the connection information from the service request
//        ConnectionPoint target = srvRequest.getDestination();
//        String targetIp = ByteUtils.buildIpAddressFromBytes(target.getAddress().getIpv4Address().getValue());
//        int targetPort = target.getPort().getValue();
//
////        try {
////            log.info("Attempting to connect to subscriber: "+targetIp+":"+targetPort);
//////            sock = new Socket(targetIp, targetPort);
////            log.info("ReplayDataDistributor " + subscriptionId + " connected to client (" + targetIp + ":" + targetPort + ").");
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//        // Ensure the results are sorted in time order
//        Collections.sort(resultList, new Comparator<PodeQueryResult>() {
//            public int compare(PodeQueryResult o1, PodeQueryResult o2) {
//                return Long.valueOf(o1.getDate()).compareTo(Long.valueOf(o2.getDate()));
//            }
//        });
//
//        // Transmit query results to waiting client with simulated delays
//
//        // Send first result immediately
//        PodeQueryResult prev = pop();
//        sendMessage(prev);
//
//        // Iterate and delay the message appropriately
//        PodeQueryResult cur;
//        while (!isInterrupted && !resultList.isEmpty()) {
//            cur = pop();
//
//            // Delay the message to match how it was originally recorded
//            try {
//                Thread.sleep(Long.valueOf(cur.getDate()) - Long.valueOf(prev.getDate()));
//            } catch (InterruptedException e) {
//                log.error("Unable to delay replay data correctly.");
//            }
//
//            // Delegate to our abstract method to send the message via whatever means
//            sendMessage(cur);
//
//            // Record our current message so we can appropriately delay the next message
//            prev = cur;
//        }
//        log.info("ReplayDataDistributor " + subscriptionId + " finished sending data to client.");
//
//        // Delegate to our abstract function to cleanup whatever connection might exist
//        cleanupConnection();
    }

    
    
    private void closeRDEConnection() throws IOException, InterruptedException{
        if (webSocketSession.isOpen()) {
            webSocketSession.sendMessage(new TextMessage(createUnSubscribeMessage()));
            Thread.sleep(100);
            webSocketSession.sendMessage(new TextMessage(createDisconnectMessage()));
        }
    }
    
    
    
    public abstract void sendMessage(PodeQueryResult message);

    public abstract void cleanupConnection();

    @Override
    protected void handleTextMessage(WebSocketSession session,
                                     TextMessage message) throws Exception {

        ByteBuffer buffer = ByteBuffer.wrap(message.asBytes());

        List<Message<byte[]>> messageList = decoder.decode(buffer);
        for (Message<byte[]> oneMessage : messageList) {
            StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(oneMessage, StompHeaderAccessor.class);

            if(StompCommand.MESSAGE.equals(headerAccessor.getCommand()) && headerAccessor.getContentLength() > 0 &&
                    (RDE_QUERY_RESULT_DESTINATION + subscriptionId).equals(headerAccessor.getHeader("simpDestination"))) {
                byte[] onePayLoad = oneMessage.getPayload();
                RDEQueryResult result = resultReader.readValue(onePayLoad);

                resultList.addAll(result.getFoundItems());

                // If this is the last message in the query result set, we're almost ready to start sending data
                if (result.isLastMessage()) {
                    allMessagesReceived = true;
                }
            }
        }
    }

    /**
     * Convert a bitstring data type (from the ASN.1) to a string representation of that data type. Used to match the
     * values in the PodeSubscriptionRequest to values that the RDE query understands.
     * @param input The bit string as specified by ASN.1
     * @return An all lowercase string describing the data type.
     */
    private String formatDataType(byte[] input) {
        if (input.length >= 1) {
            switch (input[0]) {
                case 0:
                    return "reserved";
                case 1:
                    return "speed";
                case 2:
                    return "occupancy";
                case 4:
                    return "volume";
                case 8:
                    return "traveltime"; // Intentionally left lowercase to match RDEDistributor
                case 16:
                    return "weather";
                case 32:
                    return "raw";
                case 64:
                    return "spatmap"; // Also left lowercase intentially
                default:
                    return "";
            }
        } else {
            return "";
        }
    }

    /**
     * Generates a JSON object from the PodeSubscriptionRequest for this replay data request. Parses the values out of
     * the PodeReplayData object inside the PodeSubscriptionRequest and formats them according to the defined interface
     * the RDE exposes.
     * @return A String representation of a JSON object RDE query
     */
    protected String formatQueryRequest() {
        PodeReplayData replayData = subRequest.getType().getReplayData();

        // Convert DFullTime to Unix timestamp
        DFullTime startTime = replayData.getStartTime();
        DFullTime endTime = replayData.getEndTIme();
        Calendar convCal = new GregorianCalendar();
        
        
        convCal.set(startTime.getYear().getValue().intValue(), startTime.getMonth().getValue().intValue()-1,
                    startTime.getDay().getValue().intValue(), startTime.getHour().getValue().intValue(),
                    startTime.getMinute().getValue().intValue());
        long startTimestamp = convCal.getTime().getTime();

        log.debug("Start Year: "+startTime.getYear().getValue().intValue()+" Value "+ startTime.getYear().getValue());
        log.debug("Start Time: "+convCal.getTime());
        
        convCal.set(endTime.getYear().getValue().intValue(), endTime.getMonth().getValue().intValue()-1,
                    endTime.getDay().getValue().intValue(), endTime.getHour().getValue().intValue(),
                    endTime.getMinute().getValue());
        long endTimestamp = convCal.getTime().getTime();

        log.debug("End Year: "+endTime.getYear().getValue().intValue()+" Value "+ endTime.getYear().getValue());
        log.debug("End Time: "+convCal.getTime());

        // Parse the data type
        byte[] dataType = subRequest.getSubData().getDataElements().getValue().getValue();

        // Get the geofence
        GeoRegion region = subRequest.getServiceRegion();
        Position3D nwCorner = region.getNwCorner();
        Position3D seCorner = region.getSeCorner();

        // Populate the JSON fields
        Writer writer = new StringWriter();
        try {
            JsonFactory factory = new JsonFactory();
            JsonGenerator generator = factory.createGenerator(writer);
            generator.writeStartObject();
            generator.writeStringField("pauseBetweenMessages", Integer.toString(PACKET_TX_DELAY));
            generator.writeStringField("type", formatDataType(dataType));
            generator.writeStringField("startTime", Long.toString(startTimestamp));
            generator.writeStringField("endTime", Long.toString(endTimestamp));
            generator.writeStringField("nwLatitude", nwCorner.getLat().getValue().toString());
            generator.writeStringField("seLatitude", seCorner.getLat().getValue().toString());
            generator.writeStringField("nwLongitude", nwCorner.getLon().getValue().toString());
            generator.writeStringField("seLongitude", seCorner.getLon().getValue().toString());
            generator.writeEndObject();
            generator.flush();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("Sending RDE Query of: "+writer.toString());
        return writer.toString();
    }

    /**
     * Creates the message to subscribe to the correct STOMP destination for this client.
     * @return The bytes of the STOMP command
     */
    private byte[] createSubscribeMessage() {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId(subscriptionId);
        headers.setDestination(RDE_QUERY_RESULT_DESTINATION);
        log.debug("RDE Subscription Headers: "+headers.toString());
        Message<byte[]> message1 = MessageBuilder
                .withPayload(ByteBuffer.allocate(0).array())
                .setHeaders(headers).build();
        return new StompEncoder().encode(message1);
    }

    /**
     * Creates the message to query the RDE for the data requested in the PodeSubscriptionRequest.
     * @return The bytes of the STOMP command
     */
    private byte[] createQueryMessage() {
        StompHeaderAccessor headers = StompHeaderAccessor
                .create(StompCommand.SEND);
        headers.setMessageTypeIfNotSet(SimpMessageType.MESSAGE);
        headers.setSubscriptionId(subscriptionId);
        headers.setDestination(RDE_QUERY_DESTINATION);
        log.debug("RDE Query Headers: "+headers.toString());
        return new StompEncoder().encode(MessageBuilder
                .withPayload(formatQueryRequest().getBytes()).setHeaders(headers)
                .build());
    }

    /**
     * Creates the message to unsubscribe from the assigned STOMP destination for this client.
     * @return The bytes of the STOMP command
     */
    private byte[] createUnSubscribeMessage() {
        StompHeaderAccessor headers = StompHeaderAccessor
                .create(StompCommand.UNSUBSCRIBE);
        headers.setSubscriptionId(subscriptionId);
        headers.setDestination(RDE_QUERY_RESULT_DESTINATION + subscriptionId);

        Message<byte[]> message1 = MessageBuilder
                .withPayload(ByteBuffer.allocate(0).array())
                .setHeaders(headers).build();
        return new StompEncoder().encode(message1);
    }

    /**
     * Creates the message to disconnect from the STOMP server entirely.
     * @return The bytes of the STOMP command
     */
    private byte[] createDisconnectMessage() {
        StompHeaderAccessor headers = StompHeaderAccessor
                .create(StompCommand.DISCONNECT);
        headers.setSubscriptionId(subscriptionId);

        Message<byte[]> message1 = MessageBuilder
                .withPayload(ByteBuffer.allocate(0).array())
                .setHeaders(headers).build();
        return new StompEncoder().encode(message1);
    }

    /**
     * Removes the frontmost value from the result list and returns the value
     * @return The first value in the result list if it has any, throws IndexOutOfBoundsExcetion otherwise
     */
    private PodeQueryResult pop() {
        return resultList.remove(0);
    }

}
