package com.keymetrics.controller;

import com.keymetrics.service.DeploymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("api/v1/deploy")
@RequiredArgsConstructor
public class DeploymentController {

    private final DeploymentService deploymentService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping
    public void setDeploy(@RequestParam String serviceName, @Min(1) @Max(2) Integer environment, String buildVersion, Boolean buildPassed) {
        deploymentService.update(serviceName, environment, buildVersion, buildPassed);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/services")
    public List<String> getServicesDeployed() {
        return deploymentService.getServices();
    }
}
