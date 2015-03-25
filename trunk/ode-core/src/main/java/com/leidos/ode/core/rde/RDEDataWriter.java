package com.leidos.ode.core.rde;

import org.apache.log4j.Logger;
import org.dot.rdelive.api.Datum;
import org.dot.rdelive.api.RunnableDataWriter;
import org.dot.rdelive.client.out.RDEClientUploadDirector;
import org.dot.rdelive.client.out.SampleRDEClientSocketWriter;
import org.dot.rdelive.client.out.WriterTask;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.Timer;
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
    private final String TAG = getClass().getSimpleName();
    private final Logger logger = Logger.getLogger(TAG);

    public RDEDataWriter(RDEConfig config) {
        writerQueue = new ArrayBlockingQueue<Datum<char[]>>(100);
        context = new RDEClientContext(writerQueue, config);
        writer = new SampleRDEClientSocketWriter(writerQueue, context, null, 3, 1000);
        director = new RDEClientUploadDirector(writer, null);

        // Check if the configured cert location exists, use it if we can, otherwise look inward.
        if (config.getLoadCert() && !setSslSystemProps(config)) {
            logger.warn("No external SSL certificate found at " + config.getTrustStore() + " attempting to load internal"
                        + " certificate...");

            // Try to load the RDE SSL cert from inside the JAR
            try {
                char[] password = config.getTrustStorePassword().toCharArray();
                InputStream keyStream = getClass().getResourceAsStream("rdeapi.p12");

                // Initialize the key stores
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(keyStream, password);
                KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyFactory.init(keyStore, password);
                KeyManager[] keyManagers = keyFactory.getKeyManagers();

                // Initialize the trust stores
                KeyStore trustStore = KeyStore.getInstance("PKCS12");
                trustStore.load(keyStream, password);
                TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustFactory.init(trustStore);
                TrustManager[] trustManagers = trustFactory.getTrustManagers();

                // Initialize an SSL context with our additional trust sources and set that to the default context
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(keyManagers, trustManagers, null);
                SSLContext.setDefault(sslContext);
                SSLContext.getDefault();
                logger.info("Sucessfully initialized default SSL context with RDE trust store.");
            } catch (CertificateException e) {
                logger.error("Unable to load SSL certificate for RDE.", e);
            } catch (UnrecoverableKeyException e) {
                logger.error("Unable to load SSL certificate for RDE.", e);
            } catch (NoSuchAlgorithmException e) {
                logger.error("Unable to load SSL certificate for RDE.", e);
            } catch (KeyStoreException e) {
                logger.error("Unable to load SSL certificate for RDE.", e);
            } catch (KeyManagementException e) {
                logger.error("Unable to load SSL certificate for RDE.", e);
            } catch (IOException e) {
                logger.error("Unable to load SSL certificate for RDE.", e);
            }
        }

        // Start up the RDE connection
        director.initialize();
        directorThread = new Thread(director, "RDEDataWriterThread");
        directorThread.start();
    }

    /**
     * Add new data onto the writer queue for the RDE.
     * @param data The datum object to be sent
     * @throws InterruptedException If the writer queue is at maximum capacity and this thread is interrupted while
     * waiting for it to become available.
     */
    public void send(Datum<char[]> data) throws InterruptedException {
        writerQueue.put(data);
    }

    /**
     * Get the job queue for this RDEDataWriter. This queue is thread safe.
     * @return The BlockingQueue that functions as the job queue for this thread.
     */
    public BlockingQueue<Datum<char[]>> getWriterQueue() {
        return writerQueue;
    }

    /**
     * Destroy this object and clean up threads and resources associated with it.
     */
    public void destroy() {
        director.close();
        writer.close();
    }

    /**
     * Attempts to set the SSL system properties needed to load the RDE certificate into the trust managers.
     * @param config The {@link RDEConfig} object containing the configuration data for this RDEDataWriter
     * @return True if successful at setting the SSL system properties, false o.w.
     */
    private boolean setSslSystemProps(RDEConfig config) {
        // Initialize the SSL certs needed for the RDE API
        File trustStore = new File(config.getTrustStore());
        if (!trustStore.exists()) {
            return false;
        }

        Properties props = System.getProperties();
        props.setProperty("javax.net.ssl.trustStoreType", config.getTrustStoreType());
        props.setProperty("javax.net.ssl.trustStore", config.getTrustStore());
        props.setProperty("javax.net.ssl.trustStorePassword", config.getPassword());
        System.setProperties(props);

        // TODO: Remove this log statement, it leaks the password.
        logger.info(String.format("Set trust store to %s, type %s, password %s",
                    config.getTrustStore(),
                    config.getTrustStoreType(),
                    config.getTrustStorePassword()));


        return true;
    }

    public static void main(String[] args) {
        System.out.println("Running in standalone test mode...");
        RDEConfig config = new RDEConfig("rdeconfig");
        RDEDataWriter writer = new RDEDataWriter(config);

        System.out.println("Instantiated writer.");

        WriterTask task = new WriterTask(writer.getWriterQueue());
        new Timer().schedule(task, 5000, 50000);
        System.out.println("Instantiated timer task.");
    }

}
