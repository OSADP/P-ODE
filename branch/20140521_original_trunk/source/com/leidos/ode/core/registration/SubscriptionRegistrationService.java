package com.leidos.ode.core.registration;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.leidos.ode.core.dao.RegistrationDao;
import com.leidos.ode.core.data.ODERegistrationResponse;
import com.leidos.ode.core.data.RegistrationInformation;


@Stateless
@Path("subscriptionReg")
public class SubscriptionRegistrationService {

	private RegistrationDao regDao;
	
	
	@GET
	@Produces({"application/xml","application/json"})
	public ODERegistrationResponse registerSubscriptionIntent(RegistrationInformation regInfo){
		ODERegistrationResponse response = null;
		
		regInfo = regDao.storeRegistration(regInfo);
		response = createResponse(regInfo);
		
		
		return response;
	}
	
	private ODERegistrationResponse createResponse(RegistrationInformation regInfo){
		ODERegistrationResponse resp = new ODERegistrationResponse();
		resp.setAgentId(regInfo.getAgentId());
		resp.setMessageType(regInfo.getMessageType());
		resp.setRegion(regInfo.getRegion());
		resp.setRegistrationId(regInfo.getId());
		resp.setRegistrationType(regInfo.getRegistrationType());
		return resp;
	}
}
