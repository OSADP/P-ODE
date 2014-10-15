/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.core.registration;

import com.leidos.ode.registration.request.ODERegistrationRequest;
import junit.framework.TestCase;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.sql.Date;

/**
 * @author cassadyja
 */
public class TestSubscriptionRegistrationService extends TestCase {

    public void testRegister() {

        ODERegistrationRequest registrationRequest = new ODERegistrationRequest();
        registrationRequest.setAgentId("12345");
        registrationRequest.setMessageType("BSM");
        registrationRequest.setRegion("1");
        registrationRequest.setRegistrationType("Subscribe");
        registrationRequest.setStartDate(new Date(System.currentTimeMillis()));
        registrationRequest.setEndDate(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)));
        registrationRequest.setSubscriptionReceiveAddress("cassadyja2");
        registrationRequest.setSubscriptionReceivePort(10001);


        String hostURL = "cassadyja2";
        int hostPort = 9090;
        String queueName = "ode-web/registerSubscribe";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://").append(hostURL).append(":").append(hostPort).append("/").append(queueName);
        String address = stringBuilder.toString();

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(UriBuilder.fromUri(address));
        Entity<ODERegistrationRequest> e = Entity.xml(registrationRequest);
        Response response = webTarget.request().post(e);
        System.out.println(response);

    }
}
