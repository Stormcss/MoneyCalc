package ru.strcss.projects.moneycalc.moneycalcserver;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.MetricsService;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.security.SecurityConstants;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.security.WebSecurity;

/**
 * Created by Stormcss
 * Date: 29.11.2018
 */
@TestConfiguration
@Import(WebSecurity.class)
public class BaseTestContextConfiguration {
    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    MetricsService metricsService() {
        return new MetricsService(meterRegistry());
    }

    @Bean
    SimpleMeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    @Bean
    SecurityConstants securityConstants() {
        SecurityConstants securityConstants = new SecurityConstants();
        securityConstants.setExpirationTimeMillis(864_000_000L);
        securityConstants.setHeaderString("Authorization");
        securityConstants.setSecret("Secret");
        securityConstants.setTokenPrefix("Bearer ");
        return securityConstants;
    }
}
