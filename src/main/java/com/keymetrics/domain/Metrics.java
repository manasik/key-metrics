package com.keymetrics.domain;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Metrics {
    private String serviceName;
    private Integer environment;
    private Date deployedAt;
    private String buildVersion;
}
