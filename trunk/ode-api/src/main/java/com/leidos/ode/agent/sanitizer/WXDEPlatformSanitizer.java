/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.sanitizer;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.wxde.WXDEData;
import com.leidos.ode.agent.sanitizer.ODESanitizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author cassadyja
 */
public class WXDEPlatformSanitizer  implements ODESanitizer {
    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);

    private String[] platforms;
    private String[] readings;
    
    private Set<String> platformSet;
    private Set<String> readingsSet;
    
    public ODEAgentMessage sanitizeMessage(ODEAgentMessage message) throws ODESanitizeException {
        WXDEData data = (WXDEData)message.getFormattedMessage();
        if(data != null){
            List<WXDEData.WXDEDataElement> elements =  data.getWxdeDataElements();
            List<WXDEData.WXDEDataElement> toKeep = new ArrayList<WXDEData.WXDEDataElement>();
            logger.debug("Weather Sanitize Start: "+elements.size());
            for(WXDEData.WXDEDataElement element: elements){
                if(isValidPlatform(element.getStationCode()) ){
                    if(isValidReading(element.getObsTypeName())){
                        toKeep.add(element);
                    }
                }
            }
            logger.debug("Weather Sanitize Keeping: "+toKeep.size());
            data.setWxdeDataElements(toKeep);
        }
        
        
        return message;
    }
    
    private boolean isValidPlatform(String platform){
        for(String s: platforms){
            logger.debug("Platform: "+platform+" S: "+s);
            if(s.equalsIgnoreCase(platform)){
                logger.debug("Matched!");
                return true;
            }
        }
        return false;
    }
    
    private boolean isValidReading(String reading){
        for(String s: readings){
            logger.debug("Reading: "+reading+" S: "+s);
            if(s.equalsIgnoreCase(reading)){
                logger.debug("Matched!");
                return true;
            }
        }
        return false;
    }

    private Set<String> getPlatformSet(){
        if(platformSet == null){
            platformSet = new HashSet<String>(Arrays.asList(getPlatforms()));
        }
        return platformSet;
    }
    
    private Set<String> getReadingsSet(){
        if(readingsSet == null){
            readingsSet = new HashSet<String>(Arrays.asList(getReadings()));
        }
        return readingsSet;
    }

    /**
     * @return the platforms
     */
    public String[] getPlatforms() {
        return platforms;
    }

    /**
     * @param platforms the platforms to set
     */
    public void setPlatforms(String[] platforms) {
        this.platforms = platforms;
    }

    /**
     * @return the readings
     */
    public String[] getReadings() {
        return readings;
    }

    /**
     * @param readings the readings to set
     */
    public void setReadings(String[] readings) {
        this.readings = readings;
    }
    

    
}
