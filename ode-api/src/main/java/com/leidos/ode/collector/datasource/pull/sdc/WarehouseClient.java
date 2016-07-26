/**
 * Copyright 2014 Leidos
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.leidos.ode.collector.datasource.pull.sdc;

import org.apache.log4j.Logger;
import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class WarehouseClient extends org.java_websocket.client.WebSocketClient {

	private static final Logger logger = Logger.getLogger(WarehouseClient.class
			.getName());

	private ResponseHandler handler;
	private Queue<byte[]> messages = new LinkedBlockingQueue<byte[]>();
	private SDCQuery query;

	@SuppressWarnings("unchecked")
	public static WarehouseClient configure(WarehouseConfig wsConfig, ResponseHandler handler, String jSessionID, SDCQuery query)
			throws URISyntaxException, KeyManagementException,
			KeyStoreException, NoSuchAlgorithmException, CertificateException,
			IOException {
		
		@SuppressWarnings("rawtypes")
		Map headers = new HashMap();
		headers.put(
				"Cookie",
				WarehouseConfig.JSESSIONID_KEY + "="
						+ jSessionID);

		WarehouseClient wsClient = new WarehouseClient(new URI(wsConfig.warehouseURL),
				new Draft_17(), headers); // more about drafts here:
		// http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
		wsClient.handler = handler;

		if (wsConfig.warehouseURL.startsWith("wss")) {
			SSLContext wsSSLContext;
			if(!wsConfig.keystoreFile.equals("") && !wsConfig.keystorePassword.equals("")) {
				wsSSLContext = SSLBuilder.buildSSLContext(wsConfig.keystoreFile, wsConfig.keystorePassword);
			}
			else {
				wsSSLContext = SSLBuilder.buildSSLContext();
			}
			
			wsClient.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(
					wsSSLContext));
		}

		wsClient.query = query;

		return wsClient;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private WarehouseClient(URI serverUri, Draft draft, Map headers) {
		super(serverUri, draft, headers, 0);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		logger.info("SDC Websocket connection opened");
		logger.info("Sending SDC query: " + query);
		send(query.toString());
	}

	@Override
	public void onMessage(String message) {
		handler.handleMessage(message);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// The codecodes are documented in class
		// org.java_websocket.framing.CloseFrame
		logger.info("Connection closed by " + (remote ? "remote peer" : "us"));
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
		logger.error(ex);
		// if the error is fatal then onClose will be called additionally
	}
}
