package com.leidos.ode.collector;

import com.leidos.ode.agent.datatarget.ODEDataTarget.DataTargetException;
import com.leidos.ode.collector.datasource.CollectorDataSource;
import com.leidos.ode.collector.datasource.CollectorDataSource.DataSourceException;
import com.leidos.ode.collector.datasource.pull.RestPullDataSource;
import com.leidos.ode.collector.datasource.pull.RestrictedRequestIntervalRestPullDataSource;
import com.leidos.ode.util.MongoUtils;
import com.leidos.ode.util.ODEMessageType;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.IOException;
import java.util.*;

public class CollectorRunner extends QuartzJobBean {

    private final String TAG = getClass().getSimpleName();
    private final String APPLICATION_CONTEXT_FILE = "META-INF/ODE-API-Context.xml";
    private final String RUNNER_PROPERTIES = "/collector_runner.properties";
    private final Class COLLECTOR_BEAN_CLASS = ODECollector.class;
    private final String COLLECTOR_BEAN_INDICATOR = "Collector";
    private Logger logger = Logger.getLogger(TAG);
    private Properties runnerProperties;
    private ApplicationContext context;
    private Map<ODECollector, List<ODECollector>> collectors;

    public CollectorRunner() {
        initialize();
    }

    public static void main(String[] args) throws DataSourceException, DataTargetException {
        CollectorRunner runner = new CollectorRunner();
        runner.startUpCollectors();
    }

