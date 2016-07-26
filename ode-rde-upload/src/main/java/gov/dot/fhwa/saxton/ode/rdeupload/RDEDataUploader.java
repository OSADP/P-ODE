package gov.dot.fhwa.saxton.ode.rdeupload;/*
 * PodeClientMain
 * 
 * Copyright 2012-2015 IndraSoft
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dot.rdeapi.client.websocket.sockjs.ClientWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Class responsible for using the RDE Websocket interface to push data up to the RDE. Utilizes an internal thread and a
 * synchronous queue to continuously upload data. Recevies configuration properties from rdeconfig.properties, needs:
 *
 * rde.username
 * rde.password
 * rde.destination
 * rde.websocketurl
 *
 * to configure the websocket connection properly.
 */
public class RDEDataUploader {
    private static final int UPLOAD_RESTART_THRESHOLD = 100;
    private Logger log = LogManager.getLogger(getClass());
    private LinkedBlockingQueue<String> uploadQueue = new LinkedBlockingQueue<String>();
    private SockJsClient sockJsClient;
    private ClientWebSocketHandler clientWebSocketHandler;
    private DataUploader dataUploader;
    private static final long DELAY_BETWEEN_MESSAGES = 100;
    private static final long RDE_RETRY_DELAY = 5000;

    // RDE Configuration properties
    @Value("${rde.username}")
    private String username;
    @Value("${rde.password}")
    private String password;
    @Value("${rde.destination}")
    private String destination;
    @Value("${rde.websocketurl}")
    private String rdeWebsocketUri;


    public RDEDataUploader(SockJsClient sockJsClient, ClientWebSocketHandler clientWebSocketHandler) {
        this.sockJsClient = sockJsClient;
        this.clientWebSocketHandler = clientWebSocketHandler;
	}

    /**
     * Private class for actually running with the internal thread.
     * Data is stored in the parent class, so no need to be worried if we have to destroy this.
     */
    private class DataUploader implements Runnable {

        private AtomicBoolean running = new AtomicBoolean(false);
        private AtomicBoolean needsHandshake = new AtomicBoolean(true);

