package ru.strcss.projects.moneycalc.moneycalcserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@Slf4j
@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class MoneyCalcApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyCalcApplication.class, args);
        log.info("MoneyCalcServer has started");
    }
}
