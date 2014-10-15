/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.core.registration;

import com.leidos.ode.core.dao.RegistrationDAO;
import com.leidos.ode.core.dao.RegistrationDAOImpl;
import com.leidos.ode.registration.request.ODERegistrationRequest;
import com.leidos.ode.registration.response.ODERegistrationResponse;
import junit.framework.TestCase;

import java.sql.Date;

/**
 * @author cassadyja
 */
public class TestPublicationRegistrationService extends TestCase {

    public void testRegister() {
        PublicationRegistrationService pubReg = new PublicationRegistrationService();
        RegistrationDAO dao = new RegistrationDAOImpl();
        pubReg.setRegDao(dao);

        ODERegistrationRequest registrationRequest = new ODERegistrationRequest();
        registrationRequest.setAgentId("");
        registrationRequest.setMessageType("BSM");
        registrationRequest.setRegion("1");
        registrationRequest.setRegistrationType("Publish");
        registrationRequest.setStartDate(new Date(System.currentTimeMillis()));
        registrationRequest.setEndDate(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)));

        ODERegistrationResponse registrationResponse = pubReg.registerPublicationIntent(registrationRequest);
        assertNotNull(registrationResponse);
        assertEquals("ODEQueueConnFact", registrationResponse.getQueueConnFact());
        assertEquals("localhost", registrationResponse.getQueueHostURL());
        assertEquals("BSMR1Queue", registrationResponse.getQueueName());
        assertEquals(7676, registrationResponse.getQueueHostPort());
    }
}
