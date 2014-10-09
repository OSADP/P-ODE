package com.leidos.ode.agent.data;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/8/14
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class Geometry {

    private float coordLat;
    private float coordLong;

    public float getCoordLat() {
        return coordLat;
    }

    public void setCoordLat(float coordLat) {
        this.coordLat = coordLat;
    }

    public float getCoordLong() {
        return coordLong;
    }

    public void setCoordLong(float coordLong) {
        this.coordLong = coordLong;
    }

    public float[] getPosition() {
        return new float[]{getCoordLat(), getCoordLong()};
    }
}
