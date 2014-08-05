package com.leidos.ode.collector.datasource;

import com.leidos.ode.collector.CollectorDataSourceListener;

/**
 * This collector will go out and retrieve the vdotdata from the source.
 * This collector must be configured with all necessary connection information.
 * 
 * Sources following this vdotdata should extend this class.
 * 
 * @author cassadyja
 *
 */
public abstract class PullDataSource implements CollectorDataSource{

	protected CollectorDataSourceListener dataListener;

	protected String sourceAddress;
	protected String sourcePort;
	protected String user;
	protected String pass;

	public String getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public String getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public CollectorDataSourceListener getDataListener() {
		return dataListener;
	}

	public void setDataListener(CollectorDataSourceListener dataListener) {
		this.dataListener = dataListener;
	}

}
