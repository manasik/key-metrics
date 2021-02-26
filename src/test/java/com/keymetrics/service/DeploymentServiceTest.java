package com.keymetrics.service;

import com.keymetrics.entity.BuildInfo;
import com.keymetrics.entity.Deployment;
import com.keymetrics.repository.DeploymentRepository;
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
    DeploymentRepository deploymentRepository;

    @InjectMocks
    DeploymentService service;

    String name = "blah";
    Integer environment = 1;
    String buildVersion = "blah";

    @Captor
    private ArgumentCaptor<Deployment> metricsArgumentCaptor;
    private Boolean buildPassed = true;

    @Nested
    @DisplayName("insert deployment")
    class insertDeployment {

        @Test
        void shouldSaveMetricWithDeployedTimeWhenNoDeploymentForBuildAndEnvExists() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();

            service.update(name, environment, buildVersion, buildPassed);

            verify(deploymentRepository).save(metricsArgumentCaptor.capture());

            Deployment savedDeployment = metricsArgumentCaptor.getValue();

            assertThat(savedDeployment.serviceName).isEqualTo(name);
            assertThat(savedDeployment.buildInfo.get(0).environment).isEqualTo(environment);
            assertThat(savedDeployment.buildInfo.get(0).buildVersion).isEqualTo(buildVersion);
            assertThat(savedDeployment.buildInfo.get(0).buildPassed).isEqualTo(buildPassed);
            assertThat(savedDeployment.buildInfo.get(0).deployedAt).isCloseTo(now, within(1000, ChronoUnit.MILLIS));
        }

        @Test
        void shouldBeAbleToSaveDeploymentForAServiceWhenDeploymentExistForTheService() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();
            BuildInfo deployment1 = new BuildInfo(1, now.minusHours(2), buildVersion, buildPassed);
            Deployment deployment = new Deployment(buildVersion, name, List.of(deployment1));
            when(deploymentRepository.findByServiceNameOrderByBuildInfoDesc(name)).thenReturn(deployment);

            service.update(name, 2, buildVersion, buildPassed);

            verify(deploymentRepository).save(metricsArgumentCaptor.capture());

            Deployment savedDeployment = metricsArgumentCaptor.getValue();

            assertThat(savedDeployment.serviceName).isEqualTo(name);
            assertThat(savedDeployment.buildInfo.size()).isEqualTo(2);
            assertThat(savedDeployment.buildInfo.get(0).environment).isEqualTo(2);
            assertThat(savedDeployment.buildInfo.get(0).buildVersion).isEqualTo(buildVersion);
            assertThat(savedDeployment.buildInfo.get(0).buildPassed).isEqualTo(buildPassed);
            assertThat(savedDeployment.buildInfo.get(0).deployedAt).isCloseTo(now, within(1000, ChronoUnit.MILLIS));
        }

    }

    @Nested
    @DisplayName("get services")
    class getServices {

        @Test
        void shouldGetServicesWhenPresent() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();
            BuildInfo deployment1 = new BuildInfo(1, now.minusHours(2), buildVersion, false);
            Deployment metrics1 = new Deployment(buildVersion, name, List.of(deployment1));
            Deployment deployment2 = new Deployment(buildVersion, "some name", List.of(deployment1));
            when(deploymentRepository.findAll()).thenReturn(List.of(deployment2, metrics1));

            List<String> services = service.getServices();

            assertThat(services).isEqualTo(List.of(deployment2.serviceName, metrics1.serviceName));
        }

        @Test
        void shouldEmptyListWhenNoServices() throws Exception {
            when(deploymentRepository.findAll()).thenReturn(Collections.emptyList());

            List<String> services = service.getServices();

            assertThat(services).isEqualTo(Collections.emptyList());
        }
    }
}
