package com.keymetrics.service;

import com.keymetrics.entity.BuildInfo;
import com.keymetrics.entity.Deployment;
import com.keymetrics.repository.DeploymentRepository;
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

    private final DeploymentRepository deploymentRepository;

    public void update(String name, Integer environment, String buildVersion, Boolean buildPassed) {
        Deployment existingDeploymentsForService = deploymentRepository.findByServiceNameOrderByBuildInfoDesc(name);

        if (existingDeploymentsForService == null) {
            String id = UUID.randomUUID().toString();
            BuildInfo buildInfo = new BuildInfo(environment, OffsetDateTime.now(), buildVersion, buildPassed);
            Deployment deployment = new Deployment(id, name, List.of(buildInfo));
            deploymentRepository.save(deployment);
        } else {
            BuildInfo buildInfo = new BuildInfo(environment, OffsetDateTime.now(), buildVersion, buildPassed);
            ArrayList<BuildInfo> existingBuilds = new ArrayList<>(existingDeploymentsForService.buildInfo);
            existingBuilds.add(0, buildInfo);
            existingDeploymentsForService.setBuildInfo(existingBuilds);
            deploymentRepository.save(existingDeploymentsForService);
        }
    }

    public List<String> getServices() {
        return deploymentRepository.findAll().stream().map(Deployment::getServiceName).collect(Collectors.toList());
    }
}
