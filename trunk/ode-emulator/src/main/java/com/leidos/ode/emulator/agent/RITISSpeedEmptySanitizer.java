/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.emulator.agent;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.ritis.RITISData;
import com.leidos.ode.agent.data.ritis.RITISSpeedData;
import com.leidos.ode.agent.sanitizer.ODESanitizer;
import generated.CollectionPeriod;
import generated.CollectionPeriod.CollectionPeriodItem.ZoneReports.ZoneReport.ZoneData.ZoneDataItem;

import java.util.List;

/**
 *
 * @author cassadyja
 */
public class RITISSpeedEmptySanitizer implements ODESanitizer{

    public ODEAgentMessage sanitizeMessage(ODEAgentMessage message) throws ODESanitizeException {
        List<RITISData> speedData = (List<RITISData>)message.getFormattedMessage();
        for(RITISData rd: speedData){
            RITISSpeedData rsd = (RITISSpeedData)rd;
            List<ZoneDataItem> items = rsd.getCollectionPeriod().getCollectionPeriodItem().getZoneReports().getZoneReport().getZoneData().getZoneDataItem();
            
            for(int i=0;i<items.size();i++){
                ZoneDataItem item = items.get(i);
                if(item.getZoneVehicleCount() == 0){
                    items.remove(item);
                }
            }
        }
        return message;
    }
    
}
