package com.leidos.ode.collector.datasource;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPPushDataSource extends PushDataSource {

    private static Logger logger = Logger.getLogger(UDPPushDataSource.class);

    private String listenerAddress;
    private int listenerPort;

    private DatagramSocket ss = null;
    private boolean interrupted = false;
    private boolean stopped = false;
    
    private class DataSocketListener implements Runnable {

        public void run() {
            try {
                while (!interrupted) {
                    try {
                        byte[] receiveData = new byte[5120];
                        DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
                        ss.receive(packet);
                        if (packet.getLength() > 0) {
                            logger.debug(packet.getLength());
                            byte[] sourceAddress = packet.getAddress().getAddress();

                            logger.debug(packet.getAddress().getHostName());
                            
                            byte[] resized = Arrays.copyOf(packet.getData(), packet.getLength());
                            listener.dataReceived(resized);
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

    @Override
    public void startDataSource() throws DataSourceException {
        try {
            InetAddress tmpAddress = InetAddress.getByName(listenerAddress);
            logger.info("TMP Host Address: " + tmpAddress.getHostAddress());
            byte[] addressBytes = tmpAddress.getAddress();
            InetAddress address = Inet6Address.getByAddress(listenerAddress, addressBytes);
            logger.info("Host Address: " + address.getHostAddress());
            logger.info("Host Address: " + address.getCanonicalHostName());

            logger.info("Connecting datagram socket on port: " + listenerPort);
            ss = new DatagramSocket(listenerPort, address);

            ss.setSoTimeout(10000);
        } catch (UnknownHostException e) {
            throw new DataSourceException("");
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    @Override
    public byte[] getDataFromSource() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getListenerAddress() {
        return listenerAddress;
    }

    public void setListenerAddress(String listenerAddress) {
        this.listenerAddress = listenerAddress;
    }

    public int getListenerPort() {
        return listenerPort;
    }

    public void setListenerPort(int listenerPort) {
        this.listenerPort = listenerPort;
    }

}
