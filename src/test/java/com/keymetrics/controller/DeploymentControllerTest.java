package com.keymetrics.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @DisplayName("create")
    class CreateRequest {

        String name = "blah";
        Integer environment = 1;
        private String buildVersion = "b123";

        @Test
        void shouldSaveDeploymentUpdateAndReturn200OKWhenEnvSetTo1() throws Exception {
            mockMvc.perform(post("/api/v1/deploy" + "?serviceName=" + name + "&environment=" + environment
                    + "&buildVersion=" + buildVersion + "&buildPassed=" + true)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(deploymentService).update(eq(name), eq(environment), eq(buildVersion), eq(true));
        }

        @Test
        void shouldSaveDeploymentUpdateAndReturn200OKWhenEnvSetTo2() throws Exception {
            mockMvc.perform(post("/api/v1/deploy" + "?serviceName=" + name + "&environment=2"
                    + "&buildVersion=" + buildVersion + "&buildPassed=" + false)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(deploymentService).update(eq(name), eq(2), eq(buildVersion), eq(false));
        }

        @Test
        void shouldNotSaveDeploymentUpdateAndReturn400WhenEnvironmentIsNot1Or2() throws Exception {
            mockMvc.perform(post("/api/v1/deploy" + "?serviceName=" + name + "&environment=blah"
                    + "&buildVersion=" + buildVersion + "&buildPassed=" + true)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(deploymentService, never()).update(eq(name), eq(environment), eq(buildVersion), eq(true));
        }

//        @Test
//        void shouldNotSaveDeploymentUpdateAndReturn400WhenEnvironmentIsNotAnAcceptedInt() throws Exception {
//            mockMvc.perform(post("/api/v1/deploy" + "?environment=5" + "&serviceName=" + name )
//                    .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isBadRequest());
//
//            verify(deploymentService, never()).update(eq(name), eq(environment));
//        }

//        @Test
//        void shouldReturn500WhenUnableToSave() throws Exception {
//            mockMvc.perform(post("/api/v1/deploy" + "?serviceName=" + name + "&environment=" + environment)
//                    .contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isInternalServerError());
//
//            doThrow(new Exception()).when(deploymentService).update(name, environment);
//        }
    }

    @Nested
    @DisplayName("get applications")
    class GetApplications {

        @Test
        void shouldGetListOfUniqueServicesDeployed() throws Exception {
            List<String> services = List.of("foo", "bar");
            when(deploymentService.getServices()).thenReturn(services);

            String response = mockMvc.perform(get("/api/v1/deploy/services")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

            List<String> result = new ObjectMapper().readValue(response, new TypeReference<>() {});

            assertThat(result).isEqualTo(services);
        }
    }

}
