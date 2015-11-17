package com.leidos.ode.collector.datasource;

import com.leidos.ode.collector.CollectorDataSourceListener;


/**
 * Data will be sent to this collector from the provider.  
 * The collector will provide a way for the data to be sent, that could be a UDP port or some other method.
 * Once the data has been received it will send it back to the Collector listener.
 * 
 * Sources following this model should extend this class.
 * @author cassadyja
 *
 */
public abstract class PushDataSource implements CollectorDataSource {

	protected CollectorDataSourceListener listener;
	

	
	public CollectorDataSourceListener getListener() {
		return listener;
	}

	public void setListener(CollectorDataSourceListener listener) {
		this.listener = listener;
	}
	
	
	

}
