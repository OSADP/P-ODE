package com.leidos.ode.collector;

import com.leidos.ode.agent.datatarget.ODEDataTarget.DataTargetException;
import com.leidos.ode.collector.datasource.CollectorDataSource.DataSourceException;
import com.leidos.ode.collector.datasource.DataSource;
import com.leidos.ode.util.ODEMessageType;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CollectorRunner extends QuartzJobBean {

    private final String TAG = getClass().getSimpleName();
    private final String RUNNER_PROPERTIES_FILENAME = "collector_runner.properties";
    private final String VDOT_COLLECTOR_BEAN_NAME = "VDOTCollector";
    private final String RITIS_FILTER_COLLECTOR_BEAN_NAME = "RITISFilterCollector";
    private Logger logger = Logger.getLogger(TAG);
    private Properties runnerProperties;

    {
        getLogger().error("Unable to build collectors. Runner properties was null!");
    }

    public static void main(String[] args) throws DataSourceException, DataTargetException {
        CollectorRunner runner = new CollectorRunner();
        runner.startUpCollectors();
    }

    @PostConstruct
    private void initialize() {
        runnerProperties = new Properties();
        try {
            runnerProperties.load(new FileInputStream(new File(RUNNER_PROPERTIES_FILENAME)));
            getLogger().debug("Loaded collector runner properties file.");
        } catch (IOException e) {
            getLogger().error("Unable to load collector runner properties file.");
        }
    }

    /**
     * Returns a list of ODECollectors which are built using the message types defined in the properties
     * file for the CollectorRunner. For each valid message type defined in the properties file, if the
     * message type is enabled, a collector will be created with an appropriate data source matching the
     * message type. This list is used internally to the CollectorRunner for starting each ODECollector.
     *
     * @return
     */
    private List<ODECollector> buildCollectors() {
        List<ODECollector> collectors = new ArrayList<ODECollector>();
        if (runnerProperties != null) {
            ApplicationContext context = new ClassPathXmlApplicationContext("ODE-Context.xml");
            for (Map.Entry<Object, Object> propertyMap : runnerProperties.entrySet()) {
                String keyString = (String) propertyMap.getKey();
                String valueString = (String) propertyMap.getValue();
                ODEMessageType odeMessageType = ODEMessageType.valueOf(keyString);
                if (odeMessageType != null) {
                    try {
                        int valueInt = Integer.parseInt(valueString);
                        if (valueInt == 1) {
                            ODECollector collector = getCollectorForMessageType(context, odeMessageType);
                            if (collector != null) {
                                collectors.add(collector);
                            }
                            getLogger().debug("Data source " + keyString + " is enabled.");
                        } else if (valueInt == 0) {
                            getLogger().debug("Data source " + keyString + " is disabled.");
                        } else {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        getLogger().warn("Invalid data source definition. Expected '1' for enabled, or '0' for disabled, but instead found " + valueString + ". Please correct this issue in the collector runner properties and try again.");
                    }
                } else {
                    getLogger().error("Unable to determine message type for property: " + keyString);
                }
            }
        }
        return collectors;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        startUpCollectors();
    }

    /**
     * Starts up the collectors for on the enabled message types in the properties file.
     */
    public void startUpCollectors() {
        try {
            List<ODECollector> collectors = buildCollectors();
            for (ODECollector collector : collectors) {
                collector.startUp();
            }
            getLogger().debug("Started " + collectors.size() + " collectors.");
        } catch (DataSourceException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (DataTargetException ex) {
            getLogger().error(ex.getLocalizedMessage());
        }
    }

    /**
     * Uses ODEMessageType to determine the appropriate collector for the given data source in the application context.
     *
     * @param context        Application context
     * @param odeMessageType Message type for this collector's data source
     * @return An ODE Collector who's data source is appropriate for the given message type
     */
    private ODECollector getCollectorForMessageType(ApplicationContext context, ODEMessageType odeMessageType) {
        String dataSourceBeanName = odeMessageType.toString();
        switch (odeMessageType) {
            case VDOTWeather:
                getCollector(context, VDOT_COLLECTOR_BEAN_NAME, dataSourceBeanName);
            case VDOTSpeed:
                getCollector(context, VDOT_COLLECTOR_BEAN_NAME, dataSourceBeanName);
            case VDOTTravel:
                getCollector(context, VDOT_COLLECTOR_BEAN_NAME, dataSourceBeanName);
            case RITISSpeed:
                getCollector(context, RITIS_FILTER_COLLECTOR_BEAN_NAME, dataSourceBeanName);
            case RITISWeather:
                getCollector(context, RITIS_FILTER_COLLECTOR_BEAN_NAME, dataSourceBeanName);
            case BSM:
                //TODO determine which collector to return here
                return null;
            default:
                //TODO determine which collector to return here
                return null;
        }
    }

    /**
     * Returns a collector whose data source is the bean found with the provided data source bean name.
     *
     * @param context            Application context
     * @param dataSourceBeanName Data source bean name for the collector
     * @return An ODECollector with the appropriate data source.
     */
    private ODECollector getCollector(ApplicationContext context, String collectorBeanName, String dataSourceBeanName) {
        ODECollector collector = (ODECollector) context.getBean(collectorBeanName);
        if (collector != null) {
            DataSource dataSource = (DataSource) context.getBean(dataSourceBeanName);
            if (dataSource != null) {
                collector.setDataSource(dataSource);
                getLogger().debug("Set data source '" + dataSourceBeanName + "' for collector.");
            } else {
                getLogger().error("Unable to find data source bean in application context with name: " + dataSourceBeanName);
            }
        } else {
            getLogger().error("Unable to find collector bean in application context with name: " + collectorBeanName);
        }
        return collector;
    }

    private Logger getLogger() {
        return logger;
    }
}
