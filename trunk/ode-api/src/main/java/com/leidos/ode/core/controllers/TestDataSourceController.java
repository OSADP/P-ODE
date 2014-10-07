package com.leidos.ode.core.controllers;

import com.leidos.ode.agent.datatarget.ODEDataTarget;
import com.leidos.ode.collector.ODECollector;
import com.leidos.ode.collector.RITISDataSource;
import com.leidos.ode.collector.VDOTDataSource;
import com.leidos.ode.collector.datasource.CollectorDataSource;
import com.leidos.ode.collector.datasource.CollectorDataSource.CollectorDataSourceListener;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("VDOTCollector")
    private ODECollector vdotCollector;
    @Autowired
    @Qualifier("BasicRITISCollector")
    private ODECollector ritisCollector;
    private CollectorDataSourceListener listener;

    public TestDataSourceController() {
        listener = new CollectorDataSource.CollectorDataSourceListener() {
            @Override
            public void onDataReceived(byte[] receivedData) {
                logger.debug("received data: " + new String(receivedData));
            }
        };
        //Setting request limit to slow down the requests
    }

    @RequestMapping(value = "test/{dataSource}/{feed}", method = RequestMethod.GET)
    public
    @ResponseBody
    String testDataSourceFeed(@PathVariable String dataSource, @PathVariable String feed) {
        try {
            if (dataSource.equalsIgnoreCase("vdot")) {
                ((VDOTDataSource) vdotCollector.getDataSource()).setFeedName(feed);
                ((VDOTDataSource) vdotCollector.getDataSource()).setRequestLimit("10000");
                vdotCollector.startUp(listener);
            }
            if (dataSource.equalsIgnoreCase("ritis")) {
                ((RITISDataSource) ritisCollector.getDataSource()).setFeedName(feed);
                ritisCollector.startUp(listener);
            }
        } catch (ODEDataTarget.DataTargetException e) {
            logger.error(e.getLocalizedMessage());
        } catch (CollectorDataSource.DataSourceException e) {
            logger.error(e.getLocalizedMessage());
        }
        return "Started data source.";
    }

    @RequestMapping(value = "test/stopAllSources", method = RequestMethod.GET)
    public
    @ResponseBody
    String stopAllDataSources() {
        vdotCollector.stop();
        ritisCollector.stop();
        return "Stopped all data sources.";
    }
}
