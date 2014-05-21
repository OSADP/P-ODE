package com.leidos.ode.core.dao;

import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.core.registration.RegistrationInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class to handle the registration information access to the database.
 *
 * @author lamde
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
