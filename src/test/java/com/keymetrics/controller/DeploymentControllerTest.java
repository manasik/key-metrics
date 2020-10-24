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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DeploymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    DeploymentService deploymentService;

    @BeforeEach
    void setup() {
        DeploymentController controller = new DeploymentController(deploymentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("deploy")
    class CreateRequest {

        String name = "blah";
        Integer environment = 1;

        @Test
        void shouldSaveDeploymentUpdateAndReturn200OKWhenEnvSetTo1() throws Exception {
            mockMvc.perform(post("/api/v1/deploy" + "?serviceName=" + name + "&environment=" + environment)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(deploymentService).update(eq(name), eq(environment));
        }

        @Test
        void shouldSaveDeploymentUpdateAndReturn200OKWhenEnvSetTo2() throws Exception {
            mockMvc.perform(post("/api/v1/deploy" + "?serviceName=" + name + "&environment=2")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(deploymentService).update(eq(name), eq(2));
        }

        @Test
        void shouldNotSaveDeploymentUpdateAndReturn400WhenEnvironmentIsNot1Or2() throws Exception {
            mockMvc.perform(post("/api/v1/deploy" + "?serviceName=" + name + "&environment=blah")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(deploymentService, never()).update(eq(name), eq(environment));
        }

//        @Test
//        void shouldReturn500WhenUnableToSave() throws Exception {
//            mockMvc.perform(post("/api/v1/deploy" + "?serviceName=" + name + "&environment=" + environment)
//                    .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isInternalServerError());
//
//            doThrow(new Exception()).when(deploymentService).update(name, environment);
//        }
    }
}