        public void run() {
            log.info("Beginning execution of RDE Uploader thread with " + username + ":" + password + "/" + destination);
            running.set(true);
            needsHandshake.set(true);

            ListenableFuture<WebSocketSession> future = null;
            WebSocketSession webSocketSession = null;

            // Main upload loop
            while (running.get()) {
                try {
                    if (needsHandshake.get()) {
                        // Handshake setup
                        URI apiURI = new URI(rdeWebsocketUri);

                        StringBuilder authenticationText = new StringBuilder();
                        authenticationText.append(username);
                        authenticationText.append(":");
                        authenticationText.append(password);

                        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
                        headers.add(WebSocketHttpHeaders.AUTHORIZATION, "Basic " + Base64Utils.encodeToString(authenticationText.toString().getBytes()));

                        // Do the handshake or retry it if it failed.
                        log.info("Initiating websocket handshake with RDE... " + authenticationText.toString());
                        future = sockJsClient.doHandshake(clientWebSocketHandler, headers, apiURI);
                        webSocketSession = future.get();
                        log.info("Websocket handshake complete, session " + webSocketSession.getId() + " opened.");
                        needsHandshake.set(false);
                    } else {
                        String data = uploadQueue.take();
                        log.info("RDE upload queue size " + uploadQueue.size());
                        log.info("Attempting to write " + data + " to RDE.");
                        if (webSocketSession != null) {
                            try {
                                webSocketSession.sendMessage(new TextMessage(createSendMessage(data)));
                                log.info("Date write success!");
                            } catch (IOException e) {
                                e.printStackTrace();

                                needsHandshake.set(true);
                            }
                            try {
                                Thread.sleep(DELAY_BETWEEN_MESSAGES);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("Unable to complete RDE interaction!!!", e);

                    // Attempt to nicely close the RDE connection if possible
                    try {
                        if (webSocketSession != null) {
                            webSocketSession.sendMessage(new TextMessage(createDisconnectMessage()));
                            webSocketSession.close();
                        }
                    } catch (IOException e1) {
                        log.warn("Unable to properly cleanup RDE websocket connection.");
                    }

                    // Trigger a reconnect
                    needsHandshake.set(true);
                    try {
                        Thread.sleep(RDE_RETRY_DELAY);
                    } catch (InterruptedException e1) {
                        log.warn("Unable to wait the full retry delay before attempting new RDE connection");
                    }
                } catch (ExecutionException e) {
                    log.error("Unable to complete RDE interaction!!!", e);

                    // Attempt to nicely close the RDE connection if possible
                    try {
                        if (webSocketSession != null) {
                            webSocketSession.sendMessage(new TextMessage(createDisconnectMessage()));
                            webSocketSession.close();
                        }
                    } catch (IOException e1) {
                        log.warn("Unable to properly cleanup RDE websocket connection.");
                    }

                    // Trigger a reconnect
                    needsHandshake.set(true);
                    try {
                        Thread.sleep(RDE_RETRY_DELAY);
                    } catch (InterruptedException e1) {
                        log.warn("Unable to wait the full retry delay before attempting new RDE connection");
                    }
                } catch (URISyntaxException e) {
                    log.error("Malformed RDE url: " + destination + ". RDE thread exiting.");
                }
            }

            try {
                // Cleanly close our connection if it's still open
                if (webSocketSession != null) {
                    webSocketSession.sendMessage(new TextMessage(createDisconnectMessage()));
                }
            } catch (IOException e1) {
                // Clear the websocket session and to trigger a reconnect
                log.warn("Unable to cleanly close RDE session, connection may still be open on their end.");
            }
        }

        public void setInterrupted() {
            running.set(false);
        }

    }

    /**
     * Start the data uploader thread
     */
    @PostConstruct
    public void start() {
        dataUploader = new DataUploader();
        new Thread(dataUploader).start();
    }

    /**
     * Add data to the upload queue. Will block if the upload queue is full. Will also start the uploader thread if it
     * is not running.
     *
     * @param data The data to be added to the queue, should be a JSON object.
     */
    public void enqueueUpload(String data) {
        log.info("Enqueing message: " + data);

        try {
            uploadQueue.put(data);
        } catch (InterruptedException e) {
            log.info("Interrupted while enqueing message " + data + ". Data will be lost.");
        }

        if (dataUploader == null || !dataUploader.running.get()) {
            start();
        }

        if (uploadQueue.size() > UPLOAD_RESTART_THRESHOLD) {
            log.warn("Upload queue size exceeded max of " + UPLOAD_RESTART_THRESHOLD +". Probably connection loss to RDE, restarting uploader.");
            dataUploader.setInterrupted();
            dataUploader = new DataUploader();
            new Thread(dataUploader).start();
        }

        log.info("RDE upload queue size is [" + uploadQueue.size() + "]");
    }

	private byte[] createSendMessage(String data) {
		StompHeaderAccessor headers = StompHeaderAccessor
				.create(StompCommand.SEND);
		headers.setMessageTypeIfNotSet(SimpMessageType.MESSAGE);
		headers.setDestination(destination);

		return new StompEncoder().encode(MessageBuilder
                .withPayload(data.getBytes()).setHeaders(headers)
                .build());
	}

	private byte[] createDisconnectMessage() {
		StompHeaderAccessor headers = StompHeaderAccessor
				.create(StompCommand.DISCONNECT);
		headers.setSubscriptionId("sub1");

		Message<byte[]> message1 = MessageBuilder
				.withPayload(ByteBuffer.allocate(0).array())
				.setHeaders(headers).build();
		return new StompEncoder().encode(message1);
	}

    @PreDestroy
    public void stop() {
        log.info("Stopping RDE Data Uploader.");
        dataUploader.running.set(false);
    }
}
