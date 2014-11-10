package com.leidos.ode.agent.data.vdot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/8/14
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
//TODO Not sure at this point what the data looks like since the weather urls contain no data
@XmlRootElement
public class VDOTWeatherData implements Serializable{

    private List<VDOTWeatherDataElement> vdotWeatherDataElements;

    public VDOTWeatherData() {
        vdotWeatherDataElements = new ArrayList<VDOTWeatherDataElement>();
    }

    public List<VDOTWeatherDataElement> getVdotWeatherDataElements() {
        return vdotWeatherDataElements;
    }

    public void setVdotWeatherDataElements(List<VDOTWeatherDataElement> vdotWeatherDataElements) {
        this.vdotWeatherDataElements = vdotWeatherDataElements;
    }

    public static class VDOTWeatherDataElement {

    }
}
