package com.leidos.ode.agent.datatarget;

import com.leidos.ode.agent.data.ODEAgentMessage;
import com.leidos.ode.core.data.ODERegistrationResponse;
import org.apache.log4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.HttpURLConnection;

/**
 * @author cassadyja
 */
public class ODERestTarget implements ODEDataTarget {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private HttpURLConnection conn;
    private WebTarget webTarget;

    public void configure(ODERegistrationResponse regInfo) throws DataTargetException {
        String hostURL = regInfo.getQueueHostURL();
        int hostPort = regInfo.getQueueHostPort();
        String queueName = regInfo.getQueueName();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://").append(hostURL).append(":").append(hostPort).append("/").append(queueName);
        String address = stringBuilder.toString();
        logger.debug(TAG + "- Configuring ODERegistrationResponse with endpoint address: " + address);

        Client client = ClientBuilder.newClient();
        webTarget = client.target(UriBuilder.fromUri(address));


//        try {
//            URL url = new URL(hostURL);
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setDoOutput(true);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json");
//            
//        } catch (MalformedURLException ex) {
//            throw new DataTargetException("Error connecting to host", ex);
//        } catch (ProtocolException ex) {
//            throw new DataTargetException("Error connecting to host", ex);
//        } catch (IOException ex) {
//            throw new DataTargetException("Error connecting to host", ex);
//        }
    }

    public void sendMessage(ODEAgentMessage message) throws DataTargetException {
        Response response = webTarget.request().get();
        String responseString = response.getEntity().toString();
        System.out.println("Test " + responseString);
//        ClientResponse response = webTarget.path("http://localhost:9090/ode-web").path("publish")
//                                .type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
//                                    .post(ClientResponse.class, message);

//        OutputStream os = null;
//        try {
//            String input = "{\"qty\":100,\"name\":\"iPad 4\"}";
//            os = conn.getOutputStream();
//            os.write(input.getBytes());
//            os.flush();
//            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                        + conn.getResponseCode());
//            }   
//            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//        String output;
//            System.out.println("Output from Server .... \n");
//            while ((output = br.readLine()) != null) {
//                System.out.println(output);
//            }        
//        } catch (IOException ex) {
//            Logger.getLogger(ODERestTarget.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            try {
//                os.close();
//            } catch (IOException ex) {
//                Logger.getLogger(ODERestTarget.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }

    public void close() {

    }

}
