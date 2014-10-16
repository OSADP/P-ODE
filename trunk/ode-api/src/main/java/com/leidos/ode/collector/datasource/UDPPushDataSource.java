package com.leidos.ode.collector.datasource;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPPushDataSource extends PushDataSource {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private DatagramSocket datagramSocket;

    @Override
    public void startDataSource(CollectorDataSourceListener collectorDataSourceListener) throws DataSourceException {
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
            executeDataSourceThread(collectorDataSourceListener);
        } catch (UnknownHostException e) {
            throw new DataSourceException(e.getLocalizedMessage());
        } catch (SocketException e) {
            throw new DataSourceException(e.getLocalizedMessage());
        }
    }

    @Override
    protected byte[] pollDataSource() {
        try {
            byte[] receiveData = new byte[5120];
            DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
            datagramSocket.receive(packet);
            if (packet.getLength() > 0) {
                getLogger().debug(packet.getLength());
                byte[] sourceAddress = packet.getAddress().getAddress();

                getLogger().debug(packet.getAddress().getHostName());

                byte[] resized = Arrays.copyOf(packet.getData(), packet.getLength());
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
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected void cleanUpConnections() {
        datagramSocket.close();
    }
}
