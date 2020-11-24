package com.keymetrics.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotDeployedToProductionException extends RuntimeException {
    public NotDeployedToProductionException(String serviceName) {
        super(String.format("No deployment to Production environment found for service: %s", serviceName));
    }
}
