/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.core.controllers.publish;

import com.leidos.ode.agent.data.ODEAgentMessage;

/**
 * @author cassadyja
 */
public class TestPublishBSM {

    public static void main(String[] args) {
        String hostURL = "cassadyja2";
        int hostPort = 9090;
        String queueName = "ode-web/publishBSM";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://").append(hostURL).append(":").append(hostPort).append("/").append(queueName);
        String address = stringBuilder.toString();
        ODEAgentMessage odeAgentMessage = new ODEAgentMessage();

//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//        HttpPost httppost = new HttpPost(address);
//        
//        httppost.setEntity(null);
//        CloseableHttpResponse closeableHttpResponse = httpClient.execute(httppost);
//        HttpEntity responseEntity = closeableHttpResponse.getEntity();
//        byte[] responseBytes = EntityUtils.toByteArray(responseEntity);
//        EntityUtils.consume(responseEntity);


//        Entity<ODEAgentMessage> e = Entity.xml(odeAgentMessage);
//        System.out.println(response);
    }
}
