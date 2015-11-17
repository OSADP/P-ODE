package com.leidos.ode.collector.datasource.push;

import com.leidos.ode.data.PodeSpatMap;
import org.bn.CoderFactory;
import org.bn.IEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

/**
 * Created by rushk1 on 3/26/2015.
 */
public class SPaTMAPDataSource extends PushDataSource {

    private DatagramSocket spatSocket;
    private DatagramSocket mapSocket;
    private int spatPort, mapPort;

    private static final int MAX_SPAT_LENGTH = 1400;
    private static final int MAX_MAP_LENGTH = 1400;
    private static final int SPAT_TIMEOUT = 10000;
    private static final int MAP_TIMEOUT = 10000;

    public SPaTMAPDataSource(int spatPort, int mapPort) {
        this.spatPort = spatPort;
        this.mapPort = mapPort;

        getLogger().info("Starting SPaT/MAP data source thread on ports " + spatPort + " and " + mapPort + ".");
        executeDataSourceThread();
    }

    @Override
    public byte[] pollDataSource() {
        DatagramPacket spatPacket = null;
        DatagramPacket mapPacket = null;

        // TODO: Maybe advance to latest packet on UDP port

        try {
            byte[] spatBuf = new byte[MAX_SPAT_LENGTH];
            spatPacket = new DatagramPacket(spatBuf, spatBuf.length);
            spatSocket.receive(spatPacket);
            getLogger().info("Received message over " + spatPort + " of size " + spatPacket.getLength() + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            byte[] mapBuf = new byte[MAX_MAP_LENGTH];
            mapPacket = new DatagramPacket(mapBuf, mapBuf.length);
            mapSocket.receive(mapPacket);
            getLogger().info("Received message over " + mapPort + " of size " + mapPacket.getLength() + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Use the Binarynotes BER encoder to properly format the PodeSpatMap message.
        PodeSpatMap asn = new PodeSpatMap();
        if (spatPacket != null) {
            asn.setFhwaSpat(spatPacket.getData());
        } else {
            getLogger().warn("No SPaT message received before timeout.");
        }
        if (mapPacket != null) {
            asn.setFhwaMap(mapPacket.getData());
        } else {
            getLogger().warn("No MAP message received before timeout.");
        }

        return encodeSpatMap(asn);
    }

    @Override
    protected boolean canPoll() {
        return spatSocket != null && mapSocket != null;
    }

    @Override
    protected void cleanUpConnections() {
        spatSocket.close();
        mapSocket.close();
    }

    private byte[] encodeSpatMap(PodeSpatMap data) {
        try {
            IEncoder<PodeSpatMap> encoder = new CoderFactory().getInstance().newEncoder("BER");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            encoder.encode(data, bos);
            bos.close();
            return bos.toByteArray();
        } catch (Exception e) {
            getLogger().error("Unable to encode SPaT/MAP.");
            return null;
        }
    }

    public void startDataSource() {
        try {
            spatSocket = new DatagramSocket(spatPort);
            mapSocket = new DatagramSocket(mapPort);

            spatSocket.setSoTimeout(SPAT_TIMEOUT);
            mapSocket.setSoTimeout(MAP_TIMEOUT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
