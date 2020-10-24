package com.keymetrics.repository;

import com.keymetrics.entity.Metrics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricsRepository extends MongoRepository<Metrics, String> {

}
