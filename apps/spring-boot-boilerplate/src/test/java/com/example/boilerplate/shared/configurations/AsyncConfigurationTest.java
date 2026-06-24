package com.example.boilerplate.shared.configurations;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;

class AsyncConfigurationTest {

    private static final int EXPECTED_CORE_POOL_SIZE = 4;
    private static final int EXPECTED_MAX_POOL_SIZE = 8;

    private final AsyncConfiguration configuration = new AsyncConfiguration();

    // ===== taskExecutor =====

    @Test
    void given_defaultConfiguration_when_taskExecutor_then_threadPoolConfigured() {
        Executor executor = configuration.taskExecutor();

        assertThat(executor).isInstanceOfSatisfying(
            ThreadPoolTaskExecutor.class,
            taskExecutor -> {
                assertThat(taskExecutor.getCorePoolSize()).isEqualTo(EXPECTED_CORE_POOL_SIZE);
                assertThat(taskExecutor.getMaxPoolSize()).isEqualTo(EXPECTED_MAX_POOL_SIZE);
                assertThat(taskExecutor.getThreadNamePrefix()).isEqualTo("async-");
            });
    }

}
