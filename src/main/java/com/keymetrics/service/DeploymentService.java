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
@EnableMongoRepositories(basePackages = "com.keymetrics.repository")
@Slf4j
public class DeploymentService {

    private final MetricsRepository metricsRepository;

    public void update(String name, Integer environment, String buildVersion) {
        Metrics existingMetricsForService = metricsRepository.findByServiceNameOrderByDeploymentsDesc(name);

        if (existingMetricsForService == null) {
            String id = UUID.randomUUID().toString();
            Deployment deployment = new Deployment(environment, OffsetDateTime.now(), buildVersion);
            Metrics metrics = new Metrics(id, name, List.of(deployment));
            metricsRepository.save(metrics);
        } else {
            Deployment latestDeployment = new Deployment(environment, OffsetDateTime.now(), buildVersion);
            ArrayList<Deployment> updatedDeployments = new ArrayList<>(existingMetricsForService.deployments);
            updatedDeployments.add(0, latestDeployment);
            existingMetricsForService.setDeployments(updatedDeployments);
            metricsRepository.save(existingMetricsForService);
        }
    }
}
