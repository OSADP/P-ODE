package com.leidos.ode.emulator;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.bsm.BSM;
import com.leidos.ode.agent.data.ritis.RITISData;
import com.leidos.ode.agent.data.ritis.RITISSpeedData;
import com.leidos.ode.agent.data.vdot.VDOTData;
import com.leidos.ode.agent.data.vdot.VDOTSpeedData;
import com.leidos.ode.agent.datatarget.ODEDataTarget;
import com.leidos.ode.collector.ODECollector;
import com.leidos.ode.collector.datasource.CollectorDataSource;
import com.leidos.ode.emulator.agent.EmulatorDataTarget;
import com.leidos.ode.util.ODEMessageType;
import org.apache.log4j.Logger;
import org.ritis.schema.tmdd_0_0_0.ZoneDataCollectionPeriodRITIS;
import org.ritis.schema.tmdd_0_0_0.ZoneDataRITIS;
import org.ritis.schema.tmdd_0_0_0.ZoneDetectorDataRITIS;
import org.ritis.schema.tmdd_0_0_0.ZoneReportRITIS;
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
public class ODEEmulator implements EmulatorDataListener {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    @Autowired
    @Qualifier("RITISWeatherCollector")
    private ODECollector ritisWeatherCollector;
    @Autowired
    @Qualifier("RITISSpeedCollector")
    private ODECollector ritisSpeedCollector;
    @Autowired
    @Qualifier("VDOTSpeedCollector")
    private ODECollector vdotSpeedCollector;
    @Autowired
    @Qualifier("VDOTWeatherCollector")
    private ODECollector vdotWeatherCollector;
    @Autowired
    @Qualifier("VDOTTravelCollector")
    private ODECollector vdotTravelTimeCollector;
    @Autowired
    @Qualifier("BSMCollector")
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
                    ritisWeatherCollector.startUp();
                } else if (s.equalsIgnoreCase("RITISSpeedCollector")) {
                    ritisSpeedCollector.stop();
                    ((EmulatorDataTarget) ritisSpeedCollector.getAgent().getDataTarget()).setListener(this);
                    ritisSpeedCollector.startUp();
                } else if (s.equalsIgnoreCase("VDOTSpeedCollector")) {
                    vdotSpeedCollector.stop();
                    ((EmulatorDataTarget) vdotSpeedCollector.getAgent().getDataTarget()).setListener(this);
                    vdotSpeedCollector.startUp();
                } else if (s.equalsIgnoreCase("VDOTWeatherCollector")) {
                    vdotWeatherCollector.stop();
                    ((EmulatorDataTarget) vdotWeatherCollector.getAgent().getDataTarget()).setListener(this);
                    vdotWeatherCollector.startUp();
                } else if (s.equalsIgnoreCase("VDOTTravelCollector")) {
                    vdotTravelTimeCollector.stop();
                    ((EmulatorDataTarget) vdotTravelTimeCollector.getAgent().getDataTarget()).setListener(this);
                    vdotTravelTimeCollector.startUp();
                } else if (s.equalsIgnoreCase("BSMCollector")) {
                    bsmCollector.stop();
                    ((EmulatorDataTarget) bsmCollector.getAgent().getDataTarget()).setListener(this);
                    bsmCollector.startUp();
                }
            } catch (CollectorDataSource.DataSourceException ex) {
                logger.error(ex.getLocalizedMessage());
            } catch (ODEDataTarget.DataTargetException ex) {
                logger.error(ex.getLocalizedMessage());
            }
        }
        return "<response>started</response>";
    }

    @RequestMapping(value = "getCurrentData", method = RequestMethod.GET)
    public
    @ResponseBody
    CurrentDataSet getCurrentData() {
        return currentData;
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

    public void ritisSpeedDataReceived(ODEAgentMessage data) {
        RITISData ritisData = (RITISData) data.getFormattedMessage();
        RITISSpeedData eastData = getRITISDataAverage(ritisData, "E");
        RITISSpeedData westData = getRITISDataAverage(ritisData, "W");
        //TODO: set values to correct directions in current data

    }

    private RITISSpeedData getRITISDataAverage(RITISData ritisData, String direction) {
        int count = 0;
        int avgSpeed = 0;

        RITISSpeedData ritisSpeedData = (RITISSpeedData) ritisData;
        List<ZoneDataCollectionPeriodRITIS> zoneDataCollectionPeriodRITISList = ritisSpeedData.getZoneDetectorDataRITIS().getCollectionPeriod().getCollectionPeriodItem();
        for (ZoneDataCollectionPeriodRITIS zoneDataCollectionPeriodRITIS : zoneDataCollectionPeriodRITISList) {
            List<ZoneReportRITIS> zoneReportRITISList = zoneDataCollectionPeriodRITIS.getZoneReports().getZoneReport();
            for (ZoneReportRITIS zoneReportRITIS : zoneReportRITISList) {
                List<ZoneDataRITIS> zoneDataRITISList = zoneReportRITIS.getZoneData().getZoneDataItem();
                for (ZoneDataRITIS zoneDataRITIS : zoneDataRITISList) {
                    //TODO: determine if this item is for the correct direction we are looking for.
                    avgSpeed += zoneDataRITIS.getZoneVehicleSpeed();
                    count++;
                }
            }
        }

        avgSpeed = avgSpeed / count;

        RITISSpeedData returnRITISSpeedData = new RITISSpeedData();
        org.ritis.schema.tmdd_0_0_0.ObjectFactory objectFactory = new org.ritis.schema.tmdd_0_0_0.ObjectFactory();
        ZoneDetectorDataRITIS zoneDetectorDataRITIS = objectFactory.createZoneDetectorDataRITIS();
        ZoneDetectorDataRITIS.CollectionPeriod collectionPeriod = objectFactory.createZoneDetectorDataRITISCollectionPeriod();
        zoneDataCollectionPeriodRITISList = collectionPeriod.getCollectionPeriodItem();
        ZoneDataCollectionPeriodRITIS zoneDataCollectionPeriodRITIS = objectFactory.createZoneDataCollectionPeriodRITIS();
        List<ZoneReportRITIS> zoneReportRITISList = zoneDataCollectionPeriodRITIS.getZoneReports().getZoneReport();
        ZoneReportRITIS zoneReportRITIS = objectFactory.createZoneReportRITIS();
        List<ZoneDataRITIS> zoneDataRITISList = zoneReportRITIS.getZoneData().getZoneDataItem();
        ZoneDataRITIS zoneDataRITIS = objectFactory.createZoneDataRITIS();
        zoneDataRITIS.setZoneVehicleSpeed((short) avgSpeed);
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

    public void vdotSpeedDataReceived(ODEAgentMessage data) {
        VDOTData vdotData = (VDOTData) data.getFormattedMessage();
        VDOTSpeedData eastData = getVDOTSpeedDataAverage(vdotData, "E");
        VDOTSpeedData westData = getVDOTSpeedDataAverage(vdotData, "W");
        //TODO set values to current data.
    }

    private VDOTSpeedData getVDOTSpeedDataAverage(VDOTData data, String direction) {
        int avgSpeed = 0;
        int count = 0;

        VDOTSpeedData vdotSpeedData = (VDOTSpeedData) data;

        for (VDOTSpeedData.VDOTSpeedDataElement vdotSpeedDataElement : vdotSpeedData.getVdotSpeedDataElements()) {
            if (direction.equals(vdotSpeedDataElement.getLaneDirection())) {
                avgSpeed += vdotSpeedDataElement.getSpeed();
            }
            count++;
        }

        avgSpeed = avgSpeed / count;
        VDOTSpeedData returnVDOTSpeedData = new VDOTSpeedData();
        VDOTSpeedData.VDOTSpeedDataElement vdotSpeedDataElement = new VDOTSpeedData.VDOTSpeedDataElement();
        vdotSpeedDataElement.setLaneDirection(direction);
        vdotSpeedDataElement.setSpeed(avgSpeed);
        return returnVDOTSpeedData;
    }

    public void vdotTravelTimeDataReceived(ODEAgentMessage data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void vdotWeatherDataReceived(ODEAgentMessage data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
