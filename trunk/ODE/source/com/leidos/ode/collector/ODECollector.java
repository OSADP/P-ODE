package com.leidos.ode.collector;

import com.leidos.ode.agent.ODEAgent;
import com.leidos.ode.collector.datasource.CollectorDataSource;

public class ODECollector implements CollectorDataSourceListener{

	
	private CollectorDataSource dataSource;
	
	private ODEAgent agent;
	
	
	public void startUp(){
		
	}
	
	private void startCollector(){
		// start up agent
		// start data source 
	}
	
	@Override
	public void dataReceived(byte[] receivedData) {
		//When data is received by the source it will be passed to this method.
		// passes any data from datasource to agent.
		
		
	}
	
	
	
	

	public CollectorDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(CollectorDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public ODEAgent getAgent() {
		return agent;
	}

	public void setAgent(ODEAgent agent) {
		this.agent = agent;
	}


	
	
	
	
	
	
}
