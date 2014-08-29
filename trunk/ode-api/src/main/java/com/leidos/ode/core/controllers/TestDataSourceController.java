package com.leidos.ode.core.controllers;

import com.leidos.ode.collector.RITISDataSource;
import com.leidos.ode.collector.VDOTDataSource;
import com.leidos.ode.collector.datasource.DataSourceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Class for testing VDOT data source.
 */
@Controller
public class TestDataSourceController {

    private final String TAG = getClass().getSimpleName();
    private Logger logger = Logger.getLogger(TAG);

    @Autowired
    private VDOTDataSource vdotDataSource;
    @Autowired
    private RITISDataSource ritisDataSource;

    @RequestMapping(value = "test/{dataSource}/{feed}", method = RequestMethod.GET)
    public
    @ResponseBody
    String testVDOTDataSource(@PathVariable String dataSource, @PathVariable String feed) {
        try {
            if (dataSource.equalsIgnoreCase("vdot")) {
                vdotDataSource.setFeedName(feed);
                return new String(vdotDataSource.getDataFromSource());
            }
            if (dataSource.equalsIgnoreCase("ritis")) {
                ritisDataSource.setFeedName(feed);
                return new String(ritisDataSource.getDataFromSource());
            }
        } catch (DataSourceException e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }
}
