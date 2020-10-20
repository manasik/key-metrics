package com.keymetrics.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Metrics {

    @Id
    public String id;

    public String serviceName;

    public Metrics() {}

    public Metrics(String serviceName) {
        this.serviceName = serviceName;
    }
}
