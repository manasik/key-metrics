package com.keymetrics.controller;

import com.keymetrics.service.DeploymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("api/v1/deploy")
@RequiredArgsConstructor
public class DeploymentController {

    private final DeploymentService deploymentService;

    @Autowired
    MongoTemplate mongoTemplate;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping
    public void setDeploy(@RequestParam String serviceName, @Min(1) @Max(2) Integer environment) {
        deploymentService.update(serviceName, environment);
    }
}
