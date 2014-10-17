package com.leidos.ode.emulator;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.bsm.BSM;
import com.leidos.ode.agent.data.ritis.RITISSpeedData;
import com.leidos.ode.agent.data.vdot.VDOTSpeedData;
import com.leidos.ode.agent.datatarget.ODEDataTarget;
import com.leidos.ode.collector.ODECollector;
import com.leidos.ode.collector.datasource.CollectorDataSource;
import com.leidos.ode.collector.datasource.DataSource;
import com.leidos.ode.emulator.agent.EmulatorDataTarget;
import com.leidos.ode.util.ODEMessageType;
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

/**
 * @author cassadyja
 */
@Controller
public class ODEEmulator implements EmulatorDataListener, DisposableBean {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    @Autowired
    @Qualifier("ritisWeatherCollector")
    private ODECollector ritisWeatherCollector;
    @Autowired
    @Qualifier("ritisSpeedCollector")
    private ODECollector ritisSpeedCollector;
    @Autowired
    @Qualifier("vdotSpeedCollector")
    private ODECollector vdotSpeedCollector;
    @Autowired
    @Qualifier("vdotWeatherCollector")
    private ODECollector vdotWeatherCollector;
    @Autowired
    @Qualifier("vdotTravelTimeCollector")
    private ODECollector vdotTravelTimeCollector;
    @Autowired
    @Qualifier("bsmCollector")
    private ODECollector bsmCollector;
    private CurrentDataSet currentData = new CurrentDataSet();

