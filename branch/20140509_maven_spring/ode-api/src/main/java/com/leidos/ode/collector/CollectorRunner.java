package com.leidos.ode.collector;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class CollectorRunner {

	
	public String collectorName;
	
	public static void main(String[] args) throws UnknownHostException, SocketException{
		if(args != null && args.length == 1){
			CollectorRunner runner = new CollectorRunner();
			runner.runCollector(args[0]);
		}else{
			System.out.println("No collector name provided");
			
		}
	}
	
	public void runCollector(String collectorName) throws UnknownHostException, SocketException{
		this.collectorName = collectorName;
		ApplicationContext ctx = new ClassPathXmlApplicationContext("ODE-Context.xml");
		ODECollector collector = (ODECollector)ctx.getBean(collectorName);
		collector.startUp();
	}

	public String getCollectorName() {
		return collectorName;
	}

	public void setCollectorName(String collectorName) {
		this.collectorName = collectorName;
	}
	
	
}
