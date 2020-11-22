package com.keymetrics.controller;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        void shouldSaveDeploymentUpdateAndReturn200OKWhenEnvSetTo1() throws Exception {
            when(deploymentService)
            String name = "blah";
            mockMvc.perform(get("/api/v1/metrics/lead-change/" + name)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(deploymentService).update(eq(name), eq(environment));
        }
    }

}
