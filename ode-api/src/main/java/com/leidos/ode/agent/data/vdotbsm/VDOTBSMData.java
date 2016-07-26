/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.agent.data.vdotbsm;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author cassadyja
 */
@XmlRootElement
public class VDOTBSMData {
    
    
  private int rseID;
  private String tempVehicleID;
  private long timestamp;
  private double latitude;
  private double longitude;
  private double speed;
  private double heading;
  private double elevation;
  private int brake;

    public int getRseID() {
        return rseID;
    }

    public void setRseID(int rseID) {
        this.rseID = rseID;
    }

    public String getTempVehicleID() {
        return tempVehicleID;
    }

    public void setTempVehicleID(String tempVehicleID) {
        this.tempVehicleID = tempVehicleID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public int getBrake() {
        return brake;
    }

    public void setBrake(int brake) {
        this.brake = brake;
    }
  
  
  
  
}
