package com.keymetrics.service;

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
import java.util.Collections;
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
    private Boolean buildPassed = true;

    @Nested
    @DisplayName("insert deployment")
    class insertDeployment {

        @Test
        void shouldSaveMetricWithDeployedTimeWhenNoDeploymentForBuildAndEnvExists() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();

            service.update(name, environment, buildVersion, buildPassed);

            verify(metricsRepository).save(metricsArgumentCaptor.capture());

            Metrics savedMetrics = metricsArgumentCaptor.getValue();

            assertThat(savedMetrics.serviceName).isEqualTo(name);
            assertThat(savedMetrics.deployments.get(0).environment).isEqualTo(environment);
            assertThat(savedMetrics.deployments.get(0).buildVersion).isEqualTo(buildVersion);
            assertThat(savedMetrics.deployments.get(0).buildPassed).isEqualTo(buildPassed);
            assertThat(savedMetrics.deployments.get(0).deployedAt).isCloseTo(now, within(1000, ChronoUnit.MILLIS));
        }

        @Test
        void shouldBeAbleToSaveDeploymentForAServiceWhenDeploymentExistForTheService() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();
            Deployment deployment1 = new Deployment(1, now.minusHours(2), buildVersion, buildPassed);
            Metrics metrics = new Metrics(buildVersion, name, List.of(deployment1));
            when(metricsRepository.findByServiceNameOrderByDeploymentsDesc(name)).thenReturn(metrics);

            service.update(name, 2, buildVersion, buildPassed);

            verify(metricsRepository).save(metricsArgumentCaptor.capture());

            Metrics savedMetrics = metricsArgumentCaptor.getValue();

            assertThat(savedMetrics.serviceName).isEqualTo(name);
            assertThat(savedMetrics.deployments.size()).isEqualTo(2);
            assertThat(savedMetrics.deployments.get(0).environment).isEqualTo(2);
            assertThat(savedMetrics.deployments.get(0).buildVersion).isEqualTo(buildVersion);
            assertThat(savedMetrics.deployments.get(0).buildPassed).isEqualTo(buildPassed);
            assertThat(savedMetrics.deployments.get(0).deployedAt).isCloseTo(now, within(1000, ChronoUnit.MILLIS));
        }

    }

    @Nested
    @DisplayName("get services")
    class getServices {

        @Test
        void shouldGetServicesWhenPresent() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();
            Deployment deployment1 = new Deployment(1, now.minusHours(2), buildVersion, false);
            Metrics metrics1 = new Metrics(buildVersion, name, List.of(deployment1));
            Metrics metrics2 = new Metrics(buildVersion, "some name", List.of(deployment1));
            when(metricsRepository.findAll()).thenReturn(List.of(metrics2, metrics1));

            List<String> services = service.getServices();

            assertThat(services).isEqualTo(List.of(metrics2.serviceName, metrics1.serviceName));
        }

        @Test
        void shouldEmptyListWhenNoServices() throws Exception {
            when(metricsRepository.findAll()).thenReturn(Collections.emptyList());

            List<String> services = service.getServices();

            assertThat(services).isEqualTo(Collections.emptyList());
        }
    }
}
