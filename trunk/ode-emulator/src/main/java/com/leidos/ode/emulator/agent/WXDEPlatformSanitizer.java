/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.emulator.agent;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.wxde.WXDEData;
import com.leidos.ode.agent.sanitizer.ODESanitizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author cassadyja
 */
public class WXDEPlatformSanitizer  implements ODESanitizer {

    private String[] platforms;
    private String[] readings;
    
    private Set<String> platformSet;
    private Set<String> readingsSet;
    
    public ODEAgentMessage sanitizeMessage(ODEAgentMessage message) throws ODESanitizeException {
        WXDEData data = (WXDEData)message.getFormattedMessage();
        if(data != null){
            List<WXDEData.WXDEDataElement> elements =  data.getWxdeDataElements();
            List<WXDEData.WXDEDataElement> toRemove = new ArrayList<WXDEData.WXDEDataElement>();
            for(WXDEData.WXDEDataElement element: elements){
                if(!getPlatformSet().contains(element.getPlatformCode())){
                    toRemove.add(element);
                }else if(!getReadingsSet().contains(element.getObsTypeName())){
                    toRemove.add(element);
                }
            }
            data.getWxdeDataElements().removeAll(toRemove);
        }
        
        
        return message;
    }

    private Set<String> getPlatformSet(){
        if(platformSet == null){
            platformSet = new HashSet<String>(Arrays.asList(platforms));
        }
        return platformSet;
    }
    
    private Set<String> getReadingsSet(){
        if(readingsSet == null){
            readingsSet = new HashSet<String>(Arrays.asList(readings));
        }
        return readingsSet;
    }
    

    
}
