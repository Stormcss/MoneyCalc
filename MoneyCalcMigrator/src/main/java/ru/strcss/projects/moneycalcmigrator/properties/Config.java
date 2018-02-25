package ru.strcss.projects.moneycalcmigrator.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import ru.strcss.projects.moneycalcmigrator.dto.ConfigContainer;

@Configuration
@PropertySource(value = {"classpath:migration.properties"})
public class Config {

    @Value("${config.dataPath}")
    private String dataPath;

    @Value("${config.moneyCalcServerHost}")
    private String moneyCalcServerHost;

    @Value("${config.moneyCalcServerPort}")
    private String moneyCalcServerPort;

    @Value("${config.login}")
    private String login;

    @Value("${config.name}")
    private String name;

    @Value("${config.email}")
    private String email;

    @Bean
    public ConfigContainer dataConfig() {
        ConfigContainer cc = new ConfigContainer();
        cc.setDataPath(dataPath);
        cc.setMoneyCalcServerHost(moneyCalcServerHost);
        cc.setMoneyCalcServerPort(moneyCalcServerPort);
        cc.setLogin(login);
        cc.setName(name);
        cc.setEmail(email);
        return cc;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}