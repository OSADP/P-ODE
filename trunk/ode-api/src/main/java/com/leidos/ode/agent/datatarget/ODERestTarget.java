/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.core.data.ODERegistrationResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;


/**
 *
 * @author cassadyja
 */
public class ODERestTarget implements ODEDataTarget{

    
    
    private HttpURLConnection conn;
    private WebResource webResource;
    public void configure(ODERegistrationResponse regInfo) throws DataTargetException {
        String address = "http://"+regInfo.getQueueHostURL()+":"+regInfo.getQueueHostPort()+"/"+regInfo.getQueueName();
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        webResource = client.resource(UriBuilder.fromUri(address).build());
        
        
//        try {
//            URL url = new URL(hostURL);
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setDoOutput(true);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json");
//            
//        } catch (MalformedURLException ex) {
//            throw new DataTargetException("Error connecting to host", ex);
//        } catch (ProtocolException ex) {
//            throw new DataTargetException("Error connecting to host", ex);
//        } catch (IOException ex) {
//            throw new DataTargetException("Error connecting to host", ex);
//        }
    }

    public void sendMessage(ODEAgentMessage message) throws DataTargetException {
        ClientResponse response = webResource.post(ClientResponse.class, message);
//        ClientResponse response = webResource.path("http://localhost:9090/ode-web").path("publish")
//                                .type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
//                                    .post(ClientResponse.class, message);
        String s = response.getEntity(String.class);
        System.out.println("Test "+s);
        System.out.println("Response " + response.getEntity(String.class));

        
        response.close();
//        OutputStream os = null;
//        try {
//            String input = "{\"qty\":100,\"name\":\"iPad 4\"}";
//            os = conn.getOutputStream();
//            os.write(input.getBytes());
//            os.flush();
//            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                        + conn.getResponseCode());
//            }   
//            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//        String output;
//            System.out.println("Output from Server .... \n");
//            while ((output = br.readLine()) != null) {
//                System.out.println(output);
//            }        
//        } catch (IOException ex) {
//            Logger.getLogger(ODERestTarget.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            try {
//                os.close();
//            } catch (IOException ex) {
//                Logger.getLogger(ODERestTarget.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }

    public void close() {
        
    }
    
}
