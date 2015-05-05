package com.leidos.ode.core.controllers;

import com.leidos.ode.core.distribute.ReplayDataDistributor;
import com.leidos.ode.core.distribute.TCPReplayDataDistributor;
import com.leidos.ode.core.distribute.UDPReplayDataDistributor;
import com.leidos.ode.data.PodeProtocol;
import com.leidos.ode.data.PodeSubscriptionRequest;
import com.leidos.ode.data.ServiceRequest;
import com.leidos.ode.util.ByteUtils;
import org.springframework.stereotype.Controller;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rushk1 on 4/30/2015.
 */

@Controller
public class ReplayDataController {
    private Map<String, ReplayDataDistributor> distributors = new HashMap<String, ReplayDataDistributor>();

    public void registerReplayRequest(ServiceRequest serviceRequest, PodeSubscriptionRequest podeSubscriptionRequest) {
        ReplayDataDistributor dist = null;

        /* Here's the error, if one of these branches is commented it works fine, if both are present it fails.
         * No idea why it has this behavior, as it exists both UDP and TCP Replay Distributors are identical, and it
         * doesn't seem to matter which I include as long as I don't include both in the clause. Super weird.
         */
        if (podeSubscriptionRequest.getProtocol().getValue().equals(PodeProtocol.EnumType.upd)) {
            dist = new UDPReplayDataDistributor(podeSubscriptionRequest, serviceRequest);
        } /* else if (podeSubscriptionRequest.getProtocol().getValue().equals(PodeProtocol.EnumType.tcp)) {
            dist = new TCPReplayDataDistributor(podeSubscriptionRequest, serviceRequest);
        } */

        new Thread(dist).start();
        distributors.put(ByteUtils.convertBytesToHex(podeSubscriptionRequest.getRequestID()), dist);
    }
}
