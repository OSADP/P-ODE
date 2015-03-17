package com.leidos.ode.core.rde;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.leidos.ode.core.distribute.DataDistributor;
import com.leidos.ode.core.rde.RDEClientContext;
import com.leidos.ode.core.rde.RDEDataWriter;
import com.leidos.ode.core.rde.RDETestConfig;
import com.leidos.ode.core.rde.factory.RDERequestFactory;
import com.leidos.ode.core.rde.request.RDERequest;
import com.leidos.ode.core.rde.request.impl.RDEStoreRequest;
import com.leidos.ode.core.rde.request.model.RDEData;
import com.leidos.ode.core.rde.request.model.RDEStoreData;
import com.leidos.ode.core.rde.response.impl.RDEStoreResponse;
import org.dot.rdelive.api.Datum;
import org.dot.rdelive.api.config.CharsetType;
import org.dot.rdelive.api.config.DataType;
import org.dot.rdelive.client.out.RDEClientUploadDirector;
import org.dot.rdelive.client.out.SampleRDEClientSocketWriter;
import org.dot.rdelive.impl.GenericDatum;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

/**
 * Class representing the Store Data controller. Responsible for sending published data received by the ODE to the RDE,
 * and recording that transfer to the Pub/Sub registration. Contains an RDE Interface sub-component, which is responsible
 * for formatting the data feed to the RDE as well as the metadata document that will accompany the data feed. Once data
 * is transferred to the RDE, Store Data will use the Pub/Sub registration interface sub-component to the log the data
 * transfer so it can be discovered later for playback.
 *
 * @author lamde
 */

public class RDEDistributor extends DataDistributor {

    private final String TAG = getClass().getSimpleName();
    private RDEDataWriter writer;
    private Topic topic;
    
    public RDEDistributor(){
        
    }


    /*@Override
    @RequestMapping(value = "rdeStore", method = RequestMethod.POST)
    public
    @ResponseBody
    RDEStoreResponse store(@RequestBody RDEData rdeData) throws RDEStoreException {

        
        RDERequest rawData = RDERequestFactory.storeRequest(rdeData);
        RDEStoreRequest rdeRequest = null;

        if (rawData != null) {
            // If it's not null it is an instance of RDEStoreRequest
            rdeRequest = (RDEStoreRequest) rawData;
        }

        // Generate the datum object
        GenericDatum<char[]> datum = new GenericDatum<char[]>();
        if (rdeRequest != null) {
            String json = ((String) (rdeRequest.request()));
            System.out.println(json);
            datum.setData(json.toCharArray());
            datum.setDataType(DataType.CHARACTER);
            datum.setEncoding(CharsetType.UTF8);

            try {
                queue.put(datum);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

         return new RDEStoreResponse();
    }*/

    // For testing purposes
    /*public static void main(String[] args) {
        // Initialize the store controller
        ArrayBlockingQueue<GenericDatum<char[]>> writerQueue = new ArrayBlockingQueue<GenericDatum<char[]>>(100);
        RDETestConfig config = new RDETestConfig();
        RDEClientContext context = new RDEClientContext(writerQueue, config);
        SampleRDEClientSocketWriter writer = new SampleRDEClientSocketWriter(writerQueue, context, null, 3, 1000);
        RDEClientUploadDirector director = new RDEClientUploadDirector(writer, null);
        RDEStoreController store = new RDEStoreControllerImpl(context, director, writerQueue);

        System.out.println("Proceeding with config: " + config);

        // Initialize the RDE data to store
        byte[] test = "HitQAQJRGgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPQ==".getBytes();
        String header = "BSM";
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd hh:mm:ss a");

        while (true) {
            // Send the data to the RDE
            String date = formatter.format(new Date());
            System.out.println("Sending: " + header + " " + test +  " " + date);
            try {
                RDEData data = new RDEStoreData(header, test, date);
                store.store(data);
            } catch (RDEStoreController.RDEStoreException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/

    @Override
    protected void cleanup() {

    }

    @Override
    protected void connectTarget() throws DistributeException {

    }

    @Override
    protected void sendData(Message message) throws DistributeException {
        // Generate the datum object
        GenericDatum<char[]> datum = new GenericDatum<char[]>();

        // Open the generators and writers
        JsonFactory jsonFactory = new JsonFactory();
        Writer stringWriter = new StringWriter();

        try {
            // Start writing the JSON string
            JsonGenerator generator = jsonFactory.createGenerator(stringWriter);
            generator.writeStartObject();

            // Write the topic/type and timestamp fields
            generator.writeObjectFieldStart(getRdeType(topic.getTopicName()));
            generator.writeStringField("date", getRdeTimestamp(message.getJMSTimestamp()));

            // Get the bytes of our message and write that to the JSON as a char[]
            BytesMessage msg = (BytesMessage) message;
            byte[] encoded = new byte[(int) msg.getBodyLength()];
            msg.readBytes(encoded);
            generator.writeFieldName("value");
            generator.writeBinary(encoded);

            // Close it all out
            generator.writeEndObject();
            generator.close();
            stringWriter.close();
        } catch (IOException e) {
            throw new DistributeException("Unable to create create RDE Datum JSON structure.");
        } catch (JMSException e) {
            throw new DistributeException("Unable to parse data from JMS Message for RDE.");
        }

        // Copy the above generated values into the Datum
        datum.setData(stringWriter.toString().toCharArray());
        datum.setDataType(DataType.CHARACTER);
        datum.setEncoding(CharsetType.UTF8);

        // Send it off to the RDE
        try {
            writer.send(datum);
        } catch (InterruptedException e) {
            throw new DistributeException("Interrupted while waiting on writer queue.");
        }
    }

    /**
     * Converts a topic.toString value into the appropriate String type to send to the RDE
     * @param topic a String representing the topic this RDEStoreController is subscribed to
     * @return A String representing the RDE type associated with that topic
     */
    private String getRdeType(String topic) {
        return topic;
    }

    /**
     * Converts timestamp formats for usage by the RDE
     * @param timestamp A long timestamp as used by JMS
     * @return A string encoding of that timestamp for use by the RDE
     */
    private String getRdeTimestamp(long timestamp) {
        return "" + timestamp;
    }
}
