package com.leidos.ode.collector;

import com.leidos.ode.agent.datatarget.DataTargetException;
import com.leidos.ode.collector.datasource.DataSourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.naming.NamingException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;


public class CollectorRunner extends QuartzJobBean{

	
    public String collectorName;
	
    public static void main(String[] args) throws DataSourceException, DataTargetException {
        if(args != null && args.length == 1){
                CollectorRunner runner = new CollectorRunner();
                runner.setCollectorName(args[0]);
                runner.runCollector();
        }else{
                System.out.println("No collector name provided");

        }
    }
    
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            runCollector();
        } catch (DataSourceException ex) {
            Logger.getLogger(CollectorRunner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DataTargetException ex) {
            Logger.getLogger(CollectorRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }        
	
    public void runCollector() throws DataSourceException, DataTargetException {
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
