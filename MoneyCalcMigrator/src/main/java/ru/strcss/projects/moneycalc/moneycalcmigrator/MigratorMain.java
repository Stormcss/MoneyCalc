package ru.strcss.projects.moneycalc.moneycalcmigrator;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MigratorMain implements ApplicationRunner {

    private final FileParser parser;

    public MigratorMain(FileParser parser) {
        this.parser = parser;
    }

    public static void main(String[] args) {
        SpringApplication.run(MigratorMain.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        parser.parseOldFiles(true);
    }
}
