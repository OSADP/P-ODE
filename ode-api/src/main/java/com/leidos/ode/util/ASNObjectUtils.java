/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.util;

import com.leidos.ode.data.DDateTime;
import com.leidos.ode.data.PodeSource;
import java.util.Date;

/**
 *
 * @author cassadyja
 */
public class ASNObjectUtils {
    
    public static String getSourceName(PodeSource.EnumType enumType){
        if(enumType.equals(PodeSource.EnumType.blufax)){
            return "blufax";
        }else if(enumType.equals(PodeSource.EnumType.dms)){
            return "dms";
        }else if(enumType.equals(PodeSource.EnumType.ritis)){
            return "ritis";
        }else if(enumType.equals(PodeSource.EnumType.rtms)){
            return "rtms";
        }else if(enumType.equals(PodeSource.EnumType.spat)){
            return "spat";
        }else if(enumType.equals(PodeSource.EnumType.vdot)){
            return "vdot";
        }else if(enumType.equals(PodeSource.EnumType.wxde)){
            return "wxde";
        }
        return "unknown";
    }    
    
    public static Date getDateForDDateTime(DDateTime ddt){
        String year = ddt.getYear().getValue()+"";
        
        
        return null;
    }
    
}
