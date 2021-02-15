package com.keymetrics.service;

import com.keymetrics.entity.Deployment;
import com.keymetrics.entity.Metrics;
import com.keymetrics.repository.MetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentService {

    private final MetricsRepository metricsRepository;

    public void update(String name, Integer environment, String buildVersion, Boolean buildPassed) {
        Metrics existingMetricsForService = metricsRepository.findByServiceNameOrderByDeploymentsDesc(name);

        if (existingMetricsForService == null) {
            String id = UUID.randomUUID().toString();
            Deployment deployment = new Deployment(environment, OffsetDateTime.now(), buildVersion, buildPassed);
            Metrics metrics = new Metrics(id, name, List.of(deployment));
            metricsRepository.save(metrics);
        } else {
            Deployment latestDeployment = new Deployment(environment, OffsetDateTime.now(), buildVersion, buildPassed);
            ArrayList<Deployment> updatedDeployments = new ArrayList<>(existingMetricsForService.deployments);
            updatedDeployments.add(0, latestDeployment);
            existingMetricsForService.setDeployments(updatedDeployments);
            metricsRepository.save(existingMetricsForService);
        }
    }

    public List<String> getServices() {
        return metricsRepository.findAll().stream().map(Metrics::getServiceName).collect(Collectors.toList());
    }
}
