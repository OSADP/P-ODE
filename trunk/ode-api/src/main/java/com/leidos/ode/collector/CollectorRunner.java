package com.leidos.ode.collector;

import com.leidos.ode.agent.datatarget.ODEDataTarget.DataTargetException;
import com.leidos.ode.collector.datasource.CollectorDataSource.DataSourceException;
import com.leidos.ode.util.MongoUtils;
import com.leidos.ode.util.ODEMessageType;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.IOException;
import java.util.*;

public class CollectorRunner extends QuartzJobBean {

    private final String TAG = getClass().getSimpleName();
    private final String RUNNER_PROPERTIES = "/collector_runner.properties";
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
        setContext(new ClassPathXmlApplicationContext("META-INF/ODE-Context.xml"));
        collectors = new ArrayList<ODECollector>();
        if (loadRunnerProperties()) {
            buildCollectors();
        }
        checkMongoDB();
    }

    private void checkMongoDB() {
        if (MongoUtils.isMongoDBRunning()) {
            getLogger().debug("MongoDB has been started.");
        } else {
            getLogger().warn("****Warning, MongoDB is not running. Exceptions will be thrown when using Mongo beans!****");
        }
    }

    private boolean loadRunnerProperties() {
        try {
            getRunnerProperties().load(getClass().getResourceAsStream(RUNNER_PROPERTIES));
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
    public final void startUpCollectors() {
        try {
            for (ODECollector collector : getCollectors()) {
                collector.startUp();
            }
            getLogger().debug("Started " + getCollectors().size() + " collector(s).");
        } catch (DataSourceException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (DataTargetException ex) {
            getLogger().error(ex.getLocalizedMessage());
        }
    }

    private void buildCollectors() {
        if (getRunnerProperties() != null) {
            getLogger().debug("Building collectors...");
            String[] odeCollectorBeanNames = getContext().getBeanNamesForType(ODECollector.class);
            List<String> enabledMessageTypesNames = getEnabledMessageTypesNames();

            for (String odeCollectorBeanName : odeCollectorBeanNames) {
                for (String enabledMessageTypeName : enabledMessageTypesNames) {
                    String expectedCollectorName = enabledMessageTypeName + "Collector";
                    if (odeCollectorBeanName.contains(expectedCollectorName)) {
                        ODECollector odeCollector = (ODECollector) getContext().getBean(odeCollectorBeanName);
                        if (odeCollector != null) {
                            getLogger().debug("Found collector with name: '" + odeCollectorBeanName + "'.");
                            getCollectors().add(odeCollector);
                            getLogger().debug("Added collector for message type '" + enabledMessageTypeName + "'.");
                        } else {
                            getLogger().warn("Unable to find collector with name: '" + expectedCollectorName + "'.");
                            getLogger().warn("Unable to add collector for message type '" + enabledMessageTypeName + "'. Collector not found for this message type.");
                        }
                    }
                }
            }
        }
    }

    private List<String> getEnabledMessageTypesNames() {
        List<String> enabledMessageTypesNames = new ArrayList<String>();
        Set<Map.Entry<Object, Object>> propertyEntries = getRunnerProperties().entrySet();
        for (Map.Entry<Object, Object> propertyEntry : propertyEntries) {
            String propertyKeyString = (String) propertyEntry.getKey();
            if (propertyKeyString != null) {
                ODEMessageType odeMessageType = ODEMessageType.valueOf(propertyKeyString);
                if (odeMessageType != null) {
                    String propertyValueString = (String) propertyEntry.getValue();
                    if (isMessageTypeEnabled(odeMessageType, propertyValueString)) {
                        getLogger().debug("Message type '" + odeMessageType.name() + "' is enabled.");
                        enabledMessageTypesNames.add(odeMessageType.name());
                    } else {
                        getLogger().debug("Message type '" + odeMessageType.name() + "' is disabled.");
                    }
                }
            }
        }
        return enabledMessageTypesNames;
    }

    private boolean isMessageTypeEnabled(ODEMessageType odeMessageType, String stateString) {
        if (odeMessageType != null && stateString != null) {
            try {
                int stateInt = Integer.parseInt(stateString);
                return stateInt == 1;
            } catch (NumberFormatException e) {
                getLogger().warn("State unknown for message type '" + odeMessageType.name() + "'. Expected: '1' or '0', for enabled or disabled. Received: '" + stateString + "'.");
            }
        }
        return false;
    }

    private Properties getRunnerProperties() {
        return runnerProperties;
    }

    private ApplicationContext getContext() {
        return context;
    }

    private void setContext(ApplicationContext context) {
        this.context = context;
        getLogger().debug("Using application context: '" + context.getDisplayName() + "'.");
    }

    private List<ODECollector> getCollectors() {
        return collectors;
    }

    private Logger getLogger() {
        return logger;
    }

}
