package com.leidos.ode.core.rde;

import org.dot.rdelive.api.Datum;
import org.dot.rdelive.client.api.RDEClientConfig;
import org.dot.rdelive.impl.GenericDatum;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Stores context and state information such as the queue and clientmode.
 */
public class RDEClientContext implements org.dot.rdelive.client.api.RDEClientContext<Datum<char[]>, char[]> {
    private BlockingQueue<Datum<char[]>> clientQueue = new LinkedBlockingQueue<Datum<char[]>>();
    private RDEClientConfig<Datum<char[]>, char[]> config;

    public RDEClientContext(BlockingQueue<Datum<char[]>> queue, RDEClientConfig<Datum<char[]>, char[]> config) {
        this.clientQueue = queue;
        this.config = config;
    }

    @Override
    public BlockingQueue<Datum<char[]>> getQueue() {
        return clientQueue;
    }

    @Override
    public void setQueue(BlockingQueue queue) {
        this.clientQueue = queue;
    }

    @Override
    public void setConfig(RDEClientConfig config) {
        this.config = config;
    }

    @Override
    public RDEClientConfig<Datum<char[]>, char[]> getConfig() {
        return config;
    }
}
