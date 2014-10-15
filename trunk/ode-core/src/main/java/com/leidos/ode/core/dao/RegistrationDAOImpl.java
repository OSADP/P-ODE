package com.leidos.ode.core.dao;

import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.registration.request.ODERegistrationRequest;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class to handle the registration information access to the database.
 *
 * @author lamde
 */
@Component
public class RegistrationDAOImpl implements RegistrationDAO {

    @Override
    public ODERegistrationRequest storeRegistration(ODERegistrationRequest regInfo) {
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
    public ODERegistrationRequest getRegistrationForId(int id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ODERegistrationRequest> getAllRegistration() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ODERegistrationRequest> getAllRegistrationForAgent(String agentId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueueInfo getQueueForRegistration(ODERegistrationRequest regInfo) {
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
