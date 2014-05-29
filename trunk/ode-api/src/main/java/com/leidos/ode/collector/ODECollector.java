package com.leidos.ode.collector;



import com.leidos.ode.agent.ODEAgent;
import com.leidos.ode.collector.datasource.CollectorDataSource;
import com.leidos.ode.collector.datasource.DataSourceException;
import javax.jms.JMSException;
import javax.naming.NamingException;

public class ODECollector implements CollectorDataSourceListener{

	
	private CollectorDataSource dataSource;
	
	private ODEAgent agent;
	
	
	public void startUp() throws DataSourceException, JMSException,NamingException {
		startCollector();
	}
	
	private void startCollector() throws DataSourceException, JMSException,NamingException {
		agent.startUp();
		dataSource.startDataSource();
	}
	
	
	
	@Override
	public void dataReceived(byte[] receivedData) {
		agent.processMessage(receivedData);
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