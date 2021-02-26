package com.keymetrics.service;

import com.keymetrics.domain.LeadTimeForChange;
import com.keymetrics.entity.BuildInfo;
import com.keymetrics.entity.Deployment;
import com.keymetrics.exception.MetricsNotFoundException;
import com.keymetrics.repository.DeploymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricsServiceTest {

    @Mock
    DeploymentRepository deploymentRepository;

    @InjectMocks
    MetricsService service;

    Boolean buildPassed = true;

    @Nested
    @DisplayName("lead time for change")
    class leadTimeForChange {

        @Test
        void shouldGetLeadTimeForChangeWhen2DeploymentsExistForABuild() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime twoDaysAgo = OffsetDateTime.now().minusHours(49);
            String serviceName = "blah";
            String b123 = "b123";
            BuildInfo deployment1 = new BuildInfo(1, twoDaysAgo, b123, buildPassed);
            BuildInfo deployment2 = new BuildInfo( 2, now, b123, buildPassed);
            Deployment deployment = new Deployment("1234", serviceName, List.of(deployment2, deployment1));

            LeadTimeForChange leadTimeForChange = LeadTimeForChange.builder().month(now.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                    .numberOfDays(2.0).build();

            when(deploymentRepository.findByServiceNameOrderByBuildInfoDesc(serviceName)).thenReturn(deployment);

            List<LeadTimeForChange> result = service.getMetrics(serviceName).getLeadTimeForChange();

            assertThat(result).contains(leadTimeForChange);
        }

        @Test
        void shouldSkipLeadTimeForChangeWhen1DeploymentsExistForABuildInEnv1ButNotForEnv2() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime twoHoursAgo = OffsetDateTime.now().minusHours(2);
            String serviceName = "blah";
            String buildVersion1 = "b123";
            String buildVersion2 = "b123";
            String buildVersion3 = "b234";
            BuildInfo deployment1 = new BuildInfo(1, twoHoursAgo, buildVersion1, buildPassed);
            BuildInfo deployment2 = new BuildInfo( 2, now, buildVersion2, buildPassed);
            BuildInfo deployment3 = new BuildInfo( 1, now, buildVersion3, buildPassed);
            Deployment deployment = new Deployment("1234", serviceName, List.of(deployment3, deployment2, deployment1));

            LeadTimeForChange leadTimeForChange = LeadTimeForChange.builder().month(now.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                    .numberOfDays(0.0).build();

            when(deploymentRepository.findByServiceNameOrderByBuildInfoDesc(serviceName)).thenReturn(deployment);

            List<LeadTimeForChange> result = service.getMetrics(serviceName).getLeadTimeForChange();

            assertThat(result.size()).isEqualTo(1);
            assertThat(result).contains(leadTimeForChange);
        }

        @Test
        void shouldSkipLeadTimeForChangeWhenRedeployedToEnv1AfterBeingDeployedToEnv2AndMissingEquivalentDeploymehtToEnv2() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime twoHoursAgo = OffsetDateTime.now().minusHours(2);
            String serviceName = "blah";
            String buildVersion1 = "b123";
            BuildInfo deployment1 = new BuildInfo(1, twoHoursAgo, buildVersion1, buildPassed);
            BuildInfo deployment2 = new BuildInfo( 2, now.minusHours(1), buildVersion1, buildPassed);
            BuildInfo deployment3 = new BuildInfo( 1, now, buildVersion1, buildPassed);
            Deployment deployment = new Deployment("1234", serviceName, List.of(deployment3, deployment2, deployment1));

            when(deploymentRepository.findByServiceNameOrderByBuildInfoDesc(serviceName)).thenReturn(deployment);

            List<LeadTimeForChange> result = service.getMetrics(serviceName).getLeadTimeForChange();

            assertThat(result.size()).isEqualTo(0);
        }


        @Test
        void shouldUseLatestBuildFromEachEnvToCalculateLeadTime() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime twoHoursAgo = OffsetDateTime.now().minusHours(2);
            String serviceName = "blah";
            String buildVersion1 = "b123";
            String buildVersion2 = "b123";
            String buildVersion3 = "b234";
            BuildInfo deployment1 = new BuildInfo(1, twoHoursAgo, buildVersion1, buildPassed);
            BuildInfo deployment2 = new BuildInfo( 2, now, buildVersion2, buildPassed);
            BuildInfo deployment3 = new BuildInfo( 1, now, buildVersion3, buildPassed);
            BuildInfo deployment4 = new BuildInfo( 2, now.minusHours(1), buildVersion2, buildPassed);
            Deployment deployment = new Deployment("1234", serviceName, List.of(deployment4, deployment3, deployment2, deployment1));

            when(deploymentRepository.findByServiceNameOrderByBuildInfoDesc(serviceName)).thenReturn(deployment);

            List<LeadTimeForChange> result = service.getMetrics(serviceName).getLeadTimeForChange();

            assertThat(result.size()).isEqualTo(1);
            assertThat(result.get(0).getNumberOfDays());
        }

        @Test
        void shouldNotSkipBuildVersionsWhenMultipleDeploymentsExistForEnv1ButOnlyOneDeploymentForProd() throws Exception {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime twoHoursAgo = OffsetDateTime.now().minusHours(2);
            String serviceName = "blah";
            String buildVersion1 = "b123";
            BuildInfo deployment1 = new BuildInfo(1, twoHoursAgo, buildVersion1, buildPassed);
            BuildInfo deployment2 = new BuildInfo( 1, now.minusHours(1), buildVersion1, buildPassed);
            BuildInfo deployment3 = new BuildInfo( 2, now, buildVersion1, buildPassed);
            Deployment deployment = new Deployment("1234", serviceName, List.of(deployment3, deployment2, deployment1));

            when(deploymentRepository.findByServiceNameOrderByBuildInfoDesc(serviceName)).thenReturn(deployment);

            List<LeadTimeForChange> result = service.getMetrics(serviceName).getLeadTimeForChange();

            assertThat(result.size()).isEqualTo(1);
            assertThat(result.get(0).getNumberOfDays());
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
            BuildInfo deployment1 = new BuildInfo(1, twoHoursAgo, b123, false);
            BuildInfo deployment2 = new BuildInfo( 2, now, b123, true);
            Deployment deployment = new Deployment("1234", serviceName, List.of(deployment2, deployment1));

            when(deploymentRepository.findByServiceNameOrderByBuildInfoDesc(serviceName)).thenReturn(deployment);

            List<com.keymetrics.domain.Deployment> deployments = service.getMetrics(serviceName).getDeployments();

            assertThat(deployments.size()).isEqualTo(2);
            assertThat(deployments.get(0)).isEqualTo(com.keymetrics.domain.Deployment.builder().buildVersion(b123).deployedAt(now.toLocalDate()).build());
        }

        @Test
        void shouldThrowExceptionWhenNoDeployments() {
            String serviceName = "blah";

            when(deploymentRepository.findByServiceNameOrderByBuildInfoDesc(serviceName)).thenReturn(null);

            assertThrows(MetricsNotFoundException.class, () -> {service.getMetrics(serviceName);});
        }
    }

}
