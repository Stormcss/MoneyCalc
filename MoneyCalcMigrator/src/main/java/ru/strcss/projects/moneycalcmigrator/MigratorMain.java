package ru.strcss.projects.moneycalcmigrator;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import ru.strcss.projects.moneycalcmigrator.utils.Config;
import ru.strcss.projects.moneycalcmigrator.utils.ConfigContainer;

import java.io.IOException;

public class MigratorMain {

//    @Autowired

    public static void main(String[] args) throws IOException {

        AbstractApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        ConfigContainer dataConfig = context.getBean("dataConfig", ConfigContainer.class);

        System.out.println("dataSource.getDataPath() = " + dataConfig.getDataPath());

        Parser parser = new Parser(dataConfig);

        parser.parse();
    }
}
