package com.keymetrics.service;

import com.keymetrics.domain.LeadTimeForChange;
import com.keymetrics.entity.Deployment;
import com.keymetrics.entity.Metrics;
import com.keymetrics.repository.MetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {
    public static final int ENVIRONMENT_1 = 1;
    public static final int ENVIRONMENT_2 = 2;
    private final MetricsRepository metricsRepository;

    public com.keymetrics.domain.Metrics getMetrics(String serviceName) {
        Metrics metrics = metricsRepository.findByServiceNameOrderByDeploymentsDesc(serviceName);
        List<LeadTimeForChange> leadTimeForChange = getLeadTimeForChange(metrics);
        List<com.keymetrics.domain.Deployment> deployments = getDeploymentsForService(metrics);
        return com.keymetrics.domain.Metrics.builder().serviceName(serviceName).leadTimeForChange(leadTimeForChange).deployments(deployments).build();
    }

    private List<LeadTimeForChange> getLeadTimeForChange(Metrics metrics) {
        Map<String, Map<Integer, List<OffsetDateTime>>> buildVersionForEachEnv = getDeploymentsForEachBuildVersionForEachEnv(metrics.deployments);
        List<LeadTimeForChange> leadTimeForChanges = new ArrayList<>();
        buildVersionForEachEnv.forEach((buildVersion, mapOfDeployedTimesForEnv) -> {
            if (isValid(mapOfDeployedTimesForEnv)) {
                OffsetDateTime lastDeployedBuildForEnv1 = mapOfDeployedTimesForEnv.get(ENVIRONMENT_1).get(0);
                OffsetDateTime lastDeployedBuildForEnv2 = mapOfDeployedTimesForEnv.get(ENVIRONMENT_2).get(0);
                long calculateTimeTakenToReachFinalEnvironment = Math.abs(lastDeployedBuildForEnv1.until(lastDeployedBuildForEnv2, ChronoUnit.MINUTES));
                leadTimeForChanges.add(LeadTimeForChange.builder().buildVersion(buildVersion).timeInMinutes((int) calculateTimeTakenToReachFinalEnvironment).build());
            }
        });
        return leadTimeForChanges;
    }

    private boolean isValid(Map<Integer, List<OffsetDateTime>> mapOfDeployedTimesForEnv) {
        return mapOfDeployedTimesForEnv.containsKey(ENVIRONMENT_1) && mapOfDeployedTimesForEnv.containsKey(ENVIRONMENT_2) &&
                mapOfDeployedTimesForEnv.get(ENVIRONMENT_1).get(0).compareTo(mapOfDeployedTimesForEnv.get(ENVIRONMENT_2).get(0)) < 0;
    }


    private Map<String, Map<Integer, List<OffsetDateTime>>> getDeploymentsForEachBuildVersionForEachEnv(List<Deployment> deployments) {
        Map<String, List<Map<Integer, OffsetDateTime>>> listOfBuildVersions = deployments.stream().collect(Collectors.toMap(e -> e.buildVersion, e -> List.of(Map.of(e.environment, e.deployedAt)),
                (oldValue, newValue) -> Stream.of(oldValue, newValue).flatMap(Collection::stream).collect(Collectors.toList())));

        HashMap<String, Map<Integer, List<OffsetDateTime>>> mergedMap = new HashMap<>();
        listOfBuildVersions.forEach((k, v) -> {
            Map<Integer, List<OffsetDateTime>> values = v.stream().flatMap(m -> m.entrySet().stream())
                    .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
            mergedMap.put(k, values);
        });

        return mergedMap;
    }

    private List<com.keymetrics.domain.Deployment> getDeploymentsForService(Metrics metrics) {
        List<Deployment> deployments = metrics.deployments;

        return deployments.stream().map(x -> com.keymetrics.domain.Deployment.builder()
                .buildVersion(x.buildVersion).deployedAt(x.deployedAt.toLocalDate()).build())
                .collect(Collectors.toList());
    }
}
