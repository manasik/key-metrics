package com.keymetrics.entity;

import java.time.OffsetDateTime;

public class Deployment {
    public Integer environment;

    public OffsetDateTime deployedAt;

    public String buildVersion;

    public Deployment(Integer environment, OffsetDateTime deployedAt, String buildVersion) {
        this.environment = environment;
        this.deployedAt = deployedAt;
        this.buildVersion = buildVersion;
    }
}
