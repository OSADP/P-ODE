package com.leidos.ode.collector.datasource;

public interface CollectorDataSource {
	
	public void startDataSource() throws DataSourceException;
	
	public byte[] getDataFromSource() throws DataSourceException;
	
}
