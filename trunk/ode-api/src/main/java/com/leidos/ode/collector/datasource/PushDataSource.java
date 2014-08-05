package com.leidos.ode.collector.datasource;

import com.leidos.ode.collector.CollectorDataSourceListener;

/**
 * Data will be sent to this collector from the provider.  
 * The collector will provide a way for the vdotdata to be sent, that could be a UDP port or some other method.
 * Once the vdotdata has been received it will send it back to the Collector listener.
 * 
 * Sources following this vdotdata should extend this class.
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
