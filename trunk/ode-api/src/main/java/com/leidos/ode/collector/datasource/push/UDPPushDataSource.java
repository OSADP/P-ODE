package com.leidos.ode.collector.datasource.push;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPPushDataSource extends PushDataSource {

    private DatagramSocket datagramSocket;

    @Override
    public void startDataSource() {
        try {
            InetAddress tmpAddress = InetAddress.getByName(getHostAddress());
            getLogger().info("TMP Host Address: " + tmpAddress.getHostAddress());
            byte[] addressBytes = tmpAddress.getAddress();
            InetAddress address = Inet6Address.getByAddress(getHostAddress(), addressBytes);
            getLogger().info("Host Address: " + address.getHostAddress());
            getLogger().info("Host Address: " + address.getCanonicalHostName());

            getLogger().info("Connecting datagram socket on port: " + getHostPort());
            datagramSocket = new DatagramSocket(getHostPort(), address);
            datagramSocket.setSoTimeout(10000);
            executeDataSourceThread();
        } catch (UnknownHostException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (SocketException e) {
            getLogger().error(e.getLocalizedMessage());
        }
    }

    @Override
    public byte[] pollDataSource() {
        try {
            byte[] receiveData = new byte[75000];
            DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
            datagramSocket.receive(packet);
            if (packet.getLength() > 0) {
                getLogger().debug(packet.getLength());
                byte[] sourceAddress = packet.getAddress().getAddress();
                byte[] resized = Arrays.copyOf(packet.getData(), packet.getLength());
                getLogger().info("UDP packet received by DataSource, size ["+resized.length+"]");
                return resized;
            }
        } catch (IOException e) {
            if (!(e instanceof SocketTimeoutException)) {
                getLogger().warn("Error receiving Packet from socket", e);
            } else {
                getLogger().error(e.getLocalizedMessage());
            }
        }
        return null;
    }

    @Override
    protected boolean canPoll() {
        return datagramSocket != null;
    }

    @Override
    protected void cleanUpConnections() {
        if (datagramSocket != null) {
            datagramSocket.disconnect();
            datagramSocket.close();
            getLogger().info("Closed datagram socket.");
        } else {
            getLogger().warn("Unable to close datagram socket. Datagram socket was null.");
        }
    }
}
