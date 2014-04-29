package com.leidos.ode.core.registration;

import javax.ejb.Stateless;
import javax.ws.rs.Path;

import com.leidos.ode.core.dao.RegistrationDao;
import com.leidos.ode.core.data.ODERegistrationResponse;
import com.leidos.ode.core.data.RegistrationInformation;


@Stateless
@Path("publicationReg")
public class PublicationRegistrationService {
	
	private RegistrationDao regDao;
	
	public ODERegistrationResponse registerPublicationIntent(RegistrationInformation regInfo){
		
		return null;
	}

	
	private String getQueueNameForRegistration(RegistrationInformation regInfo){
		return null;
	}
	
	public RegistrationDao getRegDao() {
		return regDao;
	}

	public void setRegDao(RegistrationDao regDao) {
		this.regDao = regDao;
	}
	
	
	

}
