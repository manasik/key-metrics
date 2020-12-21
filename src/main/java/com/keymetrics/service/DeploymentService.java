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
        String id = UUID.randomUUID().toString();
        Deployment deployment = new Deployment(environment, OffsetDateTime.now(), buildVersion);
        Metrics metrics = new Metrics(id, name, List.of(deployment));
        metricsRepository.save(metrics);
    }

    public List<LeadTimeForChange> getLeadTimeForChange(String serviceName) {
        Metrics metrics = metricsRepository.findByServiceNameOrderByDeploymentsDesc(serviceName);
        Map<String, List<OffsetDateTime>> buildVersionsWithTime = getBuildVersionsWithTime(metrics.deployments);
        List<LeadTimeForChange> leadTimeForChanges = new ArrayList<>();
        buildVersionsWithTime.forEach((key, values) -> {
            Long timeForEachBuild = calculateTimeTakenToReachFinalEnvironment(values);
            if (timeForEachBuild != null) {
                leadTimeForChanges.add(LeadTimeForChange.builder().buildVersion(key).timeInMinutes(timeForEachBuild.intValue()).build());
            }
        });
        return leadTimeForChanges;
    }

    private Long calculateTimeTakenToReachFinalEnvironment(List<OffsetDateTime> values) {
        return values.size() < 2 ? null :  Math.abs(values.get(0).until(values.get(1), ChronoUnit.MINUTES));
    }

    private Map<String, List<OffsetDateTime>> getBuildVersionsWithTime(List<Deployment> deployments) {
        Map<String, List<OffsetDateTime>> collect = deployments.stream().collect(Collectors.toMap(e -> e.buildVersion, e -> List.of(e.deployedAt),
                (oldValue, newValue) -> Stream.of(oldValue, newValue).flatMap(Collection::stream).collect(Collectors.toList())));
        return collect;
    }
}
