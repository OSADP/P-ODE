package com.leidos.ode.core.dao;

import java.util.List;

import javax.ejb.Stateless;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.core.data.RegistrationInformation;

@Stateless
public class RegistrationDaoImpl implements RegistrationDao {

	@Override
	public RegistrationInformation storeRegistration(RegistrationInformation regInfo) {
		SqlSessionFactory sqlMapper = DBSessionManager.getSqlSessionFactory();
		SqlSession session = sqlMapper.openSession(true);
		try{
			
			session.insert("com.leidos.ode.RegistrationMapper.insertRegistration", regInfo);
		}finally{
			session.close();
		}
		return regInfo;
	}

	@Override
	public RegistrationInformation getRegistrationForId(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RegistrationInformation> getAllRegistration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RegistrationInformation> getAllRegistrationForAgent(String agentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueueInfo getQueueForRegistration(RegistrationInformation regInfo) {
		SqlSessionFactory sqlMapper = DBSessionManager.getSqlSessionFactory();
		SqlSession session = sqlMapper.openSession(true);
		QueueInfo info = null;
		try{
			session.selectOne("com.leidos.ode.RegistrationMapper.selectQueueForRegistration", regInfo);
		}finally{
			session.close();
		}
		return info;
	}

}
