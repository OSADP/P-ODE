package com.leidos.ode.collector;

import com.leidos.ode.agent.datatarget.ODEDataTarget.DataTargetException;
import com.leidos.ode.collector.datasource.CollectorDataSource.DataSourceException;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class CollectorRunner extends QuartzJobBean {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private String collectorName;

    public static void main(String[] args) throws DataSourceException, DataTargetException {
        CollectorRunner runner = new CollectorRunner();
        if (args != null && args.length == 1) {
            runner.setCollectorName(args[0]);
            runner.runCollector();
        } else {
            runner.getLogger().error("No collector name provided");
        }
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            runCollector();
        } catch (DataSourceException ex) {
            getLogger().error(ex.getLocalizedMessage());
        } catch (DataTargetException ex) {
            getLogger().error(ex.getLocalizedMessage());
        }
    }

    public void runCollector() throws DataSourceException, DataTargetException {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("ODE-Context.xml");
        ODECollector collector = (ODECollector) ctx.getBean(getCollectorName());
        collector.startUp();
    }

    private Logger getLogger() {
        return logger;
    }

    public String getCollectorName() {
        return collectorName;
    }

    public void setCollectorName(String collectorName) {
        this.collectorName = collectorName;
    }
}
