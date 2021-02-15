package com.keymetrics.entity;

import java.time.OffsetDateTime;

public class Deployment {
    public Integer environment;

    public OffsetDateTime deployedAt;

    public String buildVersion;

    public Boolean buildPassed;

    public Deployment(Integer environment, OffsetDateTime deployedAt, String buildVersion, Boolean buildPassed) {
        this.environment = environment;
        this.deployedAt = deployedAt;
        this.buildVersion = buildVersion;
        this.buildPassed = buildPassed;
    }
}
