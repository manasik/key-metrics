package com.keymetrics.service;

import com.keymetrics.domain.LeadTimeForChange;
import com.keymetrics.entity.Deployment;
import com.keymetrics.entity.Metrics;
import com.keymetrics.exception.MetricsNotFoundException;
import com.keymetrics.repository.MetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {
    public static final int ENVIRONMENT_1 = 1;
    public static final int ENVIRONMENT_2 = 2;
    private final MetricsRepository metricsRepository;

    public com.keymetrics.domain.Metrics getMetrics(String serviceName) {
        Metrics metrics = metricsRepository.findByServiceNameOrderByDeploymentsDesc(serviceName);

        if (metrics != null) {
            List<LeadTimeForChange> leadTimeForChange = getLeadTimeForChange(metrics);
            List<com.keymetrics.domain.Deployment> deployments = getDeploymentsForService(metrics);
            return com.keymetrics.domain.Metrics.builder().serviceName(serviceName).leadTimeForChange(leadTimeForChange).deployments(deployments).build();
        }

        throw new MetricsNotFoundException(serviceName);
    }

    private List<LeadTimeForChange> getLeadTimeForChange(Metrics metrics) {
        List<LeadTimeForChange> leadTimeForChanges = new ArrayList<>();
        Map<String, Map<Integer, List<OffsetDateTime>>> buildVersionForEachEnv = getDeploymentsForEachBuildVersionForEachEnv(metrics.deployments);

        Map<String, Double> avgLeadTimesPerMonth = averageLeadTimeForChange(buildVersionForEachEnv);

        avgLeadTimesPerMonth.forEach((k, v) -> {
            leadTimeForChanges.add(LeadTimeForChange.builder().month(k).numberOfDays(v).build());
        });

        return leadTimeForChanges;
    }

    private Map<String, Double> averageLeadTimeForChange(Map<String, Map<Integer, List<OffsetDateTime>>> buildVersionForEachEnv) {
        ArrayList<Map<String, Double>> monthsWithNumOfDaysList = new ArrayList<>();
        buildVersionForEachEnv.forEach((buildVersion, mapOfDeployedTimesForEnv) -> {
            if (isValid(mapOfDeployedTimesForEnv)) {
                OffsetDateTime lastDeployedBuildForEnv1 = mapOfDeployedTimesForEnv.get(ENVIRONMENT_1).get(0);
                OffsetDateTime lastDeployedBuildForEnv2 = mapOfDeployedTimesForEnv.get(ENVIRONMENT_2).get(0);
                Long daysTakenToReachFinalEnvironment = Math.abs(lastDeployedBuildForEnv1.until(lastDeployedBuildForEnv2, ChronoUnit.DAYS));
                Map<String, Double> monthWithNumOfDays = new HashMap<>();
                monthWithNumOfDays.put(lastDeployedBuildForEnv2.getMonth()
                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH), daysTakenToReachFinalEnvironment.doubleValue());
                monthsWithNumOfDaysList.add(monthWithNumOfDays);
            }
        });

        return monthsWithNumOfDaysList.stream().flatMap(innerMap -> innerMap.entrySet().stream()).collect(Collectors.groupingBy(Map.Entry::getKey,
                Collectors.averagingDouble(Map.Entry::getValue)));
    }

    private boolean isValid(Map<Integer, List<OffsetDateTime>> mapOfDeployedTimesForEnv) {
        return mapOfDeployedTimesForEnv.containsKey(ENVIRONMENT_1) && mapOfDeployedTimesForEnv.containsKey(ENVIRONMENT_2) &&
                mapOfDeployedTimesForEnv.get(ENVIRONMENT_1).get(0).compareTo(mapOfDeployedTimesForEnv.get(ENVIRONMENT_2).get(0)) < 0;
    }

    private Map<String, Map<Integer, List<OffsetDateTime>>> getDeploymentsForEachBuildVersionForEachEnv(List<Deployment> deployments) {
        return deployments.stream()
                .collect(Collectors.groupingBy(Deployment::getBuildVersion,
                        Collectors.groupingBy(Deployment::getEnvironment, Collectors.mapping(Deployment::getDeployedAt, Collectors.toList()))));
    }

    private List<com.keymetrics.domain.Deployment> getDeploymentsForService(Metrics metrics) {
        List<Deployment> deployments = metrics.deployments;

        return deployments.stream().map(x -> com.keymetrics.domain.Deployment.builder()
                .buildVersion(x.buildVersion).deployedAt(x.deployedAt.toLocalDate()).build())
                .collect(Collectors.toList());
    }
}
