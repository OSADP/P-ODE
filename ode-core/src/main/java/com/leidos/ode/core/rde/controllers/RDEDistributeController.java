package com.leidos.ode.core.rde.controllers;

import com.leidos.ode.core.rde.RDEDistributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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

    @Autowired
    @Qualifier("rdeIncidentDistributor")
    RDEDistributor incident;

    public RDEDistributeController() {
        // Empty constructor
    }

    @PostConstruct
    public void initialize() {
        new Thread(weather).start();
        new Thread(speed).start();
        new Thread(volume).start();
        new Thread(occupancy).start();
        new Thread(traveltime).start();
        new Thread(incident).start();
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("Closing RDE Distributors.");
        weather.setInterrupted(true);
        speed.setInterrupted(true);
        volume.setInterrupted(true);
        occupancy.setInterrupted(true);
        traveltime.setInterrupted(true);
        incident.setInterrupted(true);
    }
}
