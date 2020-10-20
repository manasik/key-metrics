package com.keymetrics.controller;

import com.keymetrics.entity.Metrics;
import com.keymetrics.repository.MetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class DeploymentController {

    @Autowired
    MetricsRepository metricsRepository;

    @RequestMapping("/")
    public String index() {
        Metrics savedMetrics = metricsRepository.save(new Metrics("blah"+ UUID.randomUUID()));
        return savedMetrics.serviceName;
    }
}
