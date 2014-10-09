package com.leidos.ode.agent.data.ritis;

import generated.Alerts;
import generated.Header;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/8/14
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class RITISWeatherData implements RITISData {

    private Header header;
    private Alerts alerts;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Alerts getAlerts() {
        return alerts;
    }

    public void setAlerts(Alerts alerts) {
        this.alerts = alerts;
    }
}
