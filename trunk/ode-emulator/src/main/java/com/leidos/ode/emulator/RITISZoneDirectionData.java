/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.emulator;

/**
 *
 * @author cassadyja
 */
public class RITISZoneDirectionData {
    
    private int[] eastZones = new int[]{910,912,913,914,1168,1365,1371,1392};
    private int[] westZones = new int[]{911,917,935,938,939,1175,1372,1399};
    
   

    public int[] getZonesForDirection(String direction){
        if("W".equalsIgnoreCase(direction)){
            return westZones;
        }else if("E".equalsIgnoreCase(direction)){
            return eastZones;
        }
        return new int[0];
    }
    
    public int[] getZonesForOppositeDirection(String direction){
        if("W".equalsIgnoreCase(direction)){
            return eastZones;
        }else if("E".equalsIgnoreCase(direction)){
            return westZones;
        }
        return new int[0];
    }
    
    public int isInDirectionOfTravelRITIS(int zone, String direction){
        int ret = 0;
        int[] directionZones = getZonesForDirection(direction);
        int[] oppDirZones = getZonesForOppositeDirection(direction);
        for(int i=0;i<directionZones.length;i++){
            if(directionZones[i] == zone){
                return 1;
            }
        }
        for(int i=0;i<oppDirZones.length;i++){
            if(oppDirZones[i] == zone){
                return -1;
            }
        }
        
        
        return ret;
    }





 
    
}
