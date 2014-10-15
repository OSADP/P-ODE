package com.leidos.ode.collector.datasource;

import com.leidos.ode.collector.datasource.CollectorDataSource.DataSourceException;
import junit.framework.TestCase;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author cassadyja
 */
public class TestJMSPushDataSource extends TestCase {


//    public void testConnect(){
//        JMSPushDataSource ds = new JMSPushDataSource();
//        ds.setHostURL("jms1.ritis.org");
//        ds.setHostPort("61617");
//        ds.setUser("leidos");
//        ds.setPass("H3SvjshHnNBX");
//        ds.setQueueName("Clients.leidos.ATIS.Events");
//
//        try {
//            ds.startDataSource();
//        } catch (DataSourceException ex) {
//            Logger.getLogger(TestJMSPushDataSource.class.getName()).log(Level.SEVERE, null, ex);
//            ex.printStackTrace();
//            fail();
//        }
//    }

    public void testConnectTunnel() {
        JMSPushDataSource ds = new JMSPushDataSource();
        ds.setHostProtocol("tcp://");
        ds.setHostAddress("localhost");
        ds.setHostPort(7847);
        ds.setUsername("leidos");
        ds.setPassword("H3SvjshHnNBX");
        ds.setQueueName("Clients.leidos.ATIS.Events");
        try {
            ds.startDataSource(new CollectorDataSource.CollectorDataSourceListener() {
                @Override
                public void onDataReceived(byte[] receivedData) {

                }
            });
        } catch (DataSourceException ex) {
            Logger.getLogger(TestJMSPushDataSource.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            fail();
        }
    }


}
