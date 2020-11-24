package com.keymetrics.controller;

import com.keymetrics.domain.LeadChangeForTime;
import com.keymetrics.domain.Metrics;
import com.keymetrics.exception.MetricsNotFoundException;
import com.keymetrics.exception.NotDeployedToProductionException;
import com.keymetrics.service.DeploymentService;
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

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MetricsControllerTest {

    private MockMvc mockMvc;

    @Mock
    DeploymentService deploymentService;

    @BeforeEach
    void setup() {
        MetricsController controller = new MetricsController(deploymentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("lead time for change")
    class leadTime {

        @Test
        void shouldGetLeadChangeForTimeWhenAvailableForService() throws Exception {
            String serviceName = "blah";
            LeadChangeForTime leadChangeForTime = LeadChangeForTime.builder().timeInMinutes(230).buildVersion("b123").build();
            when(deploymentService.getLeadTimeForChange(serviceName)).thenReturn(List.of(leadChangeForTime));
            mockMvc.perform(get("/api/v1/metrics/leadChangeForTime/" + serviceName)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("[{\n" +
                            "  buildVersion: \"b123\",\n" +
                            "  timeInMinutes: 230\n" +
                            "}]"))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldReturn400WhenServiceNotDeployedToProdYet() throws Exception {
            String serviceName = "blah";

            when(deploymentService.getLeadTimeForChange(serviceName)).thenThrow(NotDeployedToProductionException.class);
            mockMvc.perform(get("/api/v1/metrics/leadChangeForTime/" + serviceName)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturn404WhenServiceNameNotFound() throws Exception {
            String serviceName = "blah";

            when(deploymentService.getLeadTimeForChange(serviceName)).thenThrow(MetricsNotFoundException.class);
            mockMvc.perform(get("/api/v1/metrics/leadChangeForTime/" + serviceName)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }
}
