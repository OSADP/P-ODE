package com.leidos.ode.util;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 *
 * @author cassadyja
 */
public class SHAHasher {

    public static String sha256Hash(byte[] input){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(input);
            byte[] digest = md.digest();
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < digest.length; i++) {
                String hex = Integer.toHexString(0xff & digest[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }
    
}
