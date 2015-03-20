/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.util;

import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author cassadyja
 */
public class ByteUtils {
    
    public static String buildIpAddressFromBytes(byte[] bytes){
        String s = "";
        for(byte b:bytes){
            s+=b;
            s+=".";
        }
        s = s.substring(0, s.length()-1);
        return s;
    }
    
    public static String convertBytesToHex(byte[] bytes){
        String hexString = DatatypeConverter.printHexBinary(bytes);
        return  hexString;
    }
    
    
}
