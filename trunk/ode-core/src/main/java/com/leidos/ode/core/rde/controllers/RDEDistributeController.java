package com.leidos.ode.core.rde.controllers;

import com.leidos.ode.core.rde.RDEDistributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

/**
 * Controller object just to instantiate the RDEDistributors necessary to send data to the RDE about all topics
 */

@Controller
public class RDEDistributeController {
    @Autowired
    @Qualifier("rdeWeatherDistributor")
    RDEDistributor weather;

    @Autowired
    @Qualifier("rdeSpeedDistributor")
    RDEDistributor speed;

    @Autowired
    @Qualifier("rdeVolumeDistributor")
    RDEDistributor volume;

    @Autowired
    @Qualifier("rdeOccupancyDistributor")
    RDEDistributor occupancy;

    @Autowired
    @Qualifier("rdeTravelTimeDistributor")
    RDEDistributor traveltime;

    public RDEDistributeController() {
        // Empty constructor
    }

    @PostConstruct
    public void initialize() {
        weather.run();
        speed.run();
        volume.run();
        occupancy.run();
        traveltime.run();
    }
}
