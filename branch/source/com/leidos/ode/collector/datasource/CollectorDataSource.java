package com.leidos.ode.collector.datasource;

import java.net.SocketException;
import java.net.UnknownHostException;

public interface CollectorDataSource {

	
	public void startDataSource()throws UnknownHostException, SocketException;
	
	public byte[] getDataFromSource();
	
	
}
