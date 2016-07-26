package gov.dot.fhwa.saxton.ode.rdeupload;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Listens for incoming data from the ODE and puts it on the upload queue.
 */
public class ODEDataListener implements Runnable {
    @Autowired
    private RDEDataUploader uploader;

    @Value("${ode.rdeuploader.listenport}")
    private int listenPort;
    private AtomicBoolean running = new AtomicBoolean(false);

    private Logger log = LogManager.getLogger(ODEDataListener.class);

    // Constants
    private static final int MAX_PACKET_SIZE = 8193;
    private static final int RX_TIMEOUT = 5000;

    @PostConstruct
    public void start() {
        new Thread(this, "ODEDataListenerThread").start();
    }

    public void run() {
        log.info("Beginning ODE data listener thread on port " + listenPort + "...");

        running.set(true);
        while (running.get()) {

            try (DatagramSocket sock = new DatagramSocket(listenPort)) {
                log.info("Listener socket opened, initializing buffers.");
                sock.setSoTimeout(RX_TIMEOUT);


                while (!sock.isClosed()) {
                    log.info("Listening for packet...");

                    byte[] buffer = new byte[MAX_PACKET_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, MAX_PACKET_SIZE);

                    try {
                        sock.receive(packet);
                        if (packet.getLength() > 0) {
                            log.info("Received packet of length " + packet.getLength());

                            if (packet.getLength() == MAX_PACKET_SIZE) {
                                log.error("PACKET TOO LARGE TO NOT TRUNCATE! PACKET WILL BE IGNORED!");
                                continue;
                            }

                            String s = new String(packet.getData()).substring(0, packet.getLength());

                            log.info("Data: " + s);
                            uploader.enqueueUpload(s);
                        }
                    } catch (SocketTimeoutException e) {
                        log.warn("No message received, listening again.");
                    }
                }
            } catch (SocketException e) {
                log.warn("Encountered socket exception, will retry.", e);
            } catch (IOException e) {
                log.warn("Encountered IO exception, will retry.", e);
            }
        }
    }

}
