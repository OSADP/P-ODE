/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.registration;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author cassadyja
 */
@XmlRootElement
public class RegistrationMessage {
    private byte[] encodedRegistrationMessage;

    public RegistrationMessage(){
        
    }
    
    public RegistrationMessage(byte[] encodedMessage){
        this.encodedRegistrationMessage = encodedMessage;
    }
    
    /**
     * @return the encodedRegistrationMessage
     */
    public byte[] getEncodedRegistrationMessage() {
        return encodedRegistrationMessage;
    }

    /**
     * @param encodedRegistrationMessage the encodedRegistrationMessage to set
     */
    public void setEncodedRegistrationMessage(byte[] encodedRegistrationMessage) {
        this.encodedRegistrationMessage = encodedRegistrationMessage;
    }
    
    
}
