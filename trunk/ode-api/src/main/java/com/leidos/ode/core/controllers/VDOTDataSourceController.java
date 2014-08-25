package com.leidos.ode.core.controllers;

import com.leidos.ode.collector.VDOTDataSource;
import com.leidos.ode.collector.datasource.DataSourceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Class for testing VDOT data source.
 */
@Controller
public class VDOTDataSourceController {

    private final String TAG = getClass().getSimpleName();

    private Logger logger = Logger.getLogger(TAG);

    @Autowired
    private VDOTDataSource vdotDataSource;

    @RequestMapping(value = "test/{feed}", method = RequestMethod.GET)
    public
    @ResponseBody
    String testVDOTDataSource(@PathVariable String feed) {
        try {
            vdotDataSource.startDataSource();
        } catch (DataSourceException e) {
            logger.error(e.getLocalizedMessage());
        }
        return vdotDataSource.retrieveData(feed);

    }

}
