package ru.strcss.projects.moneycalc.moneycalcserver.configuration.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Validated
@Configuration
@ConfigurationProperties("security")
public class SecurityConstants {

    @NotNull
    @NotBlank
    private String secret;
    @NotNull
    private Long expirationTimeMillis;
    @NotNull
    @NotBlank
    private String tokenPrefix;
    @NotNull
    @NotBlank
    private String headerString;
    private final String SIGN_UP_URL = "/api/registration/register";
}
