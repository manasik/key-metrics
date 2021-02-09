package com.keymetrics.service;

import com.keymetrics.domain.LeadTimeForChange;
import com.keymetrics.entity.Deployment;
import com.keymetrics.entity.Metrics;
import com.keymetrics.repository.MetricsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricsServiceTest {

    @Mock
    MetricsRepository metricsRepository;

    @InjectMocks
    MetricsService service;

    @Nested
    @DisplayName("lead time for change")
    class leadTimeForChange {

        @Test
        void shouldGetLeadTimeForChangeWhen2DeploymentsExistForABuild() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime twoHoursAgo = OffsetDateTime.now().minusHours(2);
            String serviceName = "blah";
            String b123 = "b123";
            Deployment deployment1 = new Deployment("deploymentGuid1",1, twoHoursAgo, b123);
            Deployment deployment2 = new Deployment( "deploymentGuid2", 2, now, b123);
            Metrics metrics = new Metrics("1234", serviceName, List.of(deployment2, deployment1));

            LeadTimeForChange leadTimeForChange = LeadTimeForChange.builder().buildVersion(b123).timeInMinutes(119).build();

            when(metricsRepository.findByServiceNameOrderByDeploymentsDesc(serviceName)).thenReturn(metrics);

            List<LeadTimeForChange> result = service.getMetrics(serviceName).getLeadTimeForChange();

            assertThat(result).contains(leadTimeForChange);
        }

        @Test
        void shouldSkipLeadTimeForChangeWhen1DeploymentsExistForABuild() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime twoHoursAgo = OffsetDateTime.now().minusHours(2);
            String serviceName = "blah";
            String buildVersion1 = "b123";
            String buildVersion2 = "b123";
            String buildVersion3 = "b234";
            Deployment deployment1 = new Deployment("deploymentGuid1", 1, twoHoursAgo, buildVersion1);
            Deployment deployment2 = new Deployment( "deploymentGuid2", 2, now, buildVersion2);
            Deployment deployment3 = new Deployment( "deploymentGuid3", 1, now, buildVersion3);
            Metrics metrics = new Metrics("1234", serviceName, List.of(deployment3, deployment2, deployment1));

            LeadTimeForChange leadTimeForChange = LeadTimeForChange.builder().buildVersion(buildVersion2).timeInMinutes(119).build();

            when(metricsRepository.findByServiceNameOrderByDeploymentsDesc(serviceName)).thenReturn(metrics);

            List<LeadTimeForChange> result = service.getMetrics(serviceName).getLeadTimeForChange();

            assertThat(result.size()).isEqualTo(1);
            assertThat(result).contains(leadTimeForChange);
        }

        @Test
        void shouldSkipBuildVersionsWhenMultipleDeploymentsExistForAnEnv() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime twoHoursAgo = OffsetDateTime.now().minusHours(2);
            String serviceName = "blah";
            String buildVersion1 = "b123";
            String buildVersion2 = "b123";
            String buildVersion3 = "b234";
            Deployment deployment1 = new Deployment("deploymentGuid1", 1, twoHoursAgo, buildVersion1);
            Deployment deployment2 = new Deployment("deploymentGuid2", 2, now, buildVersion2);
            Deployment deployment3 = new Deployment("deploymentGuid3", 1, now, buildVersion3);
            Deployment deployment4 = new Deployment("deploymentGuid4", 2, now.minusHours(1), buildVersion2);
            Metrics metrics = new Metrics("1234", serviceName, List.of(deployment4, deployment3, deployment2, deployment1));

            when(metricsRepository.findByServiceNameOrderByDeploymentsDesc(serviceName)).thenReturn(metrics);

            List<LeadTimeForChange> result = service.getMetrics(serviceName).getLeadTimeForChange();

            assertThat(result.size()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("deployments")
    class deployments {
        @Test
        void shouldGetDeploymentsForAService() {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime twoHoursAgo = OffsetDateTime.now().minusHours(2);
            String serviceName = "blah";
            String b123 = "b123";
            String docId1 = "2345";
            String docId2 = "245";
            Deployment deployment1 = new Deployment(docId1, 1, twoHoursAgo, b123);
            Deployment deployment2 = new Deployment(docId2, 2, now, b123);
            Metrics metrics = new Metrics("1234", serviceName, List.of(deployment2, deployment1));

            when(metricsRepository.findByServiceNameOrderByDeploymentsDesc(serviceName)).thenReturn(metrics);

            List<com.keymetrics.domain.Deployment> deployments = service.getMetrics(serviceName).getDeployments();

            assertThat(deployments.size()).isEqualTo(2);
            assertThat(deployments.get(0)).isEqualTo(com.keymetrics.domain.Deployment.builder().buildVersion(b123).deployedAt(now.toLocalDate()).build());

        }
    }

}
