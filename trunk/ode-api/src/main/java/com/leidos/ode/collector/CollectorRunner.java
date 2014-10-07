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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CollectorRunner extends QuartzJobBean {

    private final String TAG = getClass().getSimpleName();
    private final String RUNNER_PROPERTIES_FILENAME = "/collector_runner.properties";
    private Logger logger = Logger.getLogger(TAG);
    private Properties runnerProperties;
    private ApplicationContext context;
    private List<ODECollector> collectors;

    public CollectorRunner() {
        initialize();
    }

    public static void main(String[] args) throws DataSourceException, DataTargetException {
        CollectorRunner runner = new CollectorRunner();
        runner.startUpCollectors();
    }

    private void initialize() {
        runnerProperties = new Properties();
        context = new ClassPathXmlApplicationContext("META-INF/ODE-Context.xml");
        collectors = new ArrayList<ODECollector>();
        if (loadRunnerProperties()) {
            buildCollectors();
        }
    }

    private boolean loadRunnerProperties() {
        try {
            getRunnerProperties().load(getClass().getResourceAsStream(RUNNER_PROPERTIES_FILENAME));
            getLogger().debug("Loaded collector runner properties file.");
            return true;
        } catch (IOException e) {
            getLogger().error("Unable to load collector runner properties file.");
        }
        return false;
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
            for (ODECollector collector : getCollectors()) {
                collector.startUp();
            }
            getLogger().debug("Started " + getCollectors().size() + " collectors.");
        } catch (DataSourceException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (DataTargetException ex) {
            getLogger().error(ex.getLocalizedMessage());
        }
    }

    private void buildCollectors() {
        if (getRunnerProperties() != null) {
            for (Map.Entry<Object, Object> propertyEntry : getRunnerProperties().entrySet()) {
                String propertyKeyString = (String) propertyEntry.getKey();
                if (propertyKeyString != null) {
                    ODEMessageType odeMessageType = ODEMessageType.valueOf(propertyKeyString);
                    if (odeMessageType != null) {
                        String propertyValueString = (String) propertyEntry.getValue();
                        if (isEnabledMessageType(odeMessageType, propertyValueString)) {
                            ODECollector odeCollector = getODECollector(odeMessageType);
                            if (odeCollector != null) {
                                getCollectors().add(odeCollector);
                                getLogger().debug("Added collector for message type '" + odeMessageType.name() + "'.");
                            } else {
                                getLogger().warn("Unable to add collector for message type '" + odeMessageType.name() + "'. Collector not found for this message type.");
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isEnabledMessageType(ODEMessageType odeMessageType, String propertyValueString) {
        if (odeMessageType != null && propertyValueString != null) {
            try {
                int propertyValueInt = Integer.parseInt(propertyValueString);
                if (propertyValueInt == 1) {
                    getLogger().debug("Message type " + odeMessageType.name() + " is enabled.");
                    return true;
                } else if (propertyValueInt == 0) {
                    getLogger().debug("Message type " + odeMessageType.name() + " is disabled.");

                } else {
                    getLogger().warn("Message type " + odeMessageType.name() + " state unknown. Expected: '1' or '0', for enabled or disabled. Received: '" + propertyValueString + "'.");
                }
            } catch (NumberFormatException e) {
                getLogger().error(e.getLocalizedMessage());
            }
        }
        return false;
    }

    private ODECollector getODECollector(ODEMessageType odeMessageType) {
        if (odeMessageType != null) {
            String collectorName = odeMessageType.name() + "Collector";
            ODECollector odeCollector = (ODECollector) getContext().getBean(collectorName);
            if (odeCollector != null) {
                getLogger().debug("Found collector with name: '" + collectorName + "'.");
                return odeCollector;
            } else {
                getLogger().warn("Unable to find collector with name: '" + collectorName + "'.");
            }
        }
        return null;
    }

    private Properties getRunnerProperties() {
        return runnerProperties;
    }

    private ApplicationContext getContext() {
        return context;
    }

    private List<ODECollector> getCollectors() {
        return collectors;
    }

    private Logger getLogger() {
        return logger;
    }

}
