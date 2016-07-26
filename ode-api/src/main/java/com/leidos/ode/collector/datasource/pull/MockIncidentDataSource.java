package com.leidos.ode.collector.datasource.pull;

import com.leidos.ode.collector.datasource.DataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 * Created by rushk1 on 5/19/2016.
 */
public class MockIncidentDataSource extends DataSource {

    private static final int OUTPUT_DELAY = 5000;
    private Logger log = LogManager.getLogger(MockIncidentDataSource.class);

    @Override
    public byte[] pollDataSource() {
        try {
            Thread.sleep(OUTPUT_DELAY);
        } catch (InterruptedException e) {
            log.info("Outputting incident data.");
            e.printStackTrace();
        }

        return new byte[0];
    }

    @Override
    protected boolean canPoll() {
        return true;
    }

    @Override
    protected void cleanUpConnections() {
        // NO-OP
    }

    @Override
    public void startDataSource() {
        executeDataSourceThread();
    }
}
