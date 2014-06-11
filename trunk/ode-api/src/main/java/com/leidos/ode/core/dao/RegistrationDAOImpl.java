package com.leidos.ode.core.dao;

import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.core.registration.RegistrationInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import javax.ejb.Stateless;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * Class to handle the registration information access to the database.
 *
 * @author lamde
 */
@Component
public class RegistrationDAOImpl implements RegistrationDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RegistrationInformation storeRegistration(RegistrationInformation regInfo) {
        SqlSessionFactory sqlMapper = DBSessionManager.getSqlSessionFactory();
        SqlSession session = sqlMapper.openSession(true);
        try {
            session.insert("com.leidos.ode.RegistrationMapper.insertRegistration", regInfo);
        } finally {
            session.close();
        }
        return regInfo;
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
        SqlSessionFactory sqlMapper = DBSessionManager.getSqlSessionFactory();
        SqlSession session = sqlMapper.openSession(true);
        QueueInfo info = null;
        try {
            info = session.selectOne("com.leidos.ode.RegistrationMapper.selectQueueForRegistration", regInfo);
        } finally {
            session.close();
        }
        return info;
    }
}
