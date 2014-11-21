package com.leidos.ode.core.rde.request.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.leidos.ode.core.rde.request.BasicRDERequest;
import com.leidos.ode.core.rde.request.RDERequest;
import com.leidos.ode.core.rde.request.model.RDEData;
import com.leidos.ode.core.rde.request.model.RDEStoreData;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 11/21/14
 * Time: 1:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class RDEStoreRequest extends BasicRDERequest implements RDERequest {

    private String type;
    private String date;
    private byte[] data;
    private JsonFactory jsonFactory;

    protected RDEStoreRequest(RDEData rdeData) {
        super(rdeData);
        this.type = ((RDEStoreData) rdeData).getType();
        this.date = ((RDEStoreData) rdeData).getDate();
        this.data = ((RDEStoreData) rdeData).getData();
        jsonFactory = new JsonFactory();
    }

    @Override
    public Object request() {
//        Datum<char[]> data = new GenericDatum<char[]>();
        Writer writer = new StringWriter();
        try {
            JsonGenerator generator = jsonFactory.createGenerator(writer);
            generator.writeStartObject();
            generator.writeObjectFieldStart(type);
            generator.writeStringField("date", date);
            generator.writeFieldName("value");
            generator.writeBinary(data);
            generator.writeEndObject();
            generator.close();
            writer.close();
//            data.setData(writer.toString().toCharArray());
//            data.setDataType(DataType.CHARACTER);
//            data.setEncoding(CharsetType.UTF8);
//            return data;
        } catch (IOException e) {
            getLogger().error(e.getLocalizedMessage());
        }
        return null;
    }
}
