package com.leidos.ode.core.registration;

import com.leidos.ode.core.dao.RegistrationDAO;
import com.leidos.ode.core.data.ODERegistrationResponse;


//@Stateless
//@Path("publicationReg")
public class PublicationRegistrationService {
	
	private RegistrationDAO regDao;
	
	public ODERegistrationResponse registerPublicationIntent(RegistrationInformation regInfo){
		
		return null;
	}

	private String getQueueNameForRegistration(RegistrationInformation regInfo){
		return null;
	}
	
	public RegistrationDAO getRegDao() {
		return regDao;
	}

	public void setRegDao(RegistrationDAO regDao) {
		this.regDao = regDao;
	}
}
