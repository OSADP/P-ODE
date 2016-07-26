/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leidos.ode.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;


/**
 *
 * @author cassadyja
 */
public class TestEncodeServiceRequest {
    
    public static void main(String[] args){
        TestEncodeServiceRequest tester = new TestEncodeServiceRequest();
        tester.test();
        tester.testDecode();
        
    }
    
    public void test(){
        ServiceRequest srvRequest = new ServiceRequest();
        PodeDialogID dId = new PodeDialogID();
        dId.setValue(PodeDialogID.EnumType.podeDataSubscriptionRegistration);
        srvRequest.setDialogID(dId);
        
        GroupID gId = new GroupID(new byte[]{0x01,0x00,0x00,0x00});
        srvRequest.setGroupID(gId);
        
        srvRequest.setRequestID(new byte[]{0,0,0,1});
        
        SemiSequenceID seqId = new SemiSequenceID();
        seqId.setValue(SemiSequenceID.EnumType.svcReq);
        srvRequest.setSeqID(seqId);
        
        System.out.println(seqId.getValue());
        System.out.println(dId.getValue());
        
        ConnectionPoint cp = new ConnectionPoint();
        IpAddress ip = new IpAddress();
        IPv4Address ip4 = new IPv4Address();
        
        ip4.setValue(new byte[]{10,10,10,23});
        ip.selectIpv4Address(ip4);
                
        cp.setAddress(ip);
        PortNumber pn = new PortNumber(new Integer(14000));
        cp.setPort(pn);
        srvRequest.setDestination(cp);
        
        try {
            IEncoder encoder =  CoderFactory.getInstance().newEncoder("BER");
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            
            encoder.encode(srvRequest, os);
            byte[] bytes = os.toByteArray();
            for (int i = 0; i < bytes.length; i++) {
                System.out.printf("%02X ", bytes[i]);
            }
            
            System.out.println();            
        } catch (Exception ex) {
            Logger.getLogger(TestEncodeServiceRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void testDecode(){
        try {
            IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
            byte[] bytes = hexToBinary("30 21 80 02 00 C1 81 01 01 82 04 01 00 00 00 83 04 00 00 00 01 A4 0C A0 06 80 04 0A 0A 0A 17 81 02 36 B0");
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ServiceRequest request = decoder.decode(bis, ServiceRequest.class);
            System.out.println(request.getGroupID());
        } catch (Exception ex) {
            Logger.getLogger(TestEncodeServiceRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private String convertBytesToHex(byte[] bytes){
        String hexString = DatatypeConverter.printHexBinary(bytes);
        return  hexString;
    }

    private byte[] hexToBinary(String hex){
        hex = hex.replaceAll(" ", "");
        byte[] byteArray = DatatypeConverter.parseHexBinary(hex);
        return byteArray;
    }
    
}
