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

    public List<LeadTimeForChange> getLeadTimeForChange(String serviceName) {
        Metrics metrics = metricsRepository.findByServiceNameOrderByDeploymentsDesc(serviceName);
        Map<String, List<OffsetDateTime>> buildVersionsWithTime = getBuildVersionsWithTime(metrics.deployments);
        List<LeadTimeForChange> leadTimeForChanges = new ArrayList<>();
        buildVersionsWithTime.forEach((key, values) -> {
            if (values.size() == 2) {
                long calculateTimeTakenToReachFinalEnvironment = Math.abs(values.get(0).until(values.get(1), ChronoUnit.MINUTES));
                leadTimeForChanges.add(LeadTimeForChange.builder().buildVersion(key).timeInMinutes((int) calculateTimeTakenToReachFinalEnvironment).build());
            }
        });
        return leadTimeForChanges;
    }

    private Map<String, List<OffsetDateTime>> getBuildVersionsWithTime(List<Deployment> deployments) {
        return deployments.stream().collect(Collectors.toMap(e -> e.buildVersion, e -> List.of(e.deployedAt),
                (oldValue, newValue) -> Stream.of(oldValue, newValue).flatMap(Collection::stream).collect(Collectors.toList())));
    }
}
