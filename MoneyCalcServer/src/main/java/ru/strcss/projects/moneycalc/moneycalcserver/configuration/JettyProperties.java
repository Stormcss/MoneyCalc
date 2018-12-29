package ru.strcss.projects.moneycalc.moneycalcserver.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Setter
@Getter
@Configuration
@ConfigurationProperties("jetty")
public class JettyProperties {

    private String threadNamePrefix;

    @Min(3)
    private int minThreads;

    @Min(4)
    @Max(1000)
    private int maxThreads;

    private int idleTimeout;
}
