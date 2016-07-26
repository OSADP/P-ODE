/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.data.ritis;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import org.ritis.schema.atis_3_0_76.AdvisoryInformation;

/**
 *
 * @author cassadyja
 */
@XmlRootElement
public class RITISIncidentData implements Serializable{
    private AdvisoryInformation advisoryInformation;

    public AdvisoryInformation getAdvisoryInformation() {
        return advisoryInformation;
    }

    public void setAdvisoryInformation(AdvisoryInformation advisoryInformation) {
        this.advisoryInformation = advisoryInformation;
    }
    
    
}
