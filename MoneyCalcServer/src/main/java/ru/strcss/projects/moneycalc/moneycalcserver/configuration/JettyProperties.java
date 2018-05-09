package ru.strcss.projects.moneycalc.moneycalcserver.configuration;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Component
@ConfigurationProperties("jetty")
@Getter
@Setter
public class JettyProperties {

    @NotEmpty
    private String threadNamePrefix;

    @Min(3)
    private int minThreads;

    @Max(1000)
    @Min(4)
    private int maxThreads;

    private int idleTimeout;
}
