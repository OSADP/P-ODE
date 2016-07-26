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

import java.util.Queue;

public class ResponseHandler {

	private static final Logger logger = Logger.getLogger(ResponseHandler.class
			.getName());
	
	private WarehouseConfig wsConfig;
	private Queue<byte[]> messages;
	private static final String DATA_HEADER = "308203";
	
	public ResponseHandler(WarehouseConfig wsConfig, Queue<byte[]> messages) {
		this.wsConfig = wsConfig;
		this.messages = messages;
	}
	
	public void handleMessage(String message) {
		if (wsConfig.logMessages) {
			logger.info(message);
		}

		// Add whatever additional processing you want here
		if (message.startsWith(DATA_HEADER)) {
			messages.add(message.getBytes());
		}
	}
	
}
