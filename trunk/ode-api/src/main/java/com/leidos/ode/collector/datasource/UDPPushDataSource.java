package com.leidos.ode.collector.datasource;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPPushDataSource extends PushDataSource {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    private DatagramSocket datagramSocket = null;
    private boolean interrupted = false;
    private boolean stopped = false;

    @Override
    public void startDataSource() throws DataSourceException {
        try {
            InetAddress tmpAddress = InetAddress.getByName(getHostAddress());
            logger.info("TMP Host Address: " + tmpAddress.getHostAddress());
            byte[] addressBytes = tmpAddress.getAddress();
            InetAddress address = Inet6Address.getByAddress(getHostAddress(), addressBytes);
            logger.info("Host Address: " + address.getHostAddress());
            logger.info("Host Address: " + address.getCanonicalHostName());

            logger.info("Connecting datagram socket on port: " + getHostPort());
            datagramSocket = new DatagramSocket(getHostPort(), address);
            datagramSocket.setSoTimeout(10000);
            new Thread(new DataSocketListener()).start();
        } catch (UnknownHostException e) {
            throw new DataSourceException("");
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private class DataSocketListener implements Runnable {

        @Override
        public void run() {
            try {
                while (!interrupted) {
                    try {
                        byte[] receiveData = new byte[5120];
                        DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
                        datagramSocket.receive(packet);
                        if (packet.getLength() > 0) {
                            logger.debug(packet.getLength());
                            byte[] sourceAddress = packet.getAddress().getAddress();

                            logger.debug(packet.getAddress().getHostName());

                            byte[] resized = Arrays.copyOf(packet.getData(), packet.getLength());
                            getCollectorDataSourceListener().dataReceived(resized);
                        }
                    } catch (IOException e) {
                        if (!(e instanceof SocketTimeoutException)) {
                            logger.warn("Error receiving Packet from socket", e);
                        }
                    }
                }
            } catch (Exception e) {
                logger.fatal("Error with UDP Listener", e);
                e.printStackTrace();
            } finally {
                stopped = true;
            }
        }
    }
}
