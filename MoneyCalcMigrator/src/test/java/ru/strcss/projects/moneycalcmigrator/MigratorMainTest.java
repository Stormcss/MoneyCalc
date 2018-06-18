package ru.strcss.projects.moneycalcmigrator;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalcmigrator.dto.MigrationType;
import ru.strcss.projects.moneycalcmigrator.properties.MigrationProperties;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;

public class MigratorMainTest {

    private FileParser parser = mock(FileParser.class);
    private MigrationProperties migrationProperties = mock(MigrationProperties.class);
    private MigratorMain migratorMain = new MigratorMain(parser, migrationProperties);

    @BeforeClass
    public void setUp() throws Exception {
        doNothing().when(parser).parseOldFiles(anyBoolean());
    }

    @Test
    public void testRun_oldMigration() throws Exception {
        when(migrationProperties.getMigrationType()).thenReturn(MigrationType.OLD_MONEYCALC_TO_NEW);

        migratorMain.run(null);

        verify(parser, times(1)).parseOldFiles(anyBoolean());
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testRun_db2File_Migration() throws Exception {
        when(migrationProperties.getMigrationType()).thenReturn(MigrationType.DB_TO_FILE_BACKUP);

        migratorMain.run(null);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testRun_file2DB_Migration() throws Exception {
        when(migrationProperties.getMigrationType()).thenReturn(MigrationType.FILE_BACKUP_TO_DB);

        migratorMain.run(null);
    }

}