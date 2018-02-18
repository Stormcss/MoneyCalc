package ru.strcss.projects.moneycalcmigrator.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource(value = {"classpath:migration.properties"})
public class Config {

    @Value("${config.dataPath}")
    private String dataPath;

    @Value("${config.moneyCalcServerHost}")
    private String moneyCalcServerHost;

    @Value("${config.moneyCalcServerPort}")
    private String moneyCalcServerPort;

    @Bean
    public ConfigContainer dataConfig() {
        ConfigContainer cc = new ConfigContainer();
        cc.setDataPath(dataPath);
        cc.setMoneyCalcServerHost(moneyCalcServerHost);
        cc.setMoneyCalcServerPort(moneyCalcServerPort);
        return cc;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
