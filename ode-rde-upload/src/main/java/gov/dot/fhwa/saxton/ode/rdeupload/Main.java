package gov.dot.fhwa.saxton.ode.rdeupload;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dot.rdeapi.client.websocket.sockjs.ClientWebSocketHandler;
import org.dot.rdeapi.client.websocket.sockjs.RDESockJsClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.socket.sockjs.client.SockJsClient;

/**
 * Main entry point for RDE upload functionality
 */
@PropertySource("rdeupload.properties")
@Import(RDESockJsClient.class)
public class Main {

    private static Logger log = LogManager.getLogger(Main.class);

    @Bean
    public ODEDataListener odeDataListener() {
        return new ODEDataListener();
    }

    @Bean
    public RDEDataUploader rdeDataUploader(SockJsClient sockJsClient, ClientWebSocketHandler clientWebSocketHandler) {
        return new RDEDataUploader(sockJsClient, clientWebSocketHandler);
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Main.class);
    }
}
