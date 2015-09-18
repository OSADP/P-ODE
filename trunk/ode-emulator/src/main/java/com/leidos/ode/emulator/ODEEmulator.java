package com.leidos.ode.emulator;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.datatarget.ODEDataTarget;
import com.leidos.ode.collector.ODECollector;
import com.leidos.ode.collector.datasource.CollectorDataSource;
import com.leidos.ode.collector.datasource.DataSource;
import com.leidos.ode.data.PodeDataDelivery;
import com.leidos.ode.data.PodeLaneDirection;
import com.leidos.ode.data.PodeSource;
import com.leidos.ode.data.PodeWeatherData;
import com.leidos.ode.data.PodeWeatherinfo;
import com.leidos.ode.emulator.agent.EmulatorDataTarget;

import com.leidos.ode.util.ODEMessageType;
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

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
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
    
    
    private CurrentDataSet currentData = new CurrentDataSet();


    @RequestMapping(value = "startEmulator", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    String startEmulator(@RequestBody EmulatorCollectorList collectors) {
        currentData = new CurrentDataSet();
                
        destroy();
        
        for (String s : collectors.getCollectors()) {
            try {
                if (s.equalsIgnoreCase("SpeedCollector")) {
                    ((EmulatorDataTarget) speedCollector.getAgent().getDataTarget()).setListener(this);
                    setCollectorDataSourceListenerForCollectorsDataSource(speedCollector);
                    speedCollector.startUp();
                } else if (s.equalsIgnoreCase("VolumeCollector")) {
                    ((EmulatorDataTarget) getVolumeCollector().getAgent().getDataTarget()).setListener(this);
                    setCollectorDataSourceListenerForCollectorsDataSource(getVolumeCollector());
                    getVolumeCollector().startUp();
                } else if (s.equalsIgnoreCase("OccupancyCollector")) {
                    ((EmulatorDataTarget) getOccupancyCollector().getAgent().getDataTarget()).setListener(this);
                    setCollectorDataSourceListenerForCollectorsDataSource(getOccupancyCollector());
                    getOccupancyCollector().startUp();
                } else if (s.equalsIgnoreCase("TravelTimeCollector")) {
                    ((EmulatorDataTarget) getTravelTimeCollector().getAgent().getDataTarget()).setListener(this);
                    setCollectorDataSourceListenerForCollectorsDataSource(getTravelTimeCollector());
                    getTravelTimeCollector().startUp();
                } else if (s.equalsIgnoreCase("WeatherCollector")) {
                    ((EmulatorDataTarget) getWeatherCollector().getAgent().getDataTarget()).setListener(this);
                    setCollectorDataSourceListenerForCollectorsDataSource(getWeatherCollector());
                    getWeatherCollector().startUp();
                } 
                
            } catch (ODEDataTarget.DataTargetException ex) {
                logger.error(ex.getLocalizedMessage());
            }
        }
        return "<response>started</response>";
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
            } 
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
        element.setDataType("Speed");
        element.setDataValue(dataDelivery.getPodeData().getPodeData().getDetector().getLaneData().getData().getSpeed().getValue()+"");
        logger.debug("EMULATOR: Speed Direction value: "+dataDelivery.getPodeData().getPodeData().getDetector().getLaneData().getLane().getLaneDirection().getValue().name()+" From: "+source);
        if(dataDelivery.getPodeData().getPodeData().getDetector().getLaneData().getLane().getLaneDirection().getValue().equals(PodeLaneDirection.EnumType.east)){
            currentData.addSpeedEastValue(element);
        }else{
            currentData.addSpeedWestValue(element);
        }
    }

    private void volumeDataReceived(ODEAgentMessage data) {
        PodeDataDelivery dataDelivery = (PodeDataDelivery) data.getFormattedMessage();
        String source = getSourceName(dataDelivery.getPodeData().getSource().getValue());
        DataDisplayElement element = new DataDisplayElement();
        element.setSource(source);
        element.setDataType("Volume");
        element.setDataValue(dataDelivery.getPodeData().getPodeData().getDetector().getLaneData().getData().getVolume()+"");
        if(dataDelivery.getPodeData().getPodeData().getDetector().getLaneData().getLane().getLaneDirection().getValue().equals(PodeLaneDirection.EnumType.east)){
            currentData.addVolumeEastValue(element);
        }else{
            currentData.addVolumeWestValue(element);
        }
        
    }

    private void occupancyDataReceived(ODEAgentMessage data) {
        PodeDataDelivery dataDelivery = (PodeDataDelivery) data.getFormattedMessage();
        String source = getSourceName(dataDelivery.getPodeData().getSource().getValue());
        DataDisplayElement element = new DataDisplayElement();
        element.setSource(source);
        element.setDataType("Occupancy");
        element.setDataValue(dataDelivery.getPodeData().getPodeData().getDetector().getLaneData().getData().getOccupancy()+"");
        if(dataDelivery.getPodeData().getPodeData().getDetector().getLaneData().getLane().getLaneDirection().getValue().equals(PodeLaneDirection.EnumType.east)){
            currentData.addOccupancyEastValue(element);
        }else{
            currentData.addOccupancyWestValue(element);
        }
    }

    private void travelTimeDataReceived(ODEAgentMessage data) {
        PodeDataDelivery dataDelivery = (PodeDataDelivery) data.getFormattedMessage();
        String source = getSourceName(dataDelivery.getPodeData().getSource().getValue());
        DataDisplayElement element = new DataDisplayElement();
        element.setSource(source);
        element.setDataType("Travel Time");
        element.setDataValue(dataDelivery.getPodeData().getPodeData().getDetector().getLaneData().getData().getTravelTimeInfo().getTravelTime().getValue()+"");
        if(dataDelivery.getPodeData().getPodeData().getDetector().getLaneData().getLane().getLaneDirection().getValue().equals(PodeLaneDirection.EnumType.east)){
            currentData.addTravelTimeEastValue(element);
        }else{
            currentData.addTravelTimeWestValue(element);
        }
        
                
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
        PodeWeatherData weatherData = weatherInfo.getWeatherSenorData();
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
        PodeWeatherData weatherData = weatherInfo.getWeatherSenorData();
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
