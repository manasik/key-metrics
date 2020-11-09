//package com.keymetrics.service;
//
//import com.keymetrics.entity.Metrics;
//import com.keymetrics.repository.MetricsRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Date;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//class DeploymentServiceTest {
//
//    @Mock
//    MetricsRepository metricsRepository;
//
//    @InjectMocks
//    DeploymentService service;
//
//    String name = "blah";
//    Integer environment = 1;
//
//    @Captor
//    private ArgumentCaptor<Metrics> metricsArgumentCaptor;
//
//    @Nested
//    @DisplayName("insert metric")
//    class insertMetric {
//
//        @Test
//        void shouldSaveMetricWithDeployedTime() throws Exception {
//            Date now = new Date();
//
//            service.update(name, environment);
//
//            verify(metricsRepository).save(metricsArgumentCaptor.capture());
//
//            Metrics savedMetrics = metricsArgumentCaptor.getValue();
//
//            assertThat(savedMetrics.serviceName).isEqualTo(name);
//            assertThat(savedMetrics.environment).isEqualTo(environment);
//            assertThat(savedMetrics.deployedAt).isCloseTo(now, 1000);
//
//        }
//
//    }
//
//}
