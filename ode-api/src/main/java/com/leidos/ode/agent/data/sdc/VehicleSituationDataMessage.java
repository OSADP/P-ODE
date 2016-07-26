package com.leidos.ode.agent.data.sdc;

import java.util.List;

/**
 * Data class representing a delivery of Vehicle Situation data from the SDC
 *
 * Created based on ASN.1 specification contained in SEMI_v2.2.0_011315.asn
 */
public class VehicleSituationDataMessage {
    private int dialogID;
    private int seqID;
    private int groupID;
    private int requestID;
    private byte type;
    private List<VehicleSituationRecord> bundle;
    private long crc;

    public int getDialogID() {
        return dialogID;
    }

    public void setDialogID(int dialogID) {
        this.dialogID = dialogID;
    }

    public int getSeqID() {
        return seqID;
    }

    public void setSeqID(int seqID) {
        this.seqID = seqID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public List<VehicleSituationRecord> getBundle() {
        return bundle;
    }

    public void setBundle(List<VehicleSituationRecord> bundle) {
        this.bundle = bundle;
    }

    public long getCrc() {
        return crc;
    }

    public void setCrc(long crc) {
        this.crc = crc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleSituationDataMessage that = (VehicleSituationDataMessage) o;

        if (dialogID != that.dialogID) return false;
        if (seqID != that.seqID) return false;
        if (groupID != that.groupID) return false;
        if (requestID != that.requestID) return false;
        if (type != that.type) return false;
        if (crc != that.crc) return false;
        return !(bundle != null ? !bundle.equals(that.bundle) : that.bundle != null);

    }

    @Override
    public int hashCode() {
        int result = dialogID;
        result = 31 * result + seqID;
        result = 31 * result + groupID;
        result = 31 * result + requestID;
        result = 31 * result + (int) type;
        result = 31 * result + (bundle != null ? bundle.hashCode() : 0);
        result = 31 * result + (int) (crc ^ (crc >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "VehicleSituationDataMessage{" +
                "dialogID=" + dialogID +
                ", seqID=" + seqID +
                ", groupID=" + groupID +
                ", requestID=" + requestID +
                ", type=" + type +
                ", bundle=" + bundle +
                ", crc=" + crc +
                '}';
    }
}
