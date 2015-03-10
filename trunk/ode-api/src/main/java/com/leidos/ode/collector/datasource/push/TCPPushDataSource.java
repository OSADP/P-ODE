/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.collector.datasource.push;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *
 * @author cassadyja
 */
public class TCPPushDataSource extends PushDataSource{
    private final String TAG = getClass().getSimpleName();
    private final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TAG);
    private ServerSocket socket;

    @Override
    public void startDataSource() {
        try {
            socket = new ServerSocket(getHostPort());
            socket.setSoTimeout(10000);
            executeDataSourceThread();
        } catch (IOException ex) {
            logger.error("Error creating socket ", ex);
        }
    }

    @Override
    public byte[] pollDataSource() {
        byte[] returnVal = null;
        try {
            logger.debug("Polling TCP Push Source.");
            Socket clientSocket = socket.accept();
            logger.debug("TCP Push Source Socket Connection received.");
            DataInputStream bis = new DataInputStream(clientSocket.getInputStream());
            int msgSize = bis.readInt();
            logger.debug("TCP Push Source Read message size: "+msgSize);
            byte[] buffer = new byte[msgSize];
            returnVal = new byte[msgSize];
            ByteBuffer retBuffer = ByteBuffer.wrap(returnVal);
            boolean end = false;
            int bytesRead = 0;
            int totalRead = 0;
            while(!end){
                bytesRead = bis.read(buffer);
                byte[] resized = Arrays.copyOf(buffer, bytesRead);
                retBuffer.put(resized);

                totalRead += bytesRead;
                logger.debug("TCP Push Source Total Bytes Read: "+ totalRead);
                if(totalRead == msgSize){
                    end = true;
                }
            }
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            dos.writeInt(1);
            dos.flush();
            clientSocket.close();
        } catch (IOException ex) {
            if (!(ex instanceof SocketTimeoutException)) {
                getLogger().warn("Error receiving Packet from socket", ex);
            } else {
                getLogger().error(ex.getLocalizedMessage());
            }
        }
        return returnVal;
    }

    @Override
    protected boolean canPoll() {
        return socket!=null;
    }

    @Override
    protected void cleanUpConnections() {
        try {
            if(socket != null){
                socket.close();
            }
        } catch (IOException ex) {
            logger.error("Error closing socket ", ex);
        }catch(Exception ex){
            logger.warn("Error closing socket ", ex);
        }
    }
}
