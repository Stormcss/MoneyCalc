package ru.strcss.projects.moneycalcmigrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class MigratorMain implements ApplicationRunner {

    private final Parser parser;

    @Autowired
    public MigratorMain(Parser parser) {
        this.parser = parser;
    }

    public static void main(String[] args) throws IOException {

        SpringApplication.run(MigratorMain.class, args);
//        AbstractApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

//        ConfigContainer dataConfig = context.getBean("dataConfig", ConfigContainer.class);

//        System.out.println("dataSource.getDataPath() = " + dataConfig.getDataPath());
//
//        Parser parser = new Parser(dataConfig);
//
//        parser.parse();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        parser.parse();
    }
}
