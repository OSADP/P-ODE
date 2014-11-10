package com.leidos.ode.agent.data.ritis;

import edu.umd.cattlab.schema.ritisFilter.other.AlertsData;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/8/14
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
public class RITISWeatherDataNWS implements Serializable{

    private AlertsData alertsData;

    public AlertsData getAlertsData() {
        return alertsData;
    }

    public void setAlertsData(AlertsData alertsData) {
        this.alertsData = alertsData;
    }
}
