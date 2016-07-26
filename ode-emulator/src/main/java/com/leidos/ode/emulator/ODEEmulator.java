package com.leidos.ode.emulator;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.datatarget.ODEDataTarget;
import com.leidos.ode.collector.ODECollector;
import com.leidos.ode.collector.datasource.CollectorDataSource;
import com.leidos.ode.collector.datasource.DataSource;
import com.leidos.ode.data.*;
import com.leidos.ode.emulator.agent.EmulatorDataTarget;

import com.leidos.ode.util.ODEMessageType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.xml.bind.DatatypeConverter;
import org.springframework.http.MediaType;


/**
 * @author cassadyja
 */
@Controller
public class ODEEmulator implements EmulatorDataListener, DisposableBean {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);

    private static final double LAT_LONG_SCALING_FACTOR = 10000000.0;

    @Autowired
    @Qualifier("speedCollector")
    private ODECollector speedCollector;
    @Autowired
    @Qualifier("volumeCollector")
    private ODECollector volumeCollector;
    @Autowired
    @Qualifier("occupancyCollector")
    private ODECollector occupancyCollector;
    @Autowired
    @Qualifier("travelCollector")
    private ODECollector travelTimeCollector;
    @Autowired
    @Qualifier("weatherCollector")
    private ODECollector weatherCollector;
    @Autowired
    @Qualifier("incidentCollector")
    private ODECollector incidentCollector;
    
    
    private CurrentDataSet currentData = new CurrentDataSet();
    private static final String EAST_INDICATOR = "east";
    private static final String WEST_INDICATOR = "west";


    @RequestMapping(value = "startEmulator", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    String startEmulator(@RequestBody EmulatorCollectorList collectors) {
        currentData = new CurrentDataSet();
        logger.debug("Received List Start date of: "+collectors.getStartDate());
        logger.debug("Received List end date of: "+collectors.getEndDate());
        String startDate = speedCollector.getAgent().getRegistrationRequest().getReplayStartDate();
        String endDate = speedCollector.getAgent().getRegistrationRequest().getReplayEndDate();
        
        if(collectors.getStartDate() != null && collectors.getEndDate() != null){
            try {
                Date d = sdf.parse(collectors.getStartDate());
                d = sdf.parse(collectors.getEndDate());
                startDate = collectors.getStartDate();
                endDate = collectors.getEndDate();
            } catch (ParseException ex) {
                logger.debug("Provided dates not parseable, using defaults");
            }
        }
        destroy();
        
        for (String s : collectors.getCollectors()) {
            try {
                ODECollector theCollector = null;
                
                if (s.equalsIgnoreCase("SpeedCollector")) {
                    theCollector = speedCollector;
//                    ((EmulatorDataTarget) speedCollector.getAgent().getDataTarget()).setListener(this);
//                    setCollectorDataSourceListenerForCollectorsDataSource(speedCollector);
//                    speedCollector.startUp();
                } else if (s.equalsIgnoreCase("VolumeCollector")) {
                    theCollector = getVolumeCollector();
//                    ((EmulatorDataTarget) getVolumeCollector().getAgent().getDataTarget()).setListener(this);
//                    setCollectorDataSourceListenerForCollectorsDataSource(getVolumeCollector());
//                    getVolumeCollector().startUp();
                } else if (s.equalsIgnoreCase("OccupancyCollector")) {
                    theCollector = getOccupancyCollector();
//                    ((EmulatorDataTarget) getOccupancyCollector().getAgent().getDataTarget()).setListener(this);
//                    setCollectorDataSourceListenerForCollectorsDataSource(getOccupancyCollector());
//                    getOccupancyCollector().startUp();
                } else if (s.equalsIgnoreCase("TravelTimeCollector")) {
                    theCollector = getTravelTimeCollector();
//                    ((EmulatorDataTarget) getTravelTimeCollector().getAgent().getDataTarget()).setListener(this);
//                    setCollectorDataSourceListenerForCollectorsDataSource(getTravelTimeCollector());
//                    getTravelTimeCollector().startUp();
                } else if (s.equalsIgnoreCase("WeatherCollector")) {
                    theCollector = getWeatherCollector();
//                    ((EmulatorDataTarget) getWeatherCollector().getAgent().getDataTarget()).setListener(this);
//                    setCollectorDataSourceListenerForCollectorsDataSource(getWeatherCollector());
//                    getWeatherCollector().startUp();
                } else if (s.equalsIgnoreCase("IncidentCollector")) {
                    theCollector = getIncidentCollector();
                }
                
                
                if(theCollector != null){
                    ((EmulatorDataTarget) theCollector.getAgent().getDataTarget()).setListener(this);
                    setCollectorDataSourceListenerForCollectorsDataSource(theCollector);

                    if(theCollector.getAgent().getRegistrationRequest().getSubscriptionType().equalsIgnoreCase("Replay")){
                        logger.debug("StartDate: "+startDate+" End Date: "+endDate);
                        theCollector.getAgent().getRegistrationRequest().setReplayEndDate(endDate);
                        theCollector.getAgent().getRegistrationRequest().setReplayStartDate(startDate);
                    }

                    theCollector.startUp();
                }
                
            } catch (ODEDataTarget.DataTargetException ex) {
                logger.error(ex.getLocalizedMessage());
            }
        }
        return "<response>started</response>";
    }

    private ODECollector getIncidentCollector() {
        return incidentCollector;
    }

    private void setCollectorDataSourceListenerForCollectorsDataSource(ODECollector odeCollector) {
        if (odeCollector != null) {
            CollectorDataSource collectorDataSource = odeCollector.getDataSource();
            if (collectorDataSource != null && collectorDataSource instanceof DataSource) {
                DataSource dataSource = (DataSource) collectorDataSource;
                dataSource.setCollectorDataSourceListener(odeCollector);
            }
        }
    }

    @RequestMapping(value = "getCurrentData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CurrentDataSet getCurrentData() {
        return currentData;
    }

    @Override
    public void destroy() {
        speedCollector.stop();
        getVolumeCollector().stop();
        getOccupancyCollector().stop();
        getTravelTimeCollector().stop();
        getWeatherCollector().stop();
        getIncidentCollector().stop();
    }

    
    public void dataReceived(String messageType, ODEAgentMessage data) {
        logger.debug("Recevied Data, Message Type: ["+messageType+"]");
        if (data != null) {
            if (ODEMessageType.SPEED.equals(ODEMessageType.valueOf(messageType))) {
                speedDataReceived(data);
            } else if (ODEMessageType.VOLUME.equals(ODEMessageType.valueOf(messageType))) {
                volumeDataReceived(data);
            } else if (ODEMessageType.OCCUPANCY.equals(ODEMessageType.valueOf(messageType))) {
                occupancyDataReceived(data);
            } else if (ODEMessageType.TRAVEL.equals(ODEMessageType.valueOf(messageType))) {
                travelTimeDataReceived(data);
            } else if (ODEMessageType.WEATHER.equals(ODEMessageType.valueOf(messageType))) {
                weatherDataReceived(data);
            } else if (ODEMessageType.INCIDENT.equals(ODEMessageType.valueOf(messageType))) {
                incidentDataReceived(data);
            }
        }
    }

    private void incidentDataReceived(ODEAgentMessage data) {
        PodeDataDelivery dataDelivery = (PodeDataDelivery) data.getFormattedMessage();
        String source = getSourceName(dataDelivery.getPodeData().getSource().getValue());

        PodeIncidentData incident = dataDelivery.getPodeData().getPodeData().getIncident();
        IncidentElement elem = new IncidentElement(incident.getEvent().getIncidentType().getValue().toString(),
                incident.getDescription(),
                "" + (incident.getLocation().getGeoLocation().getLat().getValue() / LAT_LONG_SCALING_FACTOR),
                "" + (incident.getLocation().getGeoLocation().getLon().getValue() / LAT_LONG_SCALING_FACTOR),
                convertDirection(incident.getLocation().getDirection()));

        IncidentDisplayDataElement element = new IncidentDisplayDataElement(source, "Incident", elem);

        logger.debug("EMULATOR: Incident value: " + elem + " from " + source);
        logger.debug("EMULATOR Setting incident value");

        currentData.addIncidentValue(element);
    }

    private String convertDirection(PodeLaneDirection direction) {
        if (direction != null) {
            switch (direction.getValue()) {
                // North and south cases are arbitrarily decided, review these choices later.
                case south:
                    return WEST_INDICATOR;

                case north:
                    return EAST_INDICATOR;

                case southEast:
                case northEast:
                case east:
                    return EAST_INDICATOR;

                case southWest:
                case northWest:
                case west:
                    return WEST_INDICATOR;

                default:
                    return EAST_INDICATOR;
            }
        } else {
            return "east";
        }
    }


    private void speedDataReceived(ODEAgentMessage data) {
        PodeDataDelivery dataDelivery = (PodeDataDelivery) data.getFormattedMessage();
        String source = getSourceName(dataDelivery.getPodeData().getSource().getValue());
        DataDisplayElement element = new DataDisplayElement();
        if(source.equalsIgnoreCase("dms")){
            source = source+dataDelivery.getPodeData().getRoutename();
        }
        element.setSource(source);

        // Detect if it's BSM or detector data
        element.setDataType((dataDelivery.getPodeData().getPodeData().getDetector().getDetectMethod().getValue() == PodeDetectionMethod.EnumType.vehicleProbe ? "BSM" : "Speed"));

        // TODO: FIGURE OUT HOW TO UNPACK THIS WEIRD DATA FORMAT
        // This isn't the greatest workaround I've ever seen, but it's here
        int val = ((PodeLaneData) dataDelivery.getPodeData().getPodeData().getDetector().getLaneData().toArray()[0]).getData().getSpeed().getValue();
        double meterPerSecond = val *.02;
        double mph = meterPerSecond /.44704;
        
        
        element.setDataValue(mph+"");
        
        PodeLaneDirection.EnumType dir = ((PodeLaneData) dataDelivery.getPodeData().getPodeData().getDetector().getLaneData().toArray()[0]).getLane().getLaneDirection().getValue();
        String dirString = dir.name();
        
        logger.debug("EMULATOR: Setting speed value");
        currentData.addSpeedValue(element);
//        if(dirString.equalsIgnoreCase("east")){
//            logger.debug("EMULATOR: Setting eastbound speed value");
//            currentData.addSpeedEastValue(element);
//        }else{
//            logger.debug("EMULATOR: Setting westbound speed value");
//            currentData.addSpeedWestValue(element);
//        }
    }

    private void volumeDataReceived(ODEAgentMessage data) {
        PodeDataDelivery dataDelivery = (PodeDataDelivery) data.getFormattedMessage();
        String source = getSourceName(dataDelivery.getPodeData().getSource().getValue());
        DataDisplayElement element = new DataDisplayElement();
        element.setSource(source);
        element.setDataType("Volume");
        element.setDataValue(((PodeLaneData) dataDelivery.getPodeData().getPodeData().getDetector().getLaneData().toArray()[0]).getData().getVolume()+"");
        currentData.addVolumeValue(element);
    }

    private void occupancyDataReceived(ODEAgentMessage data) {
        PodeDataDelivery dataDelivery = (PodeDataDelivery) data.getFormattedMessage();
        String source = getSourceName(dataDelivery.getPodeData().getSource().getValue());
        DataDisplayElement element = new DataDisplayElement();
        element.setSource(source);
        element.setDataType("Occupancy");
        element.setDataValue(((PodeLaneData) dataDelivery.getPodeData().getPodeData().getDetector().getLaneData().toArray()[0]).getData().getOccupancy()+"");
        currentData.addOccupancyValue(element);
    }

    private void travelTimeDataReceived(ODEAgentMessage data) {
        PodeDataDelivery dataDelivery = (PodeDataDelivery) data.getFormattedMessage();

        // Ugly workaround hack to fix incident bug. For whatever reason our incidents sometimes come
        // over the travel time data target. No clue where this bug originates.
        if (dataDelivery.getPodeData().getPodeData().isIncidentSelected()) {
            incidentDataReceived(data);
            return;
        }

        String source = getSourceName(dataDelivery.getPodeData().getSource().getValue());
        DataDisplayElement element = new DataDisplayElement();
        element.setSource(source);
        element.setDataType("Travel Time");
        element.setDataValue(((PodeLaneData) dataDelivery.getPodeData().getPodeData().getDetector().getLaneData().toArray()[0]).getData().getTravelTimeInfo().getTravelTime().getValue()+"");
        currentData.addTravelTimeValue(element);
    }

    private void weatherDataReceived(ODEAgentMessage data) {
        PodeDataDelivery dataDelivery = (PodeDataDelivery) data.getFormattedMessage();
        String source = getSourceName(dataDelivery.getPodeData().getSource().getValue());
        DataDisplayElement element = new DataDisplayElement();
        element.setSource(source);
        String weatherReadingType = getWeatherReadingType(dataDelivery.getPodeData().getPodeData().getWeather());
        String weatherReadingValue = getWeatherValue(dataDelivery.getPodeData().getPodeData().getWeather());
        if(weatherReadingType != null && weatherReadingValue != null){
            element.setDataType(weatherReadingType);
            element.setDataValue(weatherReadingValue);
            currentData.addCurrentWeather(element);
        }
    }
    
    private String getWeatherValue(PodeWeatherinfo weatherInfo){
        PodeWeatherData weatherData = weatherInfo.getWeatherSensorData();
        if(weatherData.getSurfaceTemperature() != null){
            return weatherData.getSurfaceTemperature().getValue()+"";
        }else if(weatherData.getPrecipRate() != null){
            return weatherData.getPrecipRate().getValue()+"";
        }else if(weatherData.getVisibility() != null){
            return new String(weatherData.getVisibility().getValue());
        }
        return null;
    }
    
    private String getWeatherReadingType(PodeWeatherinfo weatherInfo){
        PodeWeatherData weatherData = weatherInfo.getWeatherSensorData();
        if(weatherData.getSurfaceTemperature() != null){
            return "Temperature";
        }else if(weatherData.getPrecipRate() != null){
            return "PrecipRate";
        }else if(weatherData.getVisibility() != null){
            return "Visibility";
        }
        return null;
    }

    
    private String getSourceName(PodeSource.EnumType enumType){
        if(enumType.equals(PodeSource.EnumType.blufax)){
            return "blufax";
        }else if(enumType.equals(PodeSource.EnumType.dms)){
            return "dms";
        }else if(enumType.equals(PodeSource.EnumType.ritis)){
            return "ritis";
        }else if(enumType.equals(PodeSource.EnumType.rtms)){
            return "rtms";
        }else if(enumType.equals(PodeSource.EnumType.spat)){
            return "spat";
        }else if(enumType.equals(PodeSource.EnumType.vdot)){
            return "vdot";
        }else if(enumType.equals(PodeSource.EnumType.wxde)){
            return "wxde";
        }
        return "unknown";
    }
    
    

    public String convertBytesToHex(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }


    /**
     * @return the weatherCollector
     */
    public ODECollector getWeatherCollector() {
        return weatherCollector;
    }

    /**
     * @param weatherCollector the weatherCollector to set
     */
    public void setWeatherCollector(ODECollector weatherCollector) {
        this.weatherCollector = weatherCollector;
    }

    /**
     * @return the volumeCollector
     */
    public ODECollector getVolumeCollector() {
        return volumeCollector;
    }

    /**
     * @param volumeCollector the volumeCollector to set
     */
    public void setVolumeCollector(ODECollector volumeCollector) {
        this.volumeCollector = volumeCollector;
    }

    /**
     * @return the occupancyCollector
     */
    public ODECollector getOccupancyCollector() {
        return occupancyCollector;
    }

    /**
     * @param occupancyCollector the occupancyCollector to set
     */
    public void setOccupancyCollector(ODECollector occupancyCollector) {
        this.occupancyCollector = occupancyCollector;
    }

    /**
     * @return the travelTimeCollector
     */
    public ODECollector getTravelTimeCollector() {
        return travelTimeCollector;
    }

    /**
     * @param travelTimeCollector the travelTimeCollector to set
     */
    public void setTravelTimeCollector(ODECollector travelTimeCollector) {
        this.travelTimeCollector = travelTimeCollector;
    }




}
