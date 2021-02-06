package com.keymetrics.domain;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Metrics {
    private String serviceName;
    private List<LeadTimeForChange> leadTimeForChange;
    private List<Deployment> deployments;
}
