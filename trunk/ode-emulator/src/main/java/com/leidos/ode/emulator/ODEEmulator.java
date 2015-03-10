package com.leidos.ode.emulator;

import com.fastlanesw.bfw.RouteStatusExt;
import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.blufax.BluFaxLinkData;
import com.leidos.ode.agent.data.blufax.BluFaxRouteData;
import com.leidos.ode.agent.data.bsm.BSM;
import com.leidos.ode.agent.data.ritis.RITISSpeedData;
import com.leidos.ode.agent.data.vdot.VDOTSpeedData;
import com.leidos.ode.agent.data.wxde.WXDEData;
import com.leidos.ode.agent.datatarget.ODEDataTarget;
import com.leidos.ode.collector.ODECollector;
import com.leidos.ode.collector.datasource.CollectorDataSource;
import com.leidos.ode.collector.datasource.DataSource;
import com.leidos.ode.data.PodeDataDelivery;
import com.leidos.ode.data.PodeLaneDirection;
import com.leidos.ode.data.PodeSource;
import com.leidos.ode.emulator.agent.EmulatorDataTarget;
import com.leidos.ode.util.ODEMessageType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.ritis.schema.tmdd_0_0_0.ZoneDataCollectionPeriodRITIS;
import org.ritis.schema.tmdd_0_0_0.ZoneDataRITIS;
import org.ritis.schema.tmdd_0_0_0.ZoneDetectorDataRITIS;
import org.ritis.schema.tmdd_0_0_0.ZoneReportRITIS;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.xml.bind.DatatypeConverter;
import java.util.List;
import java.util.Map;
import org.bn.annotations.ASN1EnumItem;
import org.tmdd._3.messages.LinkStatus;
import org.tmdd._3.messages.LinkStatusList;
import org.tmdd._3.messages.RouteStatus;
import org.tmdd._3.messages.RouteStatusList;

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


    @RequestMapping(value = "startEmulator", method = RequestMethod.POST)
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

    @RequestMapping(value = "getCurrentData", method = RequestMethod.GET)
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
        element.setSource(source);
        element.setDataType("Speed");
        element.setDataValue(dataDelivery.getPodeData().getPodeData().getDetector().getLaneData().getData().getSpeed().getValue()+"");
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
        
    }

    private void weatherDataReceived(ODEAgentMessage data) {
        PodeDataDelivery dataDelivery = (PodeDataDelivery) data.getFormattedMessage();
        
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