    private void initialize() {
        runnerProperties = new Properties();
        try {
            setContext(new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_FILE));
            getLogger().debug("Using application context: " + APPLICATION_CONTEXT_FILE);

            if (loadRunnerProperties()) {
                //If the runner properties is successfully loaded, build the collectors
                collectors = new HashMap<ODECollector, List<ODECollector>>();
                Map<String, Map<ODEMessageType, ODECollector>> odeCollectorHashMap = buildCollectorsForEnabledMessageTypes();
                Map<ODECollector, String> collectorsWithRestrictedRequestIntervalsAndDataSourcesMap = getcollectorsWithRestrictedRequestIntervalsAndDataSourcesMap(false, odeCollectorHashMap);
                removeCollectorsRequiringRestrictedRequestIntervalCollectorsFromRunnerList(collectorsWithRestrictedRequestIntervalsAndDataSourcesMap.keySet());
                Map<ODECollector, List<ODECollector>> restrictedRequestIntervalODECollectors = buildRestrictedRequestIntervalODECollectors(collectorsWithRestrictedRequestIntervalsAndDataSourcesMap);
                addRestrictedRequestIntervalODECollectorsToRunnerList(restrictedRequestIntervalODECollectors);
            } else {
                getLogger().error("No properties file found with name: " + RUNNER_PROPERTIES);
            }

            checkMongoDB();
        } catch (BeansException e) {
            getLogger().warn("Application context file not found: " + APPLICATION_CONTEXT_FILE);
            getLogger().error(e.getLocalizedMessage());
        }
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
            for (Map.Entry<ODECollector, List<ODECollector>> collectorEntry : getCollectors().entrySet()) {
                if (collectorEntry.getKey() != null) {
                    collectorEntry.getKey().startUp();
                }
                if (collectorEntry.getValue() != null) {
                    for (ODECollector odeCollector : collectorEntry.getValue()) {
                        /*Set the data source null because this collector's data source is a part of the restricted collector
                        and starting this collector with a non-null data source would be problematic based on the ODECollector
                        logic.
                         */
                        odeCollector.setDataSource(null);
                        /*Start up this collector (which since we have nulled the data source, will hopefully only start this
                        collector's agent.
                         */
                        odeCollector.startUp();
                    }
                }
            }
            getLogger().debug("Started " + getCollectors().size() + " collector(s).");
        } catch (DataSourceException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (DataTargetException ex) {
            getLogger().error(ex.getLocalizedMessage());
        }
    }

    private Map<String, Map<ODEMessageType, ODECollector>> buildCollectorsForEnabledMessageTypes() {
        Map<String, Map<ODEMessageType, ODECollector>> odeCollectorHashMap = new HashMap<String, Map<ODEMessageType, ODECollector>>();
        if (getRunnerProperties() != null && getContext() != null) {
            String collectorRules = new StringBuilder()
                    .append(System.lineSeparator())
                    .append("------------------------- Building collectors using the following rules: -------------------------")
                    .append(System.lineSeparator())
                    .append("Rule 1: Collectors must be defined in the following context: '").append(APPLICATION_CONTEXT_FILE).append("'.")
                    .append(System.lineSeparator())
                    .append("Rule 2: Collectors are only added if the bean is of class type: '").append(COLLECTOR_BEAN_CLASS.getName()).append("'.")
                    .append(System.lineSeparator())
                    .append("Rule 3: Collectors are only added if the bean name contains the indicator: '").append(COLLECTOR_BEAN_INDICATOR).append("'.")
                    .append(System.lineSeparator())
                    .append("Rule 4: Collectors are only added if the substring of the bean name, equal to the bean name without the indicator, matches an enabled 'ODEMessageType(s)'.")
                    .append(System.lineSeparator())
                    .append("Rule 5: 'ODEMessageType(s)' should be enabled in the following properties file: '").append(RUNNER_PROPERTIES).append("'.")
                    .append(System.lineSeparator())
                    .append("Rule 6: Only message types defined in 'com.leidos.ode.util.ODEMessageType' will be recognized as valid.")
                    .append(System.lineSeparator())
                    .append("-------------------------------------------------------------------------------------------------")
                    .toString();
            getLogger().debug(collectorRules);

            String[] odeCollectorBeanNames = getContext().getBeanNamesForType(COLLECTOR_BEAN_CLASS);
            List<String> enabledMessageTypesNames = getEnabledMessageTypesNames();

            for (String odeCollectorBeanName : odeCollectorBeanNames) {
                for (String enabledMessageTypeName : enabledMessageTypesNames) {
                    String expectedCollectorName = (enabledMessageTypeName + COLLECTOR_BEAN_INDICATOR).toLowerCase();
                    if (odeCollectorBeanName.toLowerCase().contains(expectedCollectorName)) {
                        ODECollector odeCollector = (ODECollector) getContext().getBean(odeCollectorBeanName);
                        if (odeCollector != null) {
                            getLogger().debug("Found collector with name: '" + odeCollectorBeanName + "'.");
                            getCollectors().put(odeCollector, new ArrayList<ODECollector>());
                            Map<ODEMessageType, ODECollector> odeMessageTypeODECollectorMap = new HashMap<ODEMessageType, ODECollector>();
                            odeMessageTypeODECollectorMap.put(ODEMessageType.valueOf(enabledMessageTypeName), odeCollector);
                            odeCollectorHashMap.put(odeCollectorBeanName, odeMessageTypeODECollectorMap);
                            getLogger().debug("Added collector for message type '" + enabledMessageTypeName + "'.");
                        } else {
                            getLogger().warn("Unable to find collector with name: '" + expectedCollectorName + "'.");
                            getLogger().warn("Unable to add collector for message type '" + enabledMessageTypeName + "'. Collector not found for this message type.");
                        }
                    }
                }
            }
        }
        return odeCollectorHashMap;
    }

    private Map<ODECollector, String> getcollectorsWithRestrictedRequestIntervalsAndDataSourcesMap(boolean occurrencesFilter, Map<String, Map<ODEMessageType, ODECollector>> odeCollectorHashMap) {
        Map<ODECollector, String> collectorsWithRestrictedRequestIntervalsAndDataSourcesMap = new HashMap<ODECollector, String>();
        if (getRunnerProperties() != null && odeCollectorHashMap != null) {
            //Initialize the available data sources defined by ODEMessageType, and their occurrences to zero
            Map<String, Integer> dataSourcesOccurencesMap = new HashMap<String, Integer>();
            for (ODEMessageType odeMessageType : ODEMessageType.values()) {
                String dataSource = odeMessageType.dataSource();
                if (!dataSourcesOccurencesMap.keySet().contains(dataSource)) {
                    dataSourcesOccurencesMap.put(dataSource, 0);
                }
            }

            //Iterate the nested hash map that contains the 1) name of the collector bean, 2) the message type
            //represented by this collector, and 3) the ODECollector object the bean creates.
            for (Map.Entry<String, Map<ODEMessageType, ODECollector>> entry : odeCollectorHashMap.entrySet()) {

                //Determine the occurrences of each data source based on the enabled collectors
                for (Map.Entry<ODEMessageType, ODECollector> innerEntry : entry.getValue().entrySet()) {
                    //Store the data source for this collector
                    String dataSource = innerEntry.getKey().dataSource();
                    //Grab the current value for this data source's occurrences
                    int occurrences = dataSourcesOccurencesMap.get(dataSource);
                    //Increment this data source's occurrences
                    occurrences++;
                    //Store the new occurrences value
                    dataSourcesOccurencesMap.put(dataSource, occurrences);
                }
            }

            //TODO May want to remove the following print loop that is used for testing
            for (Map.Entry<String, Integer> entry : dataSourcesOccurencesMap.entrySet()) {
                getLogger().debug("Data source '" + entry.getKey() + "' has '" + entry.getValue() + "' occurrences.");
            }

            //Iterate the nested hash map that contains the 1) name of the collector bean, 2) the message type
            //represented by this collector, and 3) the ODECollector object the bean creates.
            for (Map.Entry<String, Map<ODEMessageType, ODECollector>> entry : odeCollectorHashMap.entrySet()) {
                //Based on the occurrences of each data source, determine which beans need a restricted collector
                for (Map.Entry<ODEMessageType, ODECollector> innerEntry : entry.getValue().entrySet()) {
                    //Store the ODEMessageType for this collector
                    ODEMessageType messageType = innerEntry.getKey();
                    //Store the data source for this collector
                    String dataSource = messageType.dataSource();
                    //Check if this message type is from a source with restricted request intervals
                    if (messageType.restrictedRequestInterval()) {
                        /*If the occurrence filter is enabled, a collector will only need a restricted collector if the occurrence of
                        its data source is greater than one; otherwise, all collectors that are restricted need a restricted collector.
                         */
                        if (occurrencesFilter) {
                            int occurrences = dataSourcesOccurencesMap.get(dataSource);
                            //This collector needs a restricted collector if there is more than one occurrence of this data source
                            if (occurrences > 1) {
                                collectorsWithRestrictedRequestIntervalsAndDataSourcesMap.put(innerEntry.getValue(), dataSource);
                            }
                        } else {
                            //This collector needs a restricted collector regardless of the occurrences of this data source
                            collectorsWithRestrictedRequestIntervalsAndDataSourcesMap.put(innerEntry.getValue(), dataSource);
                        }
                    }
                }
            }
        }
        return collectorsWithRestrictedRequestIntervalsAndDataSourcesMap;
    }

    private void removeCollectorsRequiringRestrictedRequestIntervalCollectorsFromRunnerList(Set<ODECollector> collectorsRequiringRestrictedIntervalCollectors) {
        if (getCollectors() != null && collectorsRequiringRestrictedIntervalCollectors != null) {
            for (ODECollector odeCollector : collectorsRequiringRestrictedIntervalCollectors) {
                getCollectors().remove(odeCollector);
            }
        }
    }

    private Map<ODECollector, List<ODECollector>> buildRestrictedRequestIntervalODECollectors(Map<ODECollector, String> collectorsRequiringRestrictedRequestIntervalCollectorsAndTheirDataSourcesMap) {
        Map<ODECollector, List<ODECollector>> restrictedRequestIntervalCollectorsAndTheirCollectorsMap = new HashMap<ODECollector, List<ODECollector>>();
        if (getContext() != null && collectorsRequiringRestrictedRequestIntervalCollectorsAndTheirDataSourcesMap != null) {

            String restrictedRequestIntervalCollectorRules = new StringBuilder()
                    .append(System.lineSeparator())
                    .append("------------------------- Building restricted interval collectors using the following rules: -------------------------")
                    .append(System.lineSeparator())
                    .append("Rule 1: Restricted interval collectors are needed for ALL data sources that are of a restricted request interval 'ODEMessageType'")
                    .append(System.lineSeparator())
                    .append("Rule 2: Restricted interval collectors should NEVER be explicitly defined in the context. They are generated based on the configured data source beans.")
                    .append(System.lineSeparator())
                    .append("Rule 3: Restricted interval collectors must only contain a 'DataSource' of type 'RestrictedRequestIntervalRestPullDataSource'.")
                    .append(System.lineSeparator())
                    .append("Rule 4: A restricted interval collectors's 'RestPullDataSource' should not be explicitly defined in the context. The list of 'RestPullDataSources' is automatically configured based on the configured data source beans.")
                    .append(System.lineSeparator())
                    .append("Rule 5: A restricted interval collectors's 'CollectorDataSourceListener' is the collector itself. This does not have to be explicitly set.")
                    .append(System.lineSeparator())
                    .append("Rule 6: A 'RestrictedRequestIntervalRestPullDataSource' request limit is set to the maximum of its data source's request limits; or '0' if none are defined.")
                    .append(System.lineSeparator())
                    .append("----------------------------------------------------------------------------------------------------------------------")
                    .toString();
            getLogger().debug(restrictedRequestIntervalCollectorRules);

            Map<String, ODECollector> restrictedRequestIntervalCollectorsMap = new HashMap<String, ODECollector>();
            //Iterate the collectors requiring restricted request interval collectors to create exactly (1) restricted request interval collector per data source
            for (Map.Entry<ODECollector, String> entry : collectorsRequiringRestrictedRequestIntervalCollectorsAndTheirDataSourcesMap.entrySet()) {
                //Create a new restricted request interval collector for each data source
                restrictedRequestIntervalCollectorsMap.put(entry.getValue(), new ODECollector());
            }

            //Iterate the data source restricted request interval collector map we created
            for (Map.Entry<String, ODECollector> restrictedRequestIntervalCollectorEntry : restrictedRequestIntervalCollectorsMap.entrySet()) {
                //Create a temporary field for holding our list of RestPullDataSource(s)
                RestrictedRequestIntervalRestPullDataSource restrictedRequestIntervalRestPullDataSource = new RestrictedRequestIntervalRestPullDataSource();
                List<RestPullDataSource> restPullDataSources = new ArrayList<RestPullDataSource>();
                int requestLimit = 0;
                List<ODECollector> temporaryCollectorList = new ArrayList<ODECollector>();
                //Iterate the collectors requiring restricted request interval collectors, in order to add their data sources to the respective restricted request interval collector's data source
                for (Map.Entry<ODECollector, String> entry : collectorsRequiringRestrictedRequestIntervalCollectorsAndTheirDataSourcesMap.entrySet()) {
                    //Determine if the data source of the collector is equal to that of the restricted request interval collector
                    if (entry.getValue().equals(restrictedRequestIntervalCollectorEntry.getKey())) {
                        //This restricted collector's data source is equal to the collector requiring a restricted collector's data source
                        //Add it to the temporary list of ODECollectors for this restricted collector
                        temporaryCollectorList.add(entry.getKey());
                        //Grab the data source of the given ODECollector
                        CollectorDataSource collectorDataSource = entry.getKey().getDataSource();
                        //Determine if the data source we just stored is of type RestPullDataSource, since this is the only type of data source that is currently restricted
                        if (collectorDataSource instanceof RestPullDataSource) {
                            RestPullDataSource restPullDataSource = (RestPullDataSource) collectorDataSource;
                            /*We must set the listener of this data source to the ODECollector it belongs to, since this
                            CollectorRunner does not use the ODECollector start up method that requires a listener
                            parameter. This allows the data to be received by the original collector, and processed
                            by its agent.
                             */
                            restPullDataSource.setCollectorDataSourceListener(entry.getKey());
                            //Add the data source to the list of RestPullDataSources
                            restPullDataSources.add(restPullDataSource);
                            int collectorDataSourceRequestLimit = restPullDataSource.getRequestLimit();
                            if (collectorDataSourceRequestLimit > requestLimit) {
                                requestLimit = collectorDataSourceRequestLimit;
                            }
                        }
                    }
                }
                //Set the list of RestPullDataSources in the RestrictedRequestIntervalRestPullDataSource
                restrictedRequestIntervalRestPullDataSource.setRestPullDataSources(restPullDataSources);
                //Set the request interval of the RestPullDataSource to the max limit of all of the sources
                restrictedRequestIntervalRestPullDataSource.setRequestLimit(String.valueOf(requestLimit));
                //Set the data source of the restricted request interval collector to the RestrictedRequestIntervalRestPullDataSource
                restrictedRequestIntervalCollectorEntry.getValue().setDataSource(restrictedRequestIntervalRestPullDataSource);
                //Put the restricted request interval collector and its associated list of ODECollectors into the map
                restrictedRequestIntervalCollectorsAndTheirCollectorsMap.put(restrictedRequestIntervalCollectorEntry.getValue(), temporaryCollectorList);
            }

            if (restrictedRequestIntervalCollectorsAndTheirCollectorsMap.size() == 0) {
                getLogger().debug("No restricted request interval collectors were necessary for the enabled message types.");
            } else {
                getLogger().debug("Created '" + restrictedRequestIntervalCollectorsAndTheirCollectorsMap.size() + "' restricted request interval collector(s).");
            }
        }
        return restrictedRequestIntervalCollectorsAndTheirCollectorsMap;
    }

    private void addRestrictedRequestIntervalODECollectorsToRunnerList(Map<ODECollector, List<ODECollector>> restrictedRequestIntervalODECollectors) {
        if (getCollectors() != null && restrictedRequestIntervalODECollectors != null) {
            getCollectors().putAll(restrictedRequestIntervalODECollectors);
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
                } else {
                    getLogger().warn("Invalid message type defined in properties. Message type: '" + propertyKeyString + "' is not a valid ODEMessageType.");
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
    }

    private Map<ODECollector, List<ODECollector>> getCollectors() {
        return collectors;
    }

    private Logger getLogger() {
        return logger;
    }

}
