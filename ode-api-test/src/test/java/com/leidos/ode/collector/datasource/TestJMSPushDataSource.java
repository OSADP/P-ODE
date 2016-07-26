package com.leidos.ode.collector.datasource;

import com.leidos.ode.collector.datasource.push.JMSPushDataSource;
import junit.framework.TestCase;

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
        ds.setUsername("[REDACTED]");
        ds.setPassword("[REDACTED]");
        ds.setQueueName("Clients.leidos.ATIS.Events");
        ds.startDataSource();
    }


}
