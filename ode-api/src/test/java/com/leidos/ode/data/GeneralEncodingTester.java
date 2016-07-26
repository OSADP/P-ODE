/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.data;

import org.bn.CoderFactory;
import org.bn.IEncoder;
import org.bn.types.BitString;

import java.io.ByteArrayOutputStream;


/**
 *
 * @author cassadyja
 */
public class GeneralEncodingTester {
    
    public static void main(String[] args) throws Exception{
        PodeSubData subData = new PodeSubData();
        PodeDataTypes types = new PodeDataTypes(new BitString(new byte[]{20}));
        subData.setDataElements(types);
        
        IEncoder encoder = CoderFactory.getInstance().newEncoder("BER");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encoder.encode(subData, baos);
        
        byte[] bytes = baos.toByteArray();
        for (int i = 0; i < bytes.length; i++) {
            System.out.printf("%02X ", bytes[i]);
        }        
                
        
    }
    
}
