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

    public Metrics() {}

    public Metrics(String serviceName, Integer environment, Date deployedAt) {
        this.serviceName = serviceName;
        this.environment = environment;
        this.deployedAt = deployedAt;
    }
}
