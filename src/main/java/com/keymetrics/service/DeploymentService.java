package com.keymetrics.service;

import com.keymetrics.domain.LeadChangeForTime;
import com.keymetrics.entity.Metrics;
import com.keymetrics.repository.MetricsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableMongoRepositories(basePackages = "com.keymetrics.repository")
@Slf4j
public class DeploymentService {

    private final MetricsRepository metricsRepository;

    public void update(String name, Integer environment, String buildVersion) {
        String id = UUID.randomUUID().toString();
        Metrics metrics = new Metrics(id, name, environment, new Date(), buildVersion);
        metricsRepository.save(metrics);
    }

    public List<LeadChangeForTime> getLeadTimeForChange(String serviceName) {
        return null;
    }
}
