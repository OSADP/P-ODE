package com.leidos.ode.core.controllers;

import com.leidos.ode.core.distribute.ReplayDataDistributor;
import com.leidos.ode.core.distribute.TCPReplayDataDistributor;
import com.leidos.ode.core.distribute.UDPReplayDataDistributor;
import com.leidos.ode.data.PodeProtocol;
import com.leidos.ode.data.PodeSubscriptionRequest;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.util.ByteUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Replay Data Distributor Manager
 *
 * Controls the instantiation and state of ReplayDataDistributor instances.
 */

@Controller
public class ReplayDataController implements DisposableBean {
    private Map<String, ReplayDataDistributor> distributors = new HashMap<String, ReplayDataDistributor>();

    public void registerReplayRequest(ServiceRequest serviceRequest, PodeSubscriptionRequest podeSubscriptionRequest) {
        ReplayDataDistributor dist = null;

        /* Here's the error, if one of these branches is commented it works fine, if both are present it fails.
         * No idea why it has this behavior, as it exists both UDP and TCP Replay Distributors are identical, and it
         * doesn't seem to matter which I include as long as I don't include both in the clause. Super weird.
         */
        if (podeSubscriptionRequest.getProtocol().getValue().equals(PodeProtocol.EnumType.upd)) {
            createUDP(serviceRequest, podeSubscriptionRequest);
        }  else if (podeSubscriptionRequest.getProtocol().getValue().equals(PodeProtocol.EnumType.tcp)) {
            createTCP(serviceRequest, podeSubscriptionRequest);
        } 
        
    }
    
    public void createUDP(ServiceRequest serviceRequest, PodeSubscriptionRequest podeSubscriptionRequest){
//        ReplayDataDistributor dist = getUDPReplayDistrib(serviceRequest, podeSubscriptionRequest);
        ReplayDataDistributor dist = new UDPReplayDataDistributor(podeSubscriptionRequest, serviceRequest);
        new Thread(dist).start();
        distributors.put(ByteUtils.convertBytesToHex(podeSubscriptionRequest.getRequestID()), dist);
    }
    
    private ReplayDataDistributor getUDPReplayDistrib(ServiceRequest serviceRequest, PodeSubscriptionRequest podeSubscriptionRequest){
        return new UDPReplayDataDistributor(podeSubscriptionRequest, serviceRequest);
    }
    
    public void createTCP(ServiceRequest serviceRequest, PodeSubscriptionRequest podeSubscriptionRequest){
        ReplayDataDistributor dist = new TCPReplayDataDistributor(podeSubscriptionRequest, serviceRequest);
        new Thread(dist).start();
        distributors.put(ByteUtils.convertBytesToHex(podeSubscriptionRequest.getRequestID()), dist);
    }

    public void destroy() throws Exception {
        System.out.println("Closing Replay Data Distributors.");
        Iterator<String> it = distributors.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            distributors.get(key).setInterrupted(true);
        }
    }
}
