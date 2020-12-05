package com.keymetrics.controller;

import com.keymetrics.domain.LeadChangeForTime;
import com.keymetrics.service.DeploymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final DeploymentService deploymentService;

    @RequestMapping(value = "/lead-time-for-change/{serviceName}", method = RequestMethod.GET)
    public List<LeadChangeForTime> leadTimeForChange(@PathVariable String serviceName) {
        return deploymentService.getLeadTimeForChange(serviceName);
    }
}
