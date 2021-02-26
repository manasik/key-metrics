package com.keymetrics.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class BuildInfo {
    public Integer environment;

    public OffsetDateTime deployedAt;

    public String buildVersion;

    public Boolean buildPassed;

    public BuildInfo(Integer environment, OffsetDateTime deployedAt, String buildVersion, Boolean buildPassed) {
        this.environment = environment;
        this.deployedAt = deployedAt;
        this.buildVersion = buildVersion;
        this.buildPassed = buildPassed;
    }
}
