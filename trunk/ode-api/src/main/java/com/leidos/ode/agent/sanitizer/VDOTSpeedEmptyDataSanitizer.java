/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.sanitizer;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.vdot.VDOTSpeedData;
import com.leidos.ode.agent.sanitizer.ODESanitizer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cassadyja, lamde
 */
public class VDOTSpeedEmptyDataSanitizer implements ODESanitizer {

    public ODEAgentMessage sanitizeMessage(ODEAgentMessage message) throws ODESanitizeException {
        VDOTSpeedData vdotSpeedData = (VDOTSpeedData) message.getFormattedMessage();
        if(vdotSpeedData != null){
            List<VDOTSpeedData.VDOTSpeedDataElement> vdotSpeedDataElements = vdotSpeedData.getVdotSpeedDataElements();

            List<VDOTSpeedData.VDOTSpeedDataElement> zeroOccupancyElements = new ArrayList<VDOTSpeedData.VDOTSpeedDataElement>();

            //Iterate the VDOTSpeed data elements
            
            for (VDOTSpeedData.VDOTSpeedDataElement vdotSpeedDataElement : vdotSpeedDataElements) {
                //If the element's Volume is zero, then add it to the list of zero occupancy elements
            
                if (vdotSpeedDataElement.getVolume()== 0) {
            
                    zeroOccupancyElements.add(vdotSpeedDataElement);
                }
            }
            //Remove all elements with a occupancy value of zero
            vdotSpeedDataElements.removeAll(zeroOccupancyElements);
        }
        return message;
    }
}
