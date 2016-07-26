package com.leidos.ode.agent.data.sdc;

/**
 * Created by rushk1 on 5/12/2016.
 */
public class FundamentalSituationalStatus {

    enum TransmissionState {
        NEUTRAL,
        PARK,
        FORWARD,
        REVERSE,
        RESERVED1,
        RESERVED2,
        RESERVED3,
        UNAVAILABLE
    }

    private TransmissionState transmission;
    private Double speed;
    private Double heading;
    private Double steeringWheelAngle;
    private Double latAcceleration;
    private Double lonAcceleration;
    private Double vertAcceleration;
    private Double yawRate;
    private Integer brakeSystemStatus;
    private Integer vehicleWidth;
    private Integer vehicleLength;
    private Byte vsmEventFlag;

    public TransmissionState getTransmission() {
        return transmission;
    }

    public void setTransmission(TransmissionState transmission) {
        this.transmission = transmission;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    public Double getSteeringWheelAngle() {
        return steeringWheelAngle;
    }

    public void setSteeringWheelAngle(Double steeringWheelAngle) {
        this.steeringWheelAngle = steeringWheelAngle;
    }

    public Double getLatAcceleration() {
        return latAcceleration;
    }

    public void setLatAcceleration(Double latAcceleration) {
        this.latAcceleration = latAcceleration;
    }

    public Double getLonAcceleration() {
        return lonAcceleration;
    }

    public void setLonAcceleration(Double lonAcceleration) {
        this.lonAcceleration = lonAcceleration;
    }

    public Double getVertAcceleration() {
        return vertAcceleration;
    }

    public void setVertAcceleration(Double vertAcceleration) {
        this.vertAcceleration = vertAcceleration;
    }

    public Double getYawRate() {
        return yawRate;
    }

    public void setYawRate(Double yawRate) {
        this.yawRate = yawRate;
    }

    public Integer getBrakeSystemStatus() {
        return brakeSystemStatus;
    }

    public void setBrakeSystemStatus(Integer brakeSystemStatus) {
        this.brakeSystemStatus = brakeSystemStatus;
    }

    public Integer getVehicleWidth() {
        return vehicleWidth;
    }

    public void setVehicleWidth(Integer vehicleWidth) {
        this.vehicleWidth = vehicleWidth;
    }

    public Integer getVehicleLength() {
        return vehicleLength;
    }

    public void setVehicleLength(Integer vehicleLength) {
        this.vehicleLength = vehicleLength;
    }

    public Byte getVsmEventFlag() {
        return vsmEventFlag;
    }

    public void setVsmEventFlag(Byte vsmEventFlag) {
        this.vsmEventFlag = vsmEventFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FundamentalSituationalStatus that = (FundamentalSituationalStatus) o;

        if (transmission != that.transmission) return false;
        if (speed != null ? !speed.equals(that.speed) : that.speed != null) return false;
        if (heading != null ? !heading.equals(that.heading) : that.heading != null) return false;
        if (steeringWheelAngle != null ? !steeringWheelAngle.equals(that.steeringWheelAngle) : that.steeringWheelAngle != null)
            return false;
        if (latAcceleration != null ? !latAcceleration.equals(that.latAcceleration) : that.latAcceleration != null)
            return false;
        if (lonAcceleration != null ? !lonAcceleration.equals(that.lonAcceleration) : that.lonAcceleration != null)
            return false;
        if (vertAcceleration != null ? !vertAcceleration.equals(that.vertAcceleration) : that.vertAcceleration != null)
            return false;
        if (yawRate != null ? !yawRate.equals(that.yawRate) : that.yawRate != null) return false;
        if (brakeSystemStatus != null ? !brakeSystemStatus.equals(that.brakeSystemStatus) : that.brakeSystemStatus != null)
            return false;
        if (vehicleWidth != null ? !vehicleWidth.equals(that.vehicleWidth) : that.vehicleWidth != null) return false;
        if (vehicleLength != null ? !vehicleLength.equals(that.vehicleLength) : that.vehicleLength != null)
            return false;
        return !(vsmEventFlag != null ? !vsmEventFlag.equals(that.vsmEventFlag) : that.vsmEventFlag != null);

    }

    @Override
    public int hashCode() {
        int result = transmission != null ? transmission.hashCode() : 0;
        result = 31 * result + (speed != null ? speed.hashCode() : 0);
        result = 31 * result + (heading != null ? heading.hashCode() : 0);
        result = 31 * result + (steeringWheelAngle != null ? steeringWheelAngle.hashCode() : 0);
        result = 31 * result + (latAcceleration != null ? latAcceleration.hashCode() : 0);
        result = 31 * result + (lonAcceleration != null ? lonAcceleration.hashCode() : 0);
        result = 31 * result + (vertAcceleration != null ? vertAcceleration.hashCode() : 0);
        result = 31 * result + (yawRate != null ? yawRate.hashCode() : 0);
        result = 31 * result + (brakeSystemStatus != null ? brakeSystemStatus.hashCode() : 0);
        result = 31 * result + (vehicleWidth != null ? vehicleWidth.hashCode() : 0);
        result = 31 * result + (vehicleLength != null ? vehicleLength.hashCode() : 0);
        result = 31 * result + (vsmEventFlag != null ? vsmEventFlag.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FundamentalSituationalStatus{" +
                "transmission=" + transmission +
                ", speed=" + speed +
                ", heading=" + heading +
                ", steeringWheelAngle=" + steeringWheelAngle +
                ", latAcceleration=" + latAcceleration +
                ", lonAcceleration=" + lonAcceleration +
                ", vertAcceleration=" + vertAcceleration +
                ", yawRate=" + yawRate +
                ", brakeSystemStatus=" + brakeSystemStatus +
                ", vehicleWidth=" + vehicleWidth +
                ", vehicleLength=" + vehicleLength +
                ", vsmEventFlag=" + vsmEventFlag +
                '}';
    }
}
