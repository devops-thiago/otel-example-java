package br.com.arquivolivre.otelcrudapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.threads.virtual.enabled=true"
})
class OtelCrudApiApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring Boot application context loads successfully
        // with all configurations, including virtual threads and OpenTelemetry
    }
} 