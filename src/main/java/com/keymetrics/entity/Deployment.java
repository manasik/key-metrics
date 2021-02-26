package com.keymetrics.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "deployments")
public class Deployment {

    @Id
    public String id;

    public String serviceName;

    public List<BuildInfo> buildInfo;

    public Deployment() {}

    public Deployment(String id, String serviceName, List<BuildInfo> buildInfo) {
        this.id = id;
        this.serviceName = serviceName;
        this.buildInfo = buildInfo;
    }
}
