/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.core.registration;

import com.leidos.ode.core.dao.RegistrationDAO;
import com.leidos.ode.core.dao.RegistrationDAOImpl;
import com.leidos.ode.core.data.ODERegistrationResponse;
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

        RegistrationInformation regInfo = new RegistrationInformation();
        regInfo.setAgentId("");
        regInfo.setMessageType("BSM");
        regInfo.setRegion("1");
        regInfo.setRegistrationType("Publish");
        regInfo.setStartDate(new Date(System.currentTimeMillis()));
        regInfo.setEndDate(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)));

        ODERegistrationResponse resp = pubReg.registerPublicationIntent(regInfo);
        assertNotNull(resp);
        assertEquals("ODEQueueConnFact", resp.getQueueConnFact());
        assertEquals("localhost", resp.getQueueHostURL());
        assertEquals("BSMR1Queue", resp.getQueueName());
        assertEquals(7676, resp.getQueueHostPort());
    }
}
