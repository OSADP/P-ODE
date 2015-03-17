package com.leidos.ode.core.rde;

import org.dot.rdelive.api.Datum;
import org.dot.rdelive.api.RunnableDataWriter;
import org.dot.rdelive.client.out.RDEClientUploadDirector;
import org.dot.rdelive.client.out.SampleRDEClientSocketWriter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * RDE Writer Wrapper
 * Some of the fields of this component are manually instantiated because of
 * conflicts in types between the Spring IOC Container and the RDE API jar.
 * Namely since BlockingQueue<Datum<char[]> and BlockingQueue<GenericDatum<char[]>>
 * are unrelated types I have to instantiate any objects that require the Datum
 * interface type manually.
 *
 * Each new instance of RDEDataWriter will spawn its own threads and network
 * connections to write to the RDE.
 */
public class RDEDataWriter {
    private RDEClientContext context;
    private RDEClientUploadDirector director;
    private RunnableDataWriter<Datum<char[]>, char[]> writer;
    private BlockingQueue<Datum<char[]>> writerQueue;
    private Thread directorThread;


    public RDEDataWriter(RDEConfig config) {
        writerQueue = new ArrayBlockingQueue<Datum<char[]>>(100);
        context = new RDEClientContext(writerQueue, config);
        writer = new SampleRDEClientSocketWriter(writerQueue, context, null, 3, 1000);
        director = new RDEClientUploadDirector(writer, null);

        // Start up the RDE connection
        director.initialize();
        directorThread = new Thread(director, "RDEDataWriterThread");
        directorThread.start();
    }

    public void send(Datum<char[]> data) throws InterruptedException {
        writerQueue.put(data);
    }

    public BlockingQueue<Datum<char[]>> getWriterQueue() {
        return writerQueue;
    }

    public void destroy() {
        director.close();
        writer.close();
    }

}
