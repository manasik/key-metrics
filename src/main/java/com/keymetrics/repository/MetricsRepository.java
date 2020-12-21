package com.keymetrics.repository;

import com.keymetrics.entity.Metrics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetricsRepository extends MongoRepository<Metrics, String> {

    public Metrics findByServiceNameOrderByDeploymentsDesc(String serviceName);

}

