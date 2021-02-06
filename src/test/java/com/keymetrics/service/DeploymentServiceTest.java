package com.keymetrics.service;

import com.keymetrics.domain.LeadTimeForChange;
import com.keymetrics.entity.Deployment;
import com.keymetrics.entity.Metrics;
import com.keymetrics.repository.MetricsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
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
    @DisplayName("insert deployment")
    class insertDeployment {

        @Test
        void shouldSaveMetricWithDeployedTimeWhenNoDeploymentForBuildAndEnvExists() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();

            service.update(name, environment, buildVersion);

            verify(metricsRepository).save(metricsArgumentCaptor.capture());

            Metrics savedMetrics = metricsArgumentCaptor.getValue();

            assertThat(savedMetrics.serviceName).isEqualTo(name);
            assertThat(savedMetrics.deployments.get(0).environment).isEqualTo(environment);
            assertThat(savedMetrics.deployments.get(0).buildVersion).isEqualTo(buildVersion);
            assertThat(savedMetrics.deployments.get(0).deployedAt).isCloseTo(now, within(1000, ChronoUnit.MILLIS));
        }

        @Test
        void shouldBeAbleToSaveDeploymentForAServiceWhenDeploymentExistForTheService() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();
            Deployment deployment1 = new Deployment(1, now.minusHours(2), buildVersion);
            Metrics metrics = new Metrics(buildVersion, name, List.of(deployment1));
            when(metricsRepository.findByServiceNameOrderByDeploymentsDesc(name)).thenReturn(metrics);

            service.update(name, 2, buildVersion);

            verify(metricsRepository).save(metricsArgumentCaptor.capture());

            Metrics savedMetrics = metricsArgumentCaptor.getValue();

            assertThat(savedMetrics.serviceName).isEqualTo(name);
            assertThat(savedMetrics.deployments.size()).isEqualTo(2);
            assertThat(savedMetrics.deployments.get(0).environment).isEqualTo(2);
            assertThat(savedMetrics.deployments.get(0).buildVersion).isEqualTo(buildVersion);
            assertThat(savedMetrics.deployments.get(0).deployedAt).isCloseTo(now, within(1000, ChronoUnit.MILLIS));
        }

    }
}
