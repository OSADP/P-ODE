/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.emulator.agent;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.agent.data.ritis.RITISSpeedData;
import com.leidos.ode.agent.sanitizer.ODESanitizer;
import org.ritis.schema.tmdd_0_0_0.ZoneDataCollectionPeriodRITIS;
import org.ritis.schema.tmdd_0_0_0.ZoneDataRITIS;
import org.ritis.schema.tmdd_0_0_0.ZoneDetectorDataRITIS;
import org.ritis.schema.tmdd_0_0_0.ZoneReportRITIS;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cassadyja
 */
public class RITISSpeedEmptySanitizer implements ODESanitizer {

    public ODEAgentMessage sanitizeMessage(ODEAgentMessage message) throws ODESanitizeException {
        RITISSpeedData ritisSpeedData = (RITISSpeedData) message.getFormattedMessage();
        List<ZoneDataRITIS> zeroVehicleCountZoneDataItems = new ArrayList<ZoneDataRITIS>();
        ZoneDetectorDataRITIS zoneDetectorDataRITIS = ritisSpeedData.getZoneDetectorDataRITIS();
        List<ZoneDataCollectionPeriodRITIS> zoneDataCollectionPeriodRITISList = zoneDetectorDataRITIS.getCollectionPeriod().getCollectionPeriodItem();
        for (ZoneDataCollectionPeriodRITIS zoneDataCollectionPeriodRITIS : zoneDataCollectionPeriodRITISList) {
            List<ZoneReportRITIS> zoneReportRITISList = zoneDataCollectionPeriodRITIS.getZoneReports().getZoneReport();
            for (ZoneReportRITIS zoneReportRITIS : zoneReportRITISList) {
                List<ZoneDataRITIS> zoneDataRITISList = zoneReportRITIS.getZoneData().getZoneDataItem();
                for (ZoneDataRITIS zoneDataRITIS : zoneDataRITISList) {
                    long zoneVehicleCount = zoneDataRITIS.getZoneVehicleCount();
                    if (zoneVehicleCount == 0) {
                        zeroVehicleCountZoneDataItems.add(zoneDataRITIS);
                    }
                }
                //Remove all ZoneDataRITIS with a vehicle count of zero
                zoneDataRITISList.removeAll(zeroVehicleCountZoneDataItems);
            }
        }
        return message;
    }

}
