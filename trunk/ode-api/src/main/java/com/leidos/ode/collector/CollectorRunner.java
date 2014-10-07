package com.leidos.ode.collector;

import com.leidos.ode.agent.datatarget.ODEDataTarget.DataTargetException;
import com.leidos.ode.collector.datasource.CollectorDataSource.DataSourceException;
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
    private final int DATA_SOURCE_ENABLED = 1;
    private final int DATA_SOURCE_DISABLED = 0;
    private final String VDOT_COLLECTOR_BEAN_NAME = "VDOTCollector";
    private final String RITIS_FILTER_COLLECTOR_BEAN_NAME = "RITISFilterCollector";
    private Logger logger = Logger.getLogger(TAG);
    private Properties runnerProperties;

    public static void main(String[] args) throws DataSourceException, DataTargetException {
        CollectorRunner runner = new CollectorRunner();
        runner.startUpCollectors();
    }

    @PostConstruct
    private void initialize() {
        runnerProperties = new Properties();
        try {
            runnerProperties.load(new FileInputStream(new File(RUNNER_PROPERTIES_FILENAME)));
        } catch (IOException e) {
            getLogger().error("Unable to load runner properties file.");
        }
    }

    /**
     * Returns a list of ODECollectors which are built using the message types defined in the properties
     * file for the CollectorRunner. For each valid message type defined in the properties file, if the
     * message type is enabled, a collector will be created with an appropriate data source matching the
     * message type. This list is used internally to the CollectorRunner for starting each ODECollector.
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
                    int valueInt = Integer.parseInt(valueString);
                    if (valueInt == DATA_SOURCE_ENABLED) {
                        ODECollector collector = getCollectorForMessageType(context, odeMessageType);
                        if (collector != null) {
                            collectors.add(collector);
                        }
                        getLogger().debug("Data source " + keyString + " is enabled.");
                    }
                    if (valueInt == DATA_SOURCE_DISABLED) {
                        getLogger().debug("Data source " + keyString + " is disabled.");
                    }
                } else {
                    getLogger().error("Unable to determine message type for property: " + keyString);
                }
            }
        } else {
            getLogger().error("Unable to build collectors. Runner properties was null!");
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
     * @param context Application context
     * @param odeMessageType Message type for this collector's data source
     * @return An ODE Collector who's data source is appropriate for the given message type
     */
    private ODECollector getCollectorForMessageType(ApplicationContext context, ODEMessageType odeMessageType) {
        String dataSourceBeanName = odeMessageType.toString();
        switch (odeMessageType) {
            case VDOTWeather:
                getVDOTCollector(context, dataSourceBeanName);
            case VDOTSpeed:
                getVDOTCollector(context, dataSourceBeanName);
            case VDOTTravel:
                getVDOTCollector(context, dataSourceBeanName);
            case RITISSpeed:
                getRITISCollector(context, dataSourceBeanName);
            case RITISWeather:
                getRITISCollector(context, dataSourceBeanName);
            case BSM:
                //TODO determine which collector to return here
                return null;
            default:
                //TODO determine which collector to return here
                return null;
        }
    }

    /**
     * Returns a VDOT collector for the given data source.
     *
     * @param context Application context
     * @param dataSourceBean Data source bean name for the collector
     * @return A VDOTCollector with the appropriate data source.
     */
    private ODECollector getVDOTCollector(ApplicationContext context, String dataSourceBean) {
        ODECollector vdotCollector = (ODECollector) context.getBean(VDOT_COLLECTOR_BEAN_NAME);
        if (vdotCollector != null) {
            VDOTDataSource vdotDataSource = (VDOTDataSource) context.getBean(dataSourceBean);
            if (vdotDataSource != null) {
                vdotCollector.setDataSource(vdotDataSource);
                getLogger().debug("Set data source '" + dataSourceBean + "' for VDOT collector.");
            } else {
                getLogger().error("Unable to find VDOT data source bean in application context with name: " + dataSourceBean);
            }
        } else {
            getLogger().error("Unable to find bean VDOT collector in application context with name: " + VDOT_COLLECTOR_BEAN_NAME);
        }
        return vdotCollector;
    }

    /**
     * Returns a RITIS collector for the given data source.
     *
     * @param context Application context
     * @param dataSourceBean Data source bean name for the collector
     * @return A RITISFilterCollector with the appropriate data source.
     */
    private ODECollector getRITISCollector(ApplicationContext context, String dataSourceBean) {
        ODECollector ritisCollector = (ODECollector) context.getBean(RITIS_FILTER_COLLECTOR_BEAN_NAME);
        if (ritisCollector != null) {
            VDOTDataSource vdotDataSource = (VDOTDataSource) context.getBean(dataSourceBean);
            if (vdotDataSource != null) {
                ritisCollector.setDataSource(vdotDataSource);
                getLogger().debug("Set data source '" + dataSourceBean + "' for VDOT collector.");
            } else {
                getLogger().error("Unable to find VDOT data source bean in application context with name: " + dataSourceBean);
            }
        } else {
            getLogger().error("Unable to find bean RITIS collector in application context with name: " + RITIS_FILTER_COLLECTOR_BEAN_NAME);
        }
        return ritisCollector;
    }

    private Logger getLogger() {
        return logger;
    }
}
