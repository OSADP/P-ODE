package com.leidos.ode.agent.data.sdc;

import com.leidos.ode.data.DDateTime;
import com.leidos.ode.data.Position3D;

import java.util.Arrays;

/**
 * Data class representing one VSD record delivered by the SDC
 *
 * Created based on ASN.1 specification contained in SEMI_v2.2.0_011315.asn
 */
public class VehicleSituationRecord {
    private byte[] tempID;
    private DDateTime time;
    private Position3D pos;
    private FundamentalSituationalStatus fundamental;

    public byte[] getTempID() {
        return tempID;
    }

    public void setTempID(byte[] tempID) {
        this.tempID = tempID;
    }

    public DDateTime getTime() {
        return time;
    }

    public void setTime(DDateTime time) {
        this.time = time;
    }

    public Position3D getPos() {
        return pos;
    }

    public void setPos(Position3D pos) {
        this.pos = pos;
    }

    public FundamentalSituationalStatus getFundamental() {
        return fundamental;
    }

    public void setFundamental(FundamentalSituationalStatus fundamental) {
        this.fundamental = fundamental;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleSituationRecord that = (VehicleSituationRecord) o;

        if (!Arrays.equals(tempID, that.tempID)) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (pos != null ? !pos.equals(that.pos) : that.pos != null) return false;
        return !(fundamental != null ? !fundamental.equals(that.fundamental) : that.fundamental != null);

    }

    @Override
    public int hashCode() {
        int result = tempID != null ? Arrays.hashCode(tempID) : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (pos != null ? pos.hashCode() : 0);
        result = 31 * result + (fundamental != null ? fundamental.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VehicleSituationRecord{" +
                "tempID=" + Arrays.toString(tempID) +
                ", time=" + time +
                ", pos=" + pos +
                ", fundamental=" + fundamental +
                '}';
    }
}
