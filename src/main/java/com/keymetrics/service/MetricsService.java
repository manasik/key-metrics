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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@EnableMongoRepositories(basePackages = "com.keymetrics.repository")
@Slf4j
public class MetricsService {
    private final MetricsRepository metricsRepository;

    public com.keymetrics.domain.Metrics getMetrics(String serviceName) {
        Metrics metrics = metricsRepository.findByServiceNameOrderByDeploymentsDesc(serviceName);
        List<LeadTimeForChange> leadTimeForChange = getLeadTimeForChange(metrics);
        List<com.keymetrics.domain.Deployment> deployments = getDeploymentsForService(metrics);
        return com.keymetrics.domain.Metrics.builder().serviceName(serviceName).leadTimeForChange(leadTimeForChange).deployments(deployments).build();
    }

    private List<LeadTimeForChange> getLeadTimeForChange(Metrics metrics) {
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

    private List<com.keymetrics.domain.Deployment> getDeploymentsForService(Metrics metrics) {
        List<Deployment> deployments = metrics.deployments;

        return deployments.stream().map(x -> com.keymetrics.domain.Deployment.builder()
                .buildVersion(x.buildVersion).deployedAt(x.deployedAt.toLocalDate()).build())
                .collect(Collectors.toList());
    }
}
