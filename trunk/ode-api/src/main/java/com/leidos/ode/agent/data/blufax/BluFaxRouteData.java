package com.leidos.ode.agent.data.blufax;

import org.tmdd._3.messages.RouteStatusMsg;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/10/14
 * Time: 12:18 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
public class BluFaxRouteData {

    private RouteStatusMsg routeStatusMsg;

    public RouteStatusMsg getRouteStatusMsg() {
        return routeStatusMsg;
    }

    public void setRouteStatusMsg(RouteStatusMsg routeStatusMsg) {
        this.routeStatusMsg = routeStatusMsg;
    }
}
