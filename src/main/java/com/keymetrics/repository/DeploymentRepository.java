package com.keymetrics.repository;

import com.keymetrics.entity.Deployment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentRepository extends MongoRepository<Deployment, String> {

    public Deployment findByServiceNameOrderByBuildInfoDesc(String serviceName);

}

