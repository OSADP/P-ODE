package com.leidos.ode.core.dao;

import com.leidos.ode.core.data.QueueInfo;
import com.leidos.ode.data.ConnectionPoint;
import com.leidos.ode.data.DataSubscriptionResponse;
import com.leidos.ode.data.IPv4Address;
import com.leidos.ode.data.IpAddress;
import com.leidos.ode.data.PodeDataPulicationRegistration;
import com.leidos.ode.data.PodeSubscriptionRequest;
import com.leidos.ode.data.PortNumber;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.registration.request.ODERegistrationRequest;
import com.leidos.ode.util.ByteUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import javax.xml.bind.DatatypeConverter;

/**
 * Class to handle the registration information access to the database.
 *
 * @author lamde
 */
@Component
public class RegistrationDAOImpl implements RegistrationDAO {

    @Override
    public void storeServiceRequest(ServiceRequest serviceRequest, boolean subscribe) {
       RegistrationRow row = populateRow(serviceRequest, subscribe);
        SqlSessionFactory sqlMapper = DBSessionManager.getSqlSessionFactory();
        SqlSession session = sqlMapper.openSession(true);
        try {
            session.insert("com.leidos.ode.RegistrationMapper.insertServiceRequest", row);
        } finally {
            session.close();
        }       
    }
    
    private RegistrationRow populateRow(ServiceRequest serviceRequest, boolean subscribe){
        RegistrationRow row = new RegistrationRow();
        if(subscribe){
            row.setRegType("Subscribe");
        }else{
            row.setRegType("Publish");
        }
        
        row.setDialogId(serviceRequest.getDialogID().getValue().toString());
        row.setGroupId(ByteUtils.convertBytesToHex(serviceRequest.getGroupID().getValue()));
        row.setRequestId(ByteUtils.convertBytesToHex(serviceRequest.getRequestID()));
        row.setSequenceId(serviceRequest.getSeqID().getValue().toString());
        
        if(subscribe){
            row.setSubAddress(ByteUtils.buildIpAddressFromBytes(serviceRequest.getDestination().getAddress().getIpv4Address().getValue()));
            row.setSubPort(serviceRequest.getDestination().getPort().getValue().toString());
        }
        //TODO: set values
        row.setStartDate(null);
        row.setEndDate(null);
        
        return row;
    }
    
    @Override
    public ServiceRequest getServiceRequestForRequestId(byte[] requestId){
        ServiceRequest request = null;
        String id = ByteUtils.convertBytesToHex(requestId);
        SqlSessionFactory sqlMapper = DBSessionManager.getSqlSessionFactory();
        SqlSession session = sqlMapper.openSession(true);
        try {
            RegistrationRow row = session.selectOne("com.leidos.ode.RegistrationMapper.selectRegistrationForRequestId", id);
            request = new ServiceRequest();
            ConnectionPoint point = new ConnectionPoint();
            IpAddress address = new IpAddress();
            IPv4Address ip4 = new IPv4Address();
            byte[] bytes = new byte[4];
            String[] split = row.getSubAddress().split("\\.");
            for(int i=0;i<split.length;i++){
                bytes[i] = Byte.parseByte(split[i]);
            }
            ip4.setValue(bytes);
            address.selectIpv4Address(ip4);
            point.setAddress(address);
            point.setPort(new PortNumber(Integer.parseInt(row.getSubPort())));
            request.setDestination(point);
                    
            //TODO: fill in the rest of the values incase we need them someday.
        } finally {
            session.close();
        }       

        
        return request;
    }
    


    
    @Override
    public ODERegistrationRequest storeRegistration(ODERegistrationRequest regInfo) {
        SqlSessionFactory sqlMapper = DBSessionManager.getSqlSessionFactory();
        SqlSession session = sqlMapper.openSession(true);
        try {
            session.insert("com.leidos.ode.RegistrationMapper.insertRegistration", regInfo);
        } finally {
            session.close();
        }
        return regInfo;
    }

    @Override
    public ODERegistrationRequest getRegistrationForId(int id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ODERegistrationRequest> getAllRegistration() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ODERegistrationRequest> getAllRegistrationForAgent(String agentId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public QueueInfo getQueueForRegistration(ODERegistrationRequest regInfo) {
        SqlSessionFactory sqlMapper = DBSessionManager.getSqlSessionFactory();
        SqlSession session = sqlMapper.openSession(true);
        QueueInfo info = null;
        try {
            info = session.selectOne("com.leidos.ode.RegistrationMapper.selectQueueForRegistration", regInfo);
        } finally {
            session.close();
        }
        return info;
    }
    
    @Override
    public QueueInfo getQueueForMessageType(String messageType) {
        SqlSessionFactory sqlMapper = DBSessionManager.getSqlSessionFactory();
        SqlSession session = sqlMapper.openSession(true);
        QueueInfo info = null;
        try {
            info = session.selectOne("com.leidos.ode.RegistrationMapper.selectQueueForMessageType", messageType);
        } finally {
            session.close();
        }
        return info;
    }    

    
    public void updateRegistration(PodeSubscriptionRequest subRegistration, DataSubscriptionResponse subResponse){
        RegistrationRow row = new RegistrationRow();
        row.setRequestId(ByteUtils.convertBytesToHex(subResponse.getRequestID()));
        row.setSubId(ByteUtils.convertBytesToHex(subResponse.getSubID()));
        byte[] bytes = subRegistration.getSubData().getDataElements().getValue();
        if(bytes.length > 0){
            row.setDataTypes(Integer.toString(bytes[0], 2));
        }
        
        SqlSessionFactory sqlMapper = DBSessionManager.getSqlSessionFactory();
        SqlSession session = sqlMapper.openSession(true);
        QueueInfo info = null;
        try {
            session.update("com.leidos.ode.RegistrationMapper.updateRegistration", row);
        } finally {
            session.close();
        }
    }
    
    
    public void updateRegistration(PodeDataPulicationRegistration pubRegistration){
        RegistrationRow row = new RegistrationRow();
        row.setRequestId(ByteUtils.convertBytesToHex(pubRegistration.getRequestID()));
        byte[] bytes = pubRegistration.getRegData().getValue();
        if(bytes.length > 0){
            row.setDataTypes(Integer.toString(bytes[0], 2));
        }
        
        SqlSessionFactory sqlMapper = DBSessionManager.getSqlSessionFactory();
        SqlSession session = sqlMapper.openSession(true);
        QueueInfo info = null;
        try {
            session.update("com.leidos.ode.RegistrationMapper.updateRegistration", row);
        } finally {
            session.close();
        }
    }    
    
    
}
