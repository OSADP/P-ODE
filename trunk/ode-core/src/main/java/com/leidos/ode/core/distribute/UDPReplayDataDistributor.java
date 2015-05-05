package com.leidos.ode.core.distribute;

import com.leidos.ode.data.ConnectionPoint;
import com.leidos.ode.data.PodeSubscriptionRequest;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.util.ByteUtils;
import org.dot.rdeapi.client.websocket.sockjs.PodeQueryResult;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * UDP Variant of ReplayDataDistributor
 */
public class UDPReplayDataDistributor extends ReplayDataDistributor {
    private DatagramSocket sock;
    public UDPReplayDataDistributor(PodeSubscriptionRequest subRequest, ServiceRequest srvRequest) {
        super(subRequest, srvRequest);

        try {
            sock = new DatagramSocket();
        } catch (SocketException e) {
            log.error("Unable to open UDP distributor socket.");
        }
    }

    @Override
    public void sendMessage(PodeQueryResult message) {

        // Commented out entire method to make identical to TCPReplayDistributor

        /*
        log.debug("ReplayDataDistributor " + subscriptionId + " sending message with timestamp " + message.getDateTime() + ".");
        // Decode the message data
        byte[] decodedHex = DatatypeConverter.parseHexBinary(message.getData());
        DatagramPacket p = new DatagramPacket(decodedHex, decodedHex.length);

        // Parse the target information out of the requests
        ConnectionPoint target = srvRequest.getDestination();
        String targetIp = ByteUtils.buildIpAddressFromBytes(target.getAddress().getIpv4Address().getValue());
        int targetPort = target.getPort().getValue();

        try {
            // Set the target information
            p.setAddress(InetAddress.getByName(targetIp));
            p.setPort(targetPort);

            sock.send(p);
        } catch (IOException e) {
            log.error("Unable to transmit replay packet correctly.");
        }
        */
    }
}
