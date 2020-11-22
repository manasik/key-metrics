package com.keymetrics.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class Metrics {

    @Id
    public String id;

    public String serviceName;

    public Integer environment;

    public Date deployedAt;

    public String buildVersion;

    public Metrics() {}

    public Metrics(String id, String serviceName, Integer environment, Date deployedAt, String buildVersion) {
        this.id = id;
        this.serviceName = serviceName;
        this.environment = environment;
        this.deployedAt = deployedAt;
        this.buildVersion = buildVersion;
    }
}
