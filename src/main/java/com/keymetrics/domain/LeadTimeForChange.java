package com.keymetrics.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class LeadTimeForChange {
    private String month;
    private Double numberOfDays;
}
