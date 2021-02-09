package com.keymetrics.controller;

import com.keymetrics.domain.Deployment;
import com.keymetrics.domain.LeadTimeForChange;
import com.keymetrics.domain.Metrics;
import com.keymetrics.exception.MetricsNotFoundException;
import com.keymetrics.exception.NotDeployedToProductionException;
import com.keymetrics.service.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MetricsControllerTest {

    private MockMvc mockMvc;

    @Mock
    MetricsService metricsService;

    @BeforeEach
    void setup() {
        MetricsController controller = new MetricsController(metricsService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("metrics")
    class metrics {

        @Test
        void shouldGetMetricsForAService() throws Exception {
            String serviceName = "blah";
            String buildVersion = "b123";
            LeadTimeForChange leadTimeForChange = LeadTimeForChange.builder().timeInMinutes(230).buildVersion(buildVersion).build();
            LocalDate deployedAt = LocalDate.of(2021, 2, 6);
            Deployment deployment = Deployment.builder().buildVersion(buildVersion).deployedAt(deployedAt).build();
            Metrics metrics = Metrics.builder().serviceName(serviceName).deployments(List.of(deployment)).leadTimeForChange(List.of(leadTimeForChange)).build();
            when(metricsService.getMetrics(serviceName)).thenReturn(metrics);

            String contentAsString = mockMvc.perform(get("/api/v1/metrics?serviceName=" + serviceName)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

            assertThat(contentAsString).isEqualTo("{\"serviceName\":\"blah\",\"leadTimeForChange\":[{\"buildVersion\":\"b123\",\"timeInMinutes\":230}],\"deployments\":[{\"deployedAt\":[2021,2,6],\"buildVersion\":\"b123\"}]}");
        }

        @Test
        void shouldReturn400WhenServiceNotDeployedToProdYet() throws Exception {
            String serviceName = "blah";

            when(metricsService.getMetrics(serviceName)).thenThrow(NotDeployedToProductionException.class);
            mockMvc.perform(get("/api/v1/metrics?serviceName=" + serviceName)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn404WhenServiceNameNotFound() throws Exception {
            String serviceName = "blah";

            when(metricsService.getMetrics(serviceName)).thenThrow(MetricsNotFoundException.class);
            mockMvc.perform(get("/api/v1/metrics?serviceName=" + serviceName)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturn200WhenServiceHasOnly1deployment() throws Exception {
            String serviceName = "blah";
            String buildVersion = "b123";
            LocalDate deployedAt = LocalDate.of(2021, 2, 6);
            Deployment deployment = Deployment.builder().buildVersion(buildVersion).deployedAt(deployedAt).build();
            Metrics metrics = Metrics.builder().serviceName(serviceName).deployments(List.of(deployment)).leadTimeForChange(Collections.emptyList()).build();
            when(metricsService.getMetrics(serviceName)).thenReturn(metrics);

            String contentAsString = mockMvc.perform(get("/api/v1/metrics?serviceName=" + serviceName)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

            assertThat(contentAsString).isEqualTo("{\"serviceName\":\"blah\",\"leadTimeForChange\":[],\"deployments\":[{\"deployedAt\":[2021,2,6],\"buildVersion\":\"b123\"}]}");

        }
    }

}
