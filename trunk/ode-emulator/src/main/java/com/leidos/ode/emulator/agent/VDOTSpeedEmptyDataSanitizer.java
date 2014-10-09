/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.emulator.agent;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.vdot.VDOTData;
import com.leidos.ode.agent.data.vdot.VDOTSpeedData;
import com.leidos.ode.agent.sanitizer.ODESanitizer;
import java.util.List;

/**
 *
 * @author cassadyja
 */
public class VDOTSpeedEmptyDataSanitizer implements ODESanitizer{

    public ODEAgentMessage sanitizeMessage(ODEAgentMessage message) throws ODESanitizeException {
        List<VDOTData> speedData = (List<VDOTData>)message.getFormattedMessage();
        for(int i=0;i<speedData.size();i++){
            VDOTData data = speedData.get(i);
            VDOTSpeedData vsd = (VDOTSpeedData)data;
            if(vsd.getOccupancy() == 0){
                speedData.remove(data);
            }
        }
        
        return message;
    }
    
    
}