    @RequestMapping(value = "startEmulator", method = RequestMethod.POST)
    public
    @ResponseBody
    String startEmulator(@RequestBody EmulatorCollectorList collectors) {
        for (String s : collectors.getCollectors()) {
            System.out.println(s);
            try {
                if (s.equalsIgnoreCase("RITISWeatherCollector")) {
                    ritisWeatherCollector.stop();
                    ((EmulatorDataTarget) ritisWeatherCollector.getAgent().getDataTarget()).setListener(this);
                    setCollectorDataSourceListenerForCollectorsDataSource(ritisWeatherCollector);
                    ritisWeatherCollector.startUp();
                } else if (s.equalsIgnoreCase("RITISSpeedCollector")) {
                    ritisSpeedCollector.stop();
                    ((EmulatorDataTarget) ritisSpeedCollector.getAgent().getDataTarget()).setListener(this);
                    setCollectorDataSourceListenerForCollectorsDataSource(ritisSpeedCollector);
                    ritisSpeedCollector.startUp();
                } else if (s.equalsIgnoreCase("VDOTSpeedCollector")) {
                    vdotSpeedCollector.stop();
                    ((EmulatorDataTarget) vdotSpeedCollector.getAgent().getDataTarget()).setListener(this);
                    setCollectorDataSourceListenerForCollectorsDataSource(vdotSpeedCollector);
                    vdotSpeedCollector.startUp();
                } else if (s.equalsIgnoreCase("VDOTWeatherCollector")) {
                    vdotWeatherCollector.stop();
                    ((EmulatorDataTarget) vdotWeatherCollector.getAgent().getDataTarget()).setListener(this);
                    setCollectorDataSourceListenerForCollectorsDataSource(vdotWeatherCollector);
                    vdotWeatherCollector.startUp();
                } else if (s.equalsIgnoreCase("VDOTTravelCollector")) {
                    vdotTravelTimeCollector.stop();
                    ((EmulatorDataTarget) vdotTravelTimeCollector.getAgent().getDataTarget()).setListener(this);
                    setCollectorDataSourceListenerForCollectorsDataSource(vdotTravelTimeCollector);
                    vdotTravelTimeCollector.startUp();
                } else if (s.equalsIgnoreCase("BSMCollector")) {
                    bsmCollector.stop();
                    ((EmulatorDataTarget) bsmCollector.getAgent().getDataTarget()).setListener(this);
                    setCollectorDataSourceListenerForCollectorsDataSource(bsmCollector);
                    bsmCollector.startUp();
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
    public
    @ResponseBody
    CurrentDataSet getCurrentData() {
        return currentData;
    }

    @Override
    public void destroy() {
        System.out.println("Being destroyed!");
        ritisWeatherCollector.stop();
        ritisSpeedCollector.stop();
        vdotSpeedCollector.stop();
        vdotWeatherCollector.stop();
        vdotTravelTimeCollector.stop();
        bsmCollector.stop();

    }

    public void dataReceived(String messageType, ODEAgentMessage data) {
        if (data != null) {
            if (ODEMessageType.BSM.equals(ODEMessageType.valueOf(messageType))) {
                bsmDataReceived(data);
            } else if (ODEMessageType.VDOTSpeed.equals(ODEMessageType.valueOf(messageType))) {
                vdotSpeedDataReceived(data);
            } else if (ODEMessageType.VDOTTravelTime.equals(ODEMessageType.valueOf(messageType))) {
                vdotTravelTimeDataReceived(data);
            } else if (ODEMessageType.VDOTWeather.equals(ODEMessageType.valueOf(messageType))) {
                vdotWeatherDataReceived(data);
            } else if (ODEMessageType.RITISSpeed.equals(ODEMessageType.valueOf(messageType))) {
                ritisSpeedDataReceived(data);
            } else if (ODEMessageType.RITISWeather.equals(ODEMessageType.valueOf(messageType))) {
                ritisWeatherDataReceived(data);
            }
        }
    }

    public void ritisSpeedDataReceived(ODEAgentMessage odeAgentMessage) {
        Object data = odeAgentMessage.getFormattedMessage();
        RITISSpeedData eastData = getRITISDataAverage(data, "E");
        RITISSpeedData westData = getRITISDataAverage(data, "W");
        //TODO: set values to correct directions in current data
        currentData.setRitisSpeedDataEastValue(eastData);
        currentData.setRitisSpeedDataWestValue(westData);

    }

    private RITISSpeedData getRITISDataAverage(Object data, String direction) {
        int count = 0;
        int avgSpeed = 0;
        int occ = 0;
        int volume = 0;

        RITISSpeedData ritisSpeedData = (RITISSpeedData) data;
        List<ZoneDataCollectionPeriodRITIS> zoneDataCollectionPeriodRITISList = ritisSpeedData.getZoneDetectorDataRITIS().getCollectionPeriod().getCollectionPeriodItem();
        for (ZoneDataCollectionPeriodRITIS zoneDataCollectionPeriodRITIS : zoneDataCollectionPeriodRITISList) {
            List<ZoneReportRITIS> zoneReportRITISList = zoneDataCollectionPeriodRITIS.getZoneReports().getZoneReport();
            for (ZoneReportRITIS zoneReportRITIS : zoneReportRITISList) {
                List<ZoneDataRITIS> zoneDataRITISList = zoneReportRITIS.getZoneData().getZoneDataItem();
                for (ZoneDataRITIS zoneDataRITIS : zoneDataRITISList) {
                    //TODO: determine if this item is for the correct direction we are looking for.
                    avgSpeed += zoneDataRITIS.getZoneVehicleSpeed();
                    occ += zoneDataRITIS.getOccupancy();
                    volume += zoneDataRITIS.getZoneVehicleCount();
                    count++;
                }
            }
        }

        avgSpeed = avgSpeed / count;
        occ = occ / count;
        volume = volume / count;

        RITISSpeedData returnRITISSpeedData = new RITISSpeedData();
        org.ritis.schema.tmdd_0_0_0.ObjectFactory objectFactory = new org.ritis.schema.tmdd_0_0_0.ObjectFactory();
        ZoneDetectorDataRITIS zoneDetectorDataRITIS = objectFactory.createZoneDetectorDataRITIS();
        ZoneDetectorDataRITIS.CollectionPeriod collectionPeriod = objectFactory.createZoneDetectorDataRITISCollectionPeriod();
        zoneDataCollectionPeriodRITISList = collectionPeriod.getCollectionPeriodItem();
        ZoneDataCollectionPeriodRITIS zoneDataCollectionPeriodRITIS = objectFactory.createZoneDataCollectionPeriodRITIS();
        ZoneDataCollectionPeriodRITIS.ZoneReports zps = new ZoneDataCollectionPeriodRITIS.ZoneReports();
        zoneDataCollectionPeriodRITIS.setZoneReports(zps);
        List<ZoneReportRITIS> zoneReportRITISList = zoneDataCollectionPeriodRITIS.getZoneReports().getZoneReport();
        ZoneReportRITIS zoneReportRITIS = objectFactory.createZoneReportRITIS();
        ZoneReportRITIS.ZoneData zd = new ZoneReportRITIS.ZoneData();
        zoneReportRITIS.setZoneData(zd);
        List<ZoneDataRITIS> zoneDataRITISList = zoneReportRITIS.getZoneData().getZoneDataItem();
        ZoneDataRITIS zoneDataRITIS = objectFactory.createZoneDataRITIS();
        zoneDataRITIS.setZoneVehicleSpeed((short) avgSpeed);
        zoneDataRITIS.setOccupancy((long) occ);
        zoneDataRITIS.setZoneVehicleCount((long) volume);
        zoneDataRITISList.add(zoneDataRITIS);
        zoneReportRITISList.add(zoneReportRITIS);
        zoneDataCollectionPeriodRITISList.add(zoneDataCollectionPeriodRITIS);
        zoneDetectorDataRITIS.setCollectionPeriod(collectionPeriod);
        returnRITISSpeedData.setZoneDetectorDataRITIS(zoneDetectorDataRITIS);

        return returnRITISSpeedData;
    }

    public void ritisWeatherDataReceived(ODEAgentMessage data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void vdotSpeedDataReceived(ODEAgentMessage odeAgentMessage) {
        Object data = odeAgentMessage.getFormattedMessage();

        VDOTSpeedData eastData = getVDOTSpeedDataAverage(data, "E");
        VDOTSpeedData westData = getVDOTSpeedDataAverage(data, "W");
        //TODO set values to current data.
        currentData.setVdotSpeedDataEastValue(eastData);
        currentData.setVdotSpeedDataWestValue(westData);
    }

    private VDOTSpeedData getVDOTSpeedDataAverage(Object data, String direction) {
        int avgSpeed = 0;
        int count = 0;
        int occ = 0;
        int volume = 0;
        VDOTSpeedData vdotSpeedData = (VDOTSpeedData) data;
        System.out.println("Emulator VDOT Speed data list Size: " + vdotSpeedData.getVdotSpeedDataElements().size());
        for (VDOTSpeedData.VDOTSpeedDataElement vdotSpeedDataElement : vdotSpeedData.getVdotSpeedDataElements()) {
            if (direction.equals(vdotSpeedDataElement.getLaneDirection())) {
                avgSpeed += vdotSpeedDataElement.getSpeed();
                volume += vdotSpeedDataElement.getVolume();
                occ += vdotSpeedDataElement.getOccupancy();
            }
            count++;
        }

        avgSpeed = avgSpeed / count;
        volume = volume / count;
        occ = occ / count;
        VDOTSpeedData returnVDOTSpeedData = new VDOTSpeedData();
        VDOTSpeedData.VDOTSpeedDataElement vdotSpeedDataElement = new VDOTSpeedData.VDOTSpeedDataElement();
        vdotSpeedDataElement.setLaneDirection(direction);
        vdotSpeedDataElement.setSpeed(avgSpeed);
        vdotSpeedDataElement.setVolume(volume);
        vdotSpeedDataElement.setOccupancy(occ);
        returnVDOTSpeedData.getVdotSpeedDataElements().add(vdotSpeedDataElement);
        return returnVDOTSpeedData;
    }

    public void vdotTravelTimeDataReceived(ODEAgentMessage data) {
        //TODO: finish weather
    }

    public void vdotWeatherDataReceived(ODEAgentMessage data) {
        //TODO: finish weather
    }

    public void bsmDataReceived(ODEAgentMessage data) {
        double heading = ((BSM) data.getFormattedMessage()).getHeading();
        String hex = convertBytesToHex(data.getMessagePayload());
        String ip = getIPAddressForBSM(hex);
        if (heading >= 180 && heading < 360) {
            //WEST
            currentData.addBSMWestData(ip, (BSM) data.getFormattedMessage());
        } else if (heading >= 0 && heading < 180) {
            //EAST
            currentData.addBSMEastData(ip, (BSM) data.getFormattedMessage());
        }
    }

    public String getIPAddressForBSM(String hex) {
        int addressEnd = hex.indexOf("7766");
        if (addressEnd > -1) {
            return hex.substring(0, addressEnd);
        }
        return null;
    }

    public String convertBytesToHex(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }

    /**
     * @return the ritisWeatherCollector
     */
    public ODECollector getRitisWeatherCollector() {
        return ritisWeatherCollector;
    }

    /**
     * @param ritisWeatherCollector the ritisWeatherCollector to set
     */
    public void setRitisWeatherCollector(ODECollector ritisWeatherCollector) {
        this.ritisWeatherCollector = ritisWeatherCollector;
    }

    /**
     * @return the ritisSpeedCollector
     */
    public ODECollector getRitisSpeedCollector() {
        return ritisSpeedCollector;
    }

    /**
     * @param ritisSpeedCollector the ritisSpeedCollector to set
     */
    public void setRitisSpeedCollector(ODECollector ritisSpeedCollector) {
        this.ritisSpeedCollector = ritisSpeedCollector;
    }

    /**
     * @return the vdotSpeedCollector
     */
    public ODECollector getVdotSpeedCollector() {
        return vdotSpeedCollector;
    }

    /**
     * @param vdotSpeedCollector the vdotSpeedCollector to set
     */
    public void setVdotSpeedCollector(ODECollector vdotSpeedCollector) {
        this.vdotSpeedCollector = vdotSpeedCollector;
    }

    /**
     * @return the vdotWeatherCollector
     */
    public ODECollector getVdotWeatherCollector() {
        return vdotWeatherCollector;
    }

    /**
     * @param vdotWeatherCollector the vdotWeatherCollector to set
     */
    public void setVdotWeatherCollector(ODECollector vdotWeatherCollector) {
        this.vdotWeatherCollector = vdotWeatherCollector;
    }

    /**
     * @return the vdotTravelTimeCollector
     */
    public ODECollector getVdotTravelTimeCollector() {
        return vdotTravelTimeCollector;
    }

    /**
     * @param vdotTravelTimeCollector the vdotTravelTimeCollector to set
     */
    public void setVdotTravelTimeCollector(ODECollector vdotTravelTimeCollector) {
        this.vdotTravelTimeCollector = vdotTravelTimeCollector;
    }

    /**
     * @return the bsmCollector
     */
    public ODECollector getBsmCollector() {
        return bsmCollector;
    }

    /**
     * @param bsmCollector the bsmCollector to set
     */
    public void setBsmCollector(ODECollector bsmCollector) {
        this.bsmCollector = bsmCollector;
    }


}
