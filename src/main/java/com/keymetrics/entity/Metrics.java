package com.keymetrics.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "deployments")
public class Metrics {

    @Id
    public String id;

    public String serviceName;

    public List<Deployment> deployments;

    public Metrics() {}

    public Metrics(String id, String serviceName, List<Deployment> deployments) {
        this.id = id;
        this.serviceName = serviceName;
        this.deployments = deployments;
    }
}
