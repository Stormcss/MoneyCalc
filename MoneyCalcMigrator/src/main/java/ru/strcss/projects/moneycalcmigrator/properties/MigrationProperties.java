package ru.strcss.projects.moneycalcmigrator.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@PropertySource("classpath:migration.properties")
public class MigrationProperties {
    @Value("${config.dataPath}")
    private String dataPath;

    @Value("${config.moneyCalcServerHost}")
    private String moneyCalcServerHost;

    @Value("${config.moneyCalcServerPort}")
    private String moneyCalcServerPort;

    @Value("${config.login}")
    private String login;

    @Value("${config.password}")
    private String password;

    @Value("${config.name}")
    private String name;

    @Value("${config.email}")
    private String email;
}
