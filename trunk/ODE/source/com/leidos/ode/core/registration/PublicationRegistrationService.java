package com.leidos.ode.core.registration;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.leidos.ode.core.dao.RegistrationDao;
import com.leidos.ode.core.data.ODERegistrationResponse;
import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.core.data.RegistrationInformation;


@Stateless
@Path("publicationReg")
public class PublicationRegistrationService {
	
	private RegistrationDao regDao;
	
	@GET
	@Produces({"application/xml","application/json"})
	public ODERegistrationResponse registerPublicationIntent(RegistrationInformation regInfo){
		ODERegistrationResponse response = null;
		QueueInfo qInfo = getQueueNameForRegistration(regInfo);
		if(qInfo != null){
			regInfo = regDao.storeRegistration(regInfo);
			response = createRegResponse(regInfo, qInfo);
			
		}
		
		return response;
	}

	private ODERegistrationResponse createRegResponse(RegistrationInformation regInfo, QueueInfo qInfo){
		ODERegistrationResponse resp = new ODERegistrationResponse();
		resp.setAgentId(regInfo.getAgentId());
		resp.setMessageType(regInfo.getMessageType());
		resp.setRegion(regInfo.getRegion());
		resp.setRegistrationId(regInfo.getId());
		resp.setRegistrationType(regInfo.getRegistrationType());

		resp.setQueueConnFact(qInfo.getQueueConnectionFactory());
		resp.setQueueName(qInfo.getQueueName());
		resp.setTargetAddress(qInfo.getTargetAddress());
		resp.setTargetPort(qInfo.getTargetPort());
		return resp;
	}
	
	private QueueInfo getQueueNameForRegistration(RegistrationInformation regInfo){
		return regDao.getQueueForRegistration(regInfo);
	}
	
	public RegistrationDao getRegDao() {
		return regDao;
	}

	public void setRegDao(RegistrationDao regDao) {
		this.regDao = regDao;
	}
	
	
	

}
