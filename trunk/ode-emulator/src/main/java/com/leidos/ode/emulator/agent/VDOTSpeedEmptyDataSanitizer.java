/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.emulator.agent;

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
        List<VDOTSpeedData.VDOTSpeedDataElement> vdotSpeedDataElements = vdotSpeedData.getVdotSpeedDataElements();

        List<VDOTSpeedData.VDOTSpeedDataElement> zeroOccupancyElements = new ArrayList<VDOTSpeedData.VDOTSpeedDataElement>();

        //Iterate the VDOTSpeed data elements
        System.out.println("Have VDOT Total Elements of: "+vdotSpeedDataElements.size());
        for (VDOTSpeedData.VDOTSpeedDataElement vdotSpeedDataElement : vdotSpeedDataElements) {
            //If the element's Volume is zero, then add it to the list of zero occupancy elements
            System.out.println("Have VDOT Volume of: "+vdotSpeedDataElement.getVolume());
            if (vdotSpeedDataElement.getVolume()== 0) {
                System.out.println("Removing");    
                zeroOccupancyElements.add(vdotSpeedDataElement);
            }
        }
        //Remove all elements with a occupancy value of zero
        vdotSpeedDataElements.removeAll(zeroOccupancyElements);
        return message;
    }
}
