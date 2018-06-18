package ru.strcss.projects.moneycalcmigrator;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.strcss.projects.moneycalcmigrator.dto.MigrationType;
import ru.strcss.projects.moneycalcmigrator.properties.MigrationProperties;

@SpringBootApplication
public class MigratorMain implements ApplicationRunner {

    private final FileParser parser;
    private final MigrationProperties migrationProperties;

    public MigratorMain(FileParser parser, MigrationProperties migrationProperties) {
        this.parser = parser;
        this.migrationProperties = migrationProperties;
    }

    public static void main(String[] args) {
        SpringApplication.run(MigratorMain.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (migrationProperties.getMigrationType().equals(MigrationType.OLD_MONEYCALC_TO_NEW)) {
            parser.parseOldFiles(true);

        } else if (migrationProperties.getMigrationType().equals(MigrationType.DB_TO_FILE_BACKUP)) {
            throw new UnsupportedOperationException("Not supported yet");
        } else {
            throw new UnsupportedOperationException("Not supported yet");
        }
    }
}
