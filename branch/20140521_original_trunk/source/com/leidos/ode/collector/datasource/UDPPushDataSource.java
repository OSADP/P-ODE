package com.leidos.ode.collector.datasource;

import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class UDPPushDataSource extends PushDataSource {
	private static Logger logger = Logger.getLogger(UDPPushDataSource.class);

	private String listenerAddress;
	private int listenerPort;
	
	private DatagramSocket ss = null;
	
	@Override
	public void startDataSource() throws UnknownHostException, SocketException {
		InetAddress tmpAddress = InetAddress.getByName(listenerAddress);
		logger.info("TMP Host Address: "+tmpAddress.getHostAddress());
		byte[] addressBytes = tmpAddress.getAddress();
		InetAddress address = Inet6Address.getByAddress(listenerAddress, addressBytes);
		logger.info("Host Address: "+address.getHostAddress());
		logger.info("Host Address: "+address.getCanonicalHostName());

		
		logger.info("Connecting datagram socket on port: "+listenerPort);
		ss = new DatagramSocket(listenerPort,address);

		
		ss.setSoTimeout(10000);


	}

	@Override
	public byte[] getDataFromSource() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListenerAddress() {
		return listenerAddress;
	}

	public void setListenerAddress(String listenerAddress) {
		this.listenerAddress = listenerAddress;
	}

	public int getListenerPort() {
		return listenerPort;
	}

	public void setListenerPort(int listenerPort) {
		this.listenerPort = listenerPort;
	}

	
	
	
	
}
