package com.keymetrics.service;

import com.keymetrics.domain.LeadTimeForChange;
import com.keymetrics.domain.Metrics;
import com.keymetrics.entity.BuildInfo;
import com.keymetrics.entity.Deployment;
import com.keymetrics.exception.MetricsNotFoundException;
import com.keymetrics.repository.DeploymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {
    public static final int ENVIRONMENT_1 = 1;
    public static final int ENVIRONMENT_2 = 2;
    private final DeploymentRepository deploymentRepository;

    public Metrics getMetrics(String serviceName) {
        Deployment deployment = deploymentRepository.findByServiceNameOrderByBuildInfoDesc(serviceName);

        if (deployment != null) {
            List<LeadTimeForChange> leadTimeForChange = getLeadTimeForChange(deployment);
            List<com.keymetrics.domain.Deployment> deployments = getDeploymentsForService(deployment);
            return Metrics.builder()
                    .serviceName(serviceName)
                    .leadTimeForChange(leadTimeForChange)
                    .deployments(deployments)
                    .build();
        }

        throw new MetricsNotFoundException(serviceName);
    }

    private List<LeadTimeForChange> getLeadTimeForChange(Deployment deployment) {
        Map<String, Map<Integer, List<OffsetDateTime>>> buildVersionForEachEnv = getDeploymentsForEachBuildVersionForEachEnv(deployment.buildInfo);
        List<LeadTimeForChange> leadTimeForChanges = new ArrayList<>();
        buildVersionForEachEnv.forEach((buildVersion, mapOfDeployedTimesForEnv) -> {
            if (isValid(mapOfDeployedTimesForEnv)) {
                OffsetDateTime lastDeployedBuildForEnv1 = mapOfDeployedTimesForEnv.get(ENVIRONMENT_1).get(0);
                OffsetDateTime lastDeployedBuildForEnv2 = mapOfDeployedTimesForEnv.get(ENVIRONMENT_2).get(0);
                Long daysTakenToReachFinalEnvironment = Math.abs(lastDeployedBuildForEnv1.until(lastDeployedBuildForEnv2, ChronoUnit.DAYS));
                leadTimeForChanges.add(LeadTimeForChange.builder().deployedAt(lastDeployedBuildForEnv2.toLocalDate())
                        .numberOfDays(daysTakenToReachFinalEnvironment.doubleValue()).build());
            }
        });
        return leadTimeForChanges;
    }

    private boolean isValid(Map<Integer, List<OffsetDateTime>> mapOfDeployedTimesForEnv) {
        return mapOfDeployedTimesForEnv.containsKey(ENVIRONMENT_1) && mapOfDeployedTimesForEnv.containsKey(ENVIRONMENT_2) &&
                mapOfDeployedTimesForEnv.get(ENVIRONMENT_1).get(0).compareTo(mapOfDeployedTimesForEnv.get(ENVIRONMENT_2).get(0)) < 0;
    }

    private Map<String, Map<Integer, List<OffsetDateTime>>> getDeploymentsForEachBuildVersionForEachEnv(List<BuildInfo> deployments) {
        return deployments.stream()
                .collect(Collectors.groupingBy(BuildInfo::getBuildVersion,
                        Collectors.groupingBy(BuildInfo::getEnvironment, Collectors.mapping(BuildInfo::getDeployedAt, Collectors.toList()))));
    }

    private List<com.keymetrics.domain.Deployment> getDeploymentsForService(Deployment deployment) {
        List<BuildInfo> deployments = deployment.buildInfo;

        return deployments.stream().map(x -> com.keymetrics.domain.Deployment.builder()
                .buildVersion(x.buildVersion).deployedAt(x.deployedAt.toLocalDate()).build())
                .collect(Collectors.toList());
    }
}
