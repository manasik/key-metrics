package com.keymetrics.service;

import com.keymetrics.domain.LeadTimeForChange;
import com.keymetrics.entity.Deployment;
import com.keymetrics.entity.Metrics;
import com.keymetrics.repository.MetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentService {

    private final MetricsRepository metricsRepository;

    public void update(String name, Integer environment, String buildVersion) {
        Metrics existingMetricsForService = metricsRepository.findByServiceNameOrderByDeploymentsDesc(name);
        String deploymentId = UUID.randomUUID().toString();

        if (existingMetricsForService == null) {
            String metricsId = UUID.randomUUID().toString();
            Deployment deployment = new Deployment(deploymentId, environment, OffsetDateTime.now(), buildVersion);
            Metrics metrics = new Metrics(metricsId, name, List.of(deployment));
            metricsRepository.save(metrics);
        } else {
            Deployment latestDeployment = new Deployment(deploymentId, environment, OffsetDateTime.now(), buildVersion);
            ArrayList<Deployment> updatedDeployments = new ArrayList<>(existingMetricsForService.deployments);
            updatedDeployments.add(0, latestDeployment);
            existingMetricsForService.setDeployments(updatedDeployments);
            metricsRepository.save(existingMetricsForService);
        }
    }
}
