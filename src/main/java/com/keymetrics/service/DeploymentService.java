package com.keymetrics.service;

import com.keymetrics.domain.LeadChangeForTime;
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
        Metrics metrics = new Metrics(id, name, environment, OffsetDateTime.now(), buildVersion);
        metricsRepository.save(metrics);
    }

    public List<LeadChangeForTime> getLeadTimeForChange(String serviceName) {
        List<Metrics> metrics = metricsRepository.findByServiceNameOOrderByDeployedAtDesc(serviceName);
        Map<String, List<OffsetDateTime>> buildVersionsWithTime = getBuildVersionsWithTimeInMinutes(metrics);
        List<LeadChangeForTime> leadChangeForTimes = new ArrayList<>();
        buildVersionsWithTime.forEach((key, values) -> {
            leadChangeForTimes.add(LeadChangeForTime.builder().buildVersion(key).timeInMinutes(calculateTimeTakenToReachFinalEnvironment(values).intValue()).build());
        });
        return leadChangeForTimes;
    }

    private Long calculateTimeTakenToReachFinalEnvironment(List<OffsetDateTime> values) {
        // assumes a version has only ever been deployed once to an env
        return Math.abs(values.get(0).until(values.get(1), ChronoUnit.MINUTES));
    }

    private Map<String, List<OffsetDateTime>> getBuildVersionsWithTimeInMinutes(List<Metrics> metrics) {
        return metrics.stream().collect(Collectors.toMap(e -> e.buildVersion, e -> List.of(e.deployedAt),
                (oldValue, newValue) -> Stream.of(oldValue, newValue).flatMap(Collection::stream).collect(Collectors.toList())));
    }
}
