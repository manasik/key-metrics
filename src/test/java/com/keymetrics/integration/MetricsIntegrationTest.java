package com.keymetrics.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static com.keymetrics.helper.HttpHelper.getHttpHeaders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MetricsIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String baseUrlDeployment;
    private String baseUrlMetrics;

    @BeforeEach
    void setup() {
        baseUrlDeployment = "http://localhost:" + port + "/api/v1/deploy";
        baseUrlMetrics = "http://localhost:" + port + "/api/v1/metrics";
    }

    @Test
    void shouldGetMetricsForAService() {
        String name = "someServiceName";
        String buildVersion1 = "b123";
        String buildVersion2 = "b234";

        HttpEntity<String> requestEntity = new HttpEntity<>(getHttpHeaders());

        restTemplate.exchange(baseUrlDeployment + "?serviceName=" + name + "&environment=1" + "&buildVersion=" + buildVersion1, HttpMethod.POST, requestEntity, String.class);
        restTemplate.exchange(baseUrlDeployment + "?serviceName=" + name + "&environment=1"  + "&buildVersion=" + buildVersion2, HttpMethod.POST, requestEntity, String.class);
        restTemplate.exchange(baseUrlDeployment + "?serviceName=" + name + "&environment=2"  + "&buildVersion=" + buildVersion1, HttpMethod.POST, requestEntity, String.class);
        restTemplate.exchange(baseUrlDeployment + "?serviceName=" + name + "&environment=2"  + "&buildVersion=" + buildVersion2, HttpMethod.POST, requestEntity, String.class);

        ResponseEntity<String> response = restTemplate
                .exchange(baseUrlMetrics + "?serviceName=" + name, HttpMethod.GET, requestEntity, String.class);

        assertThat(response.getBody()).isEqualTo("[{\"buildVersion\":\"b234\",\"timeInMinutes\":0},{\"buildVersion\":\"b123\",\"timeInMinutes\":0}]");
    }
}
