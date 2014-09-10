package com.leidos.ode.core.controllers;

import com.leidos.ode.collector.RITISDataSource;
import com.leidos.ode.collector.VDOTDataSource;
import com.leidos.ode.collector.datasource.CollectorDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Class for testing data sources (currently supports VDOT and RITIS).
 */
@Controller
public class TestDataSourceController {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);
    @Autowired
    private VDOTDataSource vdotDataSource;
    @Autowired
    private RITISDataSource ritisDataSource;
    private CollectorDataSource.CollectorDataSourceListener listener;

    public TestDataSourceController() {
        listener = new CollectorDataSource.CollectorDataSourceListener() {
            @Override
            public void onDataReceived(byte[] receivedData) {
                logger.debug("received data: " + new String(receivedData));
            }
        };
    }

    @RequestMapping(value = "test/{dataSource}/{feed}", method = RequestMethod.GET)
    public
    @ResponseBody
    String testVDOTDataSource(@PathVariable String dataSource, @PathVariable String feed) {
        if (dataSource.equalsIgnoreCase("vdot")) {
            vdotDataSource.setFeedName(feed);
            vdotDataSource.startDataSource(listener);
        }
        if (dataSource.equalsIgnoreCase("ritis")) {
            ritisDataSource.setFeedName(feed);
            ritisDataSource.startDataSource(listener);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Started data source ");
        stringBuilder.append(dataSource);
        stringBuilder.append(" with feed ");
        stringBuilder.append(feed);
        stringBuilder.append(".");
        return stringBuilder.toString();
    }
}
