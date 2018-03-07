package ru.strcss.projects.moneycalcmigrator;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MigratorMain implements ApplicationRunner {

    private final Parser parser;

    public MigratorMain(Parser parser) {
        this.parser = parser;
    }

    public static void main(String[] args){
        SpringApplication.run(MigratorMain.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        parser.parse();
    }
}
