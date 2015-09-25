package com.leidos.ode.core.distribute;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.leidos.ode.data.*;
import com.leidos.ode.util.ByteUtils;
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
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.TextMessage;
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
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dot.rdeapi.client.websocket.sockjs.ClientWebSocketHandler;
import org.springframework.util.Base64Utils;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.socket.WebSocketHttpHeaders;


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
    private static final String RDE_QUERY_RESULT_DESTINATION = "/query/result/";
    private static final String RDE_QUERY_DESTINATION = "/app/api/";
    private static final String RDE_API_BASE_URL = "wss://rde.indrasoft.net/api";

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

    // Logging
    private final String TAG = getClass().getSimpleName();
    protected final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TAG);

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
            webSocketSession.sendMessage(new TextMessage(createQueryMessage()));
            log.info("ReplayDataDistributor " + subscriptionId + " query sent to RDE.");

            
            // Wait until we've received all the messages
            while(!clientWebSocketHandler.isRecievedMessage()){
                Thread.sleep(100);
            }
            
//            while (!allMessagesReceived) {
//                Thread.sleep(100);
//            }

            // Close the websocket connection
            webSocketSession.sendMessage(new TextMessage(createUnSubscribeMessage()));
            Thread.sleep(100);
            webSocketSession.sendMessage(new TextMessage(createDisconnectMessage()));
            webSocketSession.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException ex) {
            Logger.getLogger(ReplayDataDistributor.class.getName()).log(Level.SEVERE, null, ex);
        }

        log.info("ReplayDataDistributor " + subscriptionId + " finished query. " + resultList.size() + " entries returned");

        // Connect to the client

        // Get the connection information from the service request
        ConnectionPoint target = srvRequest.getDestination();
        String targetIp = ByteUtils.buildIpAddressFromBytes(target.getAddress().getIpv4Address().getValue());
        int targetPort = target.getPort().getValue();

//        try {
//            log.info("Attempting to connect to subscriber: "+targetIp+":"+targetPort);
////            sock = new Socket(targetIp, targetPort);
//            log.info("ReplayDataDistributor " + subscriptionId + " connected to client (" + targetIp + ":" + targetPort + ").");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // Ensure the results are sorted in time order
        Collections.sort(resultList, new Comparator<PodeQueryResult>() {
            public int compare(PodeQueryResult o1, PodeQueryResult o2) {
                return Long.valueOf(o1.getDate()).compareTo(Long.valueOf(o2.getDate()));
            }
        });

        // Transmit query results to waiting client with simulated delays

        // Send first result immediately
        PodeQueryResult prev = pop();
        sendMessage(prev);

        // Iterate and delay the message appropriately
        PodeQueryResult cur;
        while (!isInterrupted && !resultList.isEmpty()) {
            cur = pop();

            // Delay the message to match how it was originally recorded
            try {
                Thread.sleep(Long.valueOf(cur.getDate()) - Long.valueOf(prev.getDate()));
            } catch (InterruptedException e) {
                log.error("Unable to delay replay data correctly.");
            }

            // Delegate to our abstract method to send the message via whatever means
            sendMessage(cur);

            // Record our current message so we can appropriately delay the next message
            prev = cur;
        }
        log.info("ReplayDataDistributor " + subscriptionId + " finished sending data to client.");

        // Delegate to our abstract function to cleanup whatever connection might exist
        cleanupConnection();
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
    private String formatQueryRequest() {
        PodeReplayData replayData = subRequest.getType().getReplayData();

        // Convert DFullTime to Unix timestamp
        DFullTime startTime = replayData.getStartTime();
        DFullTime endTime = replayData.getEndTIme();
        Calendar convCal = new GregorianCalendar();
        convCal.set(startTime.getYear().getValue().intValue(), startTime.getMonth().getValue().intValue(),
                    startTime.getDay().getValue().intValue(), startTime.getHour().getValue().intValue(),
                    startTime.getMinute().getValue().intValue());
        long startTimestamp = convCal.getTime().getTime() / 1000; // MS to S conversion

        convCal.set(endTime.getYear().getValue().intValue(), endTime.getMonth().getValue().intValue(),
                    endTime.getDay().getValue().intValue(), endTime.getHour().getValue().intValue(),
                    endTime.getMinute().getValue());
        long endTimestamp = convCal.getTime().getTime() / 1000;

        // Parse the data type
        byte[] dataType = subRequest.getSubData().getDataElements().getValue();

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

        return writer.toString();
    }

    /**
     * Creates the message to subscribe to the correct STOMP destination for this client.
     * @return The bytes of the STOMP command
     */
    private byte[] createSubscribeMessage() {
        StompHeaderAccessor headers = StompHeaderAccessor
                .create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId(subscriptionId);
        headers.setDestination(RDE_QUERY_RESULT_DESTINATION + subscriptionId);
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
        headers.setDestination(RDE_QUERY_DESTINATION + subscriptionId);

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
