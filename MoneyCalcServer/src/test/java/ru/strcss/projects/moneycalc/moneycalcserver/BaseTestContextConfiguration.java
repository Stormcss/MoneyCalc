package ru.strcss.projects.moneycalc.moneycalcserver;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.strcss.projects.moneycalc.moneycalcserver.security.WebSecurity;

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
}
