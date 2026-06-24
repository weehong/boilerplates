package com.example.boilerplate.shared.cache.configurations;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class CacheConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(CacheConfiguration.class);

    // ===== cacheManager =====

    @Test
    void given_cacheTypeNotRedis_when_contextRuns_then_cacheManagerNotLoaded() {
        contextRunner
            .withPropertyValues("spring.cache.type=none")
            .run(context -> assertThat(context)
                .doesNotHaveBean("cacheManager"));
    }

    @Test
    void given_noCacheType_when_contextRuns_then_cacheManagerNotLoaded() {
        contextRunner
            .run(context -> assertThat(context)
                .doesNotHaveBean("cacheManager"));
    }

}
