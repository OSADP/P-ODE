/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.core.controllers.publish;

import com.leidos.ode.agent.data.ODEAgentMessage;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response;
import junit.framework.TestCase;

/**
 *
 * @author cassadyja
 */
public class TestPublishBSM {
    
    public static void main(String[] args){
        String hostURL = "cassadyja2";
        int hostPort = 9090;
        String queueName = "ode-web/publishBSM";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://").append(hostURL).append(":").append(hostPort).append("/").append(queueName);
        String address = stringBuilder.toString();

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(UriBuilder.fromUri(address));
        ODEAgentMessage odeAgentMessage = new ODEAgentMessage();
        Entity<ODEAgentMessage> e = Entity.xml(odeAgentMessage);
        Response response = webTarget.request().post(e);
        System.out.println(response);
    }
}
