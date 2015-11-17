package com.leidos.ode.collector.datasource.push;

import com.leidos.ode.collector.datasource.DataSource;

/**
 * Data will be sent to this collector from the provider.
 * The collector will provide a way for the data to be sent, that could be a UDP port or some other method.
 * Once the data has been received it will send it back to the Collector listener.
 * <p/>
 * Sources following this data should extend this class.
 *
 * @author cassadyja
 */
public abstract class PushDataSource extends DataSource {

    private String queueName;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
