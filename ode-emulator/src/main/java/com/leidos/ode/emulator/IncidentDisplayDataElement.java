package com.leidos.ode.emulator;

/**
 * JSON Serializable object for incident data headed towards the display
 */
public class IncidentDisplayDataElement {
    private String source;
    private String dataType;
    private IncidentElement dataValue;

    public IncidentDisplayDataElement(String source, String dataType, IncidentElement dataValue) {
        this.source = source;
        this.dataType = dataType;
        this.dataValue = dataValue;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public IncidentElement getDataValue() {
        return dataValue;
    }

    public void setDataValue(IncidentElement dataValue) {
        this.dataValue = dataValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IncidentDisplayDataElement that = (IncidentDisplayDataElement) o;

        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        if (dataType != null ? !dataType.equals(that.dataType) : that.dataType != null) return false;
        return !(dataValue != null ? !dataValue.equals(that.dataValue) : that.dataValue != null);

    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (dataValue != null ? dataValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "IncidentDisplayDataElement{" +
                "source='" + source + '\'' +
                ", dataType='" + dataType + '\'' +
                ", dataValue=" + dataValue +
                '}';
    }
}
