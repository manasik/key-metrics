package com.keymetrics.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Getter
@Setter
@Document(collection = "deployments")
public class Deployment {
    @Id
    public String id;

    public Integer environment;

    public OffsetDateTime deployedAt;

    public String buildVersion;

    public Deployment() {
    }

    public Deployment(String id, Integer environment, OffsetDateTime deployedAt, String buildVersion) {
        this.id = id;
        this.environment = environment;
        this.deployedAt = deployedAt;
        this.buildVersion = buildVersion;
    }
}
