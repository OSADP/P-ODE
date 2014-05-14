package com.leidos.ode.core.dao;

import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.core.registration.RegistrationInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 5/7/14
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class RegistrationDAOImpl implements RegistrationDAO{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RegistrationInformation storeRegistration(RegistrationInformation regInfo) {
        return null;
    }

    @Override
    public RegistrationInformation getRegistrationForId(int id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<RegistrationInformation> getAllRegistration() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<RegistrationInformation> getAllRegistrationForAgent(String agentId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueueInfo getQueueForRegistration(RegistrationInformation regInfo) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
