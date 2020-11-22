package com.keymetrics.controller;

import com.keymetrics.service.DeploymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("api/v1/deploy")
@RequiredArgsConstructor
public class DeploymentController {

    private final DeploymentService deploymentService;

    @RequestMapping("/blah")
    public String hello() {
        return "HELLO WORLD";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping
    public void setDeploy(@RequestParam String serviceName, @Min(1) @Max(2) Integer environment, String buildVersion) {
        deploymentService.update(serviceName, environment, buildVersion);
    }
}
