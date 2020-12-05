package com.keymetrics.service;

import com.keymetrics.domain.LeadChangeForTime;
import com.keymetrics.entity.Metrics;
import com.keymetrics.repository.MetricsRepository;
import org.assertj.core.data.TemporalOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeploymentServiceTest {

    @Mock
    MetricsRepository metricsRepository;

    @InjectMocks
    DeploymentService service;

    String name = "blah";
    Integer environment = 1;
    String buildVersion = "blah";

    @Captor
    private ArgumentCaptor<Metrics> metricsArgumentCaptor;

    @Nested
    @DisplayName("insert metric")
    class insertMetric {

        @Test
        void shouldSaveMetricWithDeployedTime() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();

            service.update(name, environment, buildVersion);

            verify(metricsRepository).save(metricsArgumentCaptor.capture());

            Metrics savedMetrics = metricsArgumentCaptor.getValue();

            assertThat(savedMetrics.serviceName).isEqualTo(name);
            assertThat(savedMetrics.environment).isEqualTo(environment);
            assertThat(savedMetrics.buildVersion).isEqualTo(buildVersion);
            assertThat(savedMetrics.deployedAt).isCloseTo(now, within(1000, ChronoUnit.MILLIS));
        }

    }

    @Nested
    @DisplayName("lead time for change")
    class leadTimeForChange {

        @Test
        void shouldGetLeadTimeForChangeWhenDataExists() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime twoHoursAgo = OffsetDateTime.now().minusHours(2);
            String serviceName = "blah";
            String b123 = "b123";
            Metrics metrics1 = new Metrics("1234", serviceName, 1, twoHoursAgo, b123);
            Metrics metrics2 = new Metrics("1234", serviceName, 2, now, b123);

            LeadChangeForTime leadChangeForTime = LeadChangeForTime.builder().buildVersion(b123).timeInMinutes(119).build();

            when(metricsRepository.findByServiceNameOOrderByDeployedAtDesc(serviceName)).thenReturn(List.of(metrics2, metrics1));

            List<LeadChangeForTime> result = service.getLeadTimeForChange(serviceName);

            assertThat(result).contains(leadChangeForTime);
        }

    }

}
