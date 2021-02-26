package com.keymetrics.controller;

import com.keymetrics.domain.Metrics;
import com.keymetrics.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("api/v1/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(params = {"serviceName"})
    public Metrics metrics(@RequestParam("serviceName") String serviceName) {
        return metricsService.getMetrics(serviceName);
    }
}
