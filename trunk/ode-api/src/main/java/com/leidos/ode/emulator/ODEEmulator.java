/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.leidos.ode.emulator;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.bsm.BSM;
import com.leidos.ode.collector.ODECollector;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

/**
 *
 * @author cassadyja
 */
@Controller
public class ODEEmulator implements EmulatorDataListener{
    
    
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
    
    //This needs to be a WS method, probably POST
    //Will take a list of names representing the collectors to be started.
    public void startEmulator(){
        //lookup collectors in the context, start them.
        
        
    }
    
    
    
    public void dataReceived(String messageType, ODEAgentMessage data) {
        if(MessageTypes.BSM.equals(MessageTypes.valueOf(messageType))){
            bsmDataReceived(data);
        }else if(MessageTypes.VDOTSpeed.equals(MessageTypes.valueOf(messageType))){
            vdotSpeedDataReceived(data);
        }else if(MessageTypes.VDOTTravel.equals(MessageTypes.valueOf(messageType))){
            vdotTravelTimeDataReceived(data);
        }else if(MessageTypes.VDOTWeather.equals(MessageTypes.valueOf(messageType))){
            vdotWeatherDataReceived(data);
        }else if(MessageTypes.RITISSpeed.equals(MessageTypes.valueOf(messageType))){
            ritisSpeedDataReceived(data);
        }else if(MessageTypes.RITISWeather.equals(MessageTypes.valueOf(messageType))){
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
        double heading = ((BSM)data.getFormattedMessage()).getHeading();
        String hex = convertBytesToHex(data.getMessagePayload());
        String ip = getIPAddressForBSM(hex);
        if(heading >= 180 && heading < 360){
            //WEST
            currentData.addBSMWestData(ip, (BSM)data.getFormattedMessage());
        }else if(heading >=0 && heading < 180){
            //EAST
            currentData.addBSMEastData(ip, (BSM)data.getFormattedMessage());
        }
    }

    
    public String getIPAddressForBSM(String hex){
        int addressEnd = hex.indexOf("7766");
        if(addressEnd > -1){
            return hex.substring(0,addressEnd);
        }
        return null;
    }
    public String convertBytesToHex(byte[] bytes){
        String hexString = DatatypeConverter.printHexBinary(bytes);
        return  hexString;
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
