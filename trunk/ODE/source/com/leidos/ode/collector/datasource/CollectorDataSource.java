package com.leidos.ode.collector.datasource;

public interface CollectorDataSource {

	
	public void startDataSource();
	
	public byte[] getDataFromSource();
	
	
}
