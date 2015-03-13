package com.leidos.ode.core.rde.controllers;

import com.leidos.ode.core.rde.RDEClientContext;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Class representing the Store Data controller. Responsible for sending published data received by the ODE to the RDE,
 * and recording that transfer to the Pub/Sub registration. Contains an RDE Interface sub-component, which is responsible
 * for formatting the data feed to the RDE as well as the metadata document that will accompany the data feed. Once data
 * is transferred to the RDE, Store Data will use the Pub/Sub registration interface sub-component to the log the data
 * transfer so it can be discovered later for playback.
 *
 * @author lamde
 */
@Controller
public class RDEStoreControllerImpl implements RDEStoreController {

    private final String TAG = getClass().getSimpleName();
    @Autowired
    @Qualifier("clientUploadContext")
    private RDEClientContext context;
    @Autowired
    @Qualifier("uploadDirector")
    private RDEClientUploadDirector director;
    @Autowired
    @Qualifier("writerQueueDatum")
    private BlockingQueue<Datum<char[]>> queue;
    private Thread directorThread;

    private boolean started = false;
    
    public RDEStoreControllerImpl(){
        
    }
    
    public RDEStoreControllerImpl(RDEClientContext context, RDEClientUploadDirector director,
                                  BlockingQueue<Datum<char[]>> queue) {
        this.context = context;
        this.director = director;
        this.queue = queue;

        // Initialize the RDE data writing mechanism
        if (!this.director.isAvailable()) {
            this.director.initialize();
            
        }

        if (!director.isActive()) {
            directorThread = new Thread(this.director, "RDEWriterThread");
            directorThread.start();
        }
    }

    @Override
    @RequestMapping(value = "rdeStore", method = RequestMethod.POST)
    public
    @ResponseBody
    RDEStoreResponse store(@RequestBody RDEData rdeData) throws RDEStoreException {
        if(!started){
            // Initialize the RDE data writing mechanism
            if (!this.director.isAvailable()) {
                this.director.initialize();

            }

            if (!director.isActive()) {
                directorThread = new Thread(this.director, "RDEWriterThread");
                directorThread.start();
            }      
            started = true;
        }
        
        RDERequest rawData = RDERequestFactory.storeRequest(rdeData);
        RDEStoreRequest rdeRequest = null;

        if (rawData != null) {
            // If it's not null it is an instance of RDEStoreRequest
            rdeRequest = (RDEStoreRequest) rawData;
        }

        // Generate the datum object
        Datum<char[]> datum = new GenericDatum<char[]>();
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
    }

    // For testing purposes
    public static void main(String[] args) {
        // Initialize the store controller
        ArrayBlockingQueue<Datum<char[]>> writerQueue = new ArrayBlockingQueue<Datum<char[]>>(100);
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
    }
}
