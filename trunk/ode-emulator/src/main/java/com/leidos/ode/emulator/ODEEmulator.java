package com.leidos.ode.emulator;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.bsm.BSM;
import com.leidos.ode.agent.datatarget.ODEDataTarget;
import com.leidos.ode.collector.ODECollector;
import com.leidos.ode.collector.datasource.CollectorDataSource;
import com.leidos.ode.emulator.agent.EmulatorDataTarget;
import com.leidos.ode.util.ODEMessageType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.xml.bind.DatatypeConverter;

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
    public @ResponseBody String startEmulator(@RequestBody EmulatorCollectorList collectors) {
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
    public @ResponseBody CurrentDataSet getCurrentData() {
        return currentData;
    }

    public void dataReceived(String messageType, ODEAgentMessage data) {
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

    public void ritisSpeedDataReceived(ODEAgentMessage data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void ritisWeatherDataReceived(ODEAgentMessage data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void vdotSpeedDataReceived(ODEAgentMessage data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
