/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.registration;


import com.leidos.ode.data.DataSubscriptionResponse;
import com.leidos.ode.data.PodeDataConfirmation;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author cassadyja
 */
@XmlRootElement
public class RegistrationResponse {
    private DataSubscriptionResponse subResponse;
    private PodeDataConfirmation pubResponse;

    /**
     * @return the subResponse
     */
    public DataSubscriptionResponse getSubResponse() {
        return subResponse;
    }

    /**
     * @param subResponse the subResponse to set
     */
    public void setSubResponse(DataSubscriptionResponse subResponse) {
        this.subResponse = subResponse;
    }

    /**
     * @return the pubResponse
     */
    public PodeDataConfirmation getPubResponse() {
        return pubResponse;
    }

    /**
     * @param pubResponse the pubResponse to set
     */
    public void setPubResponse(PodeDataConfirmation pubResponse) {
        this.pubResponse = pubResponse;
    }
    
    
    
    
}
