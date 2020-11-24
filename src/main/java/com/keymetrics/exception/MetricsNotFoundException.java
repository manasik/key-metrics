package com.keymetrics.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MetricsNotFoundException extends RuntimeException {
    public MetricsNotFoundException(String serviceName) {
        super(String.format("No metrics found for %s", serviceName));
    }
}
