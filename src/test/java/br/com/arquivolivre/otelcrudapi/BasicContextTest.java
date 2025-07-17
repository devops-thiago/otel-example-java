package br.com.arquivolivre.otelcrudapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "otel.traces.exporter=none",
    "otel.metrics.exporter=none",
    "otel.springboot.web.enabled=false",
    "otel.springboot.actuator.enabled=false",
    "spring.threads.virtual.enabled=false"
})
public class BasicContextTest {

    @Test
    public void contextLoads() {
        // This test will pass if the Spring Boot context loads successfully
    }
} 