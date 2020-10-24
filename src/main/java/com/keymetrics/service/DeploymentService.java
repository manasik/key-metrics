package com.keymetrics.service;

import com.keymetrics.entity.Metrics;
import com.keymetrics.repository.MetricsRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DeploymentService {

    private MetricsRepository metricsRepository;

    public void update(String name, Integer environment) {
        Metrics metrics = new Metrics(name, environment, new Date());
        metricsRepository.save(metrics);
    }
}
