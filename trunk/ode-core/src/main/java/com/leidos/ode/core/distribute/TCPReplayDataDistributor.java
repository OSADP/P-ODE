package com.leidos.ode.core.distribute;

import com.leidos.ode.data.ConnectionPoint;
import com.leidos.ode.data.PodeSubscriptionRequest;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.util.ByteUtils;
import org.dot.rdeapi.client.websocket.sockjs.PodeQueryResult;

import javax.xml.bind.DatatypeConverter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * TCP Variant of ReplayDataDistributor.
 */
public class TCPReplayDataDistributor extends ReplayDataDistributor {
    private Socket sock;

    public TCPReplayDataDistributor(PodeSubscriptionRequest subRequest, ServiceRequest srvRequest) {
        super(subRequest, srvRequest);
    }

    @Override
    public void sendMessage(PodeQueryResult message) {
        log.debug("ReplayDataDistributor " + subscriptionId + " sending message with timestamp " + message.getDate() + ".");

        try {
            // Connect the socket
            ConnectionPoint target = srvRequest.getDestination();
            String targetIp = ByteUtils.buildIpAddressFromBytes(target.getAddress().getIpv4Address().getValue());
            int targetPort = target.getPort().getValue();
            sock = new Socket(targetIp, targetPort);
            DataOutputStream os = new DataOutputStream(sock.getOutputStream());

            // Write the data
            byte[] bytes = DatatypeConverter.parseHexBinary(message.getValue());
            log.debug("Writing TCP replay packet length.");
            os.write(bytes.length);
            os.flush();
            log.debug("Writing TCP replay packet data.");
            sock.getOutputStream().write(bytes);
            os.flush();

            // Clean up the TCP connection
            sock.close();
        } catch (IOException e) {
            log.error("Unable to transmit replay packet correctly.");
        }
    }

    @Override
    public void cleanupConnection() {
        // Nothing needed
    }
}
