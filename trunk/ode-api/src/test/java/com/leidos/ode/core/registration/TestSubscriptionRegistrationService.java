/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.core.registration;

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

        RegistrationInformation regInfo = new RegistrationInformation();
        regInfo.setAgentId("12345");
        regInfo.setMessageType("BSM");
        regInfo.setRegion("1");
        regInfo.setRegistrationType("Subscribe");
        regInfo.setStartDate(new Date(System.currentTimeMillis()));
        regInfo.setEndDate(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)));
        regInfo.setSubscriptionReceiveAddress("cassadyja2");
        regInfo.setSubscriptionReceivePort(10001);


        String hostURL = "cassadyja2";
        int hostPort = 9090;
        String queueName = "ode-web/registerSubscribe";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://").append(hostURL).append(":").append(hostPort).append("/").append(queueName);
        String address = stringBuilder.toString();

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(UriBuilder.fromUri(address));
        Entity<RegistrationInformation> e = Entity.xml(regInfo);
        Response response = webTarget.request().post(e);
        System.out.println(response);


    }
}
