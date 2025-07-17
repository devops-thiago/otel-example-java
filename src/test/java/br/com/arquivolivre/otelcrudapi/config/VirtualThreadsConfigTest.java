package br.com.arquivolivre.otelcrudapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.*;

@SpringJUnitConfig
class VirtualThreadsConfigTest {

    @Test
    void virtualThreadsEnabled_ShouldCreateVirtualThreadConfiguration() {
        VirtualThreadsConfig config = new VirtualThreadsConfig();
        
        AsyncTaskExecutor executor = config.applicationTaskExecutor();
        assertThat(executor).isNotNull();
        
        TomcatProtocolHandlerCustomizer<?> customizer = config.protocolHandlerVirtualThreadExecutorCustomizer();
        assertThat(customizer).isNotNull();
    }

    @Test
    void applicationTaskExecutor_ShouldReturnValidExecutor() {
        VirtualThreadsConfig config = new VirtualThreadsConfig();
        AsyncTaskExecutor executor = config.applicationTaskExecutor();
        
        assertThat(executor).isNotNull();
        
        // Test that the executor can execute tasks
        assertThatCode(() -> {
            executor.execute(() -> {
                // Simple task to verify executor works
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }).doesNotThrowAnyException();
    }

    @Test
    void protocolHandlerVirtualThreadExecutorCustomizer_ShouldReturnValidCustomizer() {
        VirtualThreadsConfig config = new VirtualThreadsConfig();
        TomcatProtocolHandlerCustomizer<?> customizer = config.protocolHandlerVirtualThreadExecutorCustomizer();
        
        assertThat(customizer).isNotNull();
        
        // Test that the customizer is properly configured
        // Note: Testing the actual customization would require a real protocol handler
        // This test verifies the customizer bean is created successfully
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public VirtualThreadsConfig virtualThreadsConfig() {
            return new VirtualThreadsConfig();
        }
    }
} 