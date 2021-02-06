package com.keymetrics.controller;

import com.keymetrics.domain.Deployment;
import com.keymetrics.domain.LeadTimeForChange;
import com.keymetrics.domain.Metrics;
import com.keymetrics.service.DeploymentService;
import com.keymetrics.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping(params = {"serviceName"})
    public Metrics metrics(@RequestParam("serviceName") String serviceName) {
        return metricsService.getMetrics(serviceName);
    }
}
