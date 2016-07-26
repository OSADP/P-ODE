package com.leidos.ode.agent.data.ritis;

import org.ritis.schema.tmdd_0_0_0.ZoneDetectorDataRITIS;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/8/14
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
public class RITISSpeedData implements Serializable {

    private ZoneDetectorDataRITIS zoneDetectorDataRITIS;

    public ZoneDetectorDataRITIS getZoneDetectorDataRITIS() {
        return zoneDetectorDataRITIS;
    }

    public void setZoneDetectorDataRITIS(ZoneDetectorDataRITIS zoneDetectorDataRITIS) {
        this.zoneDetectorDataRITIS = zoneDetectorDataRITIS;
    }
}
