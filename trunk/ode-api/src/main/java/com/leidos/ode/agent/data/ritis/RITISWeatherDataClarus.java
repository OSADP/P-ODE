package com.leidos.ode.agent.data.ritis;

import edu.umd.cattlab.schema.ritisFilter.other.ClarusData;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/10/14
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
public class RITISWeatherDataClarus implements Serializable{

    private ClarusData clarusData;

    public ClarusData getClarusData() {
        return clarusData;
    }

    public void setClarusData(ClarusData clarusData) {
        this.clarusData = clarusData;
    }
}
