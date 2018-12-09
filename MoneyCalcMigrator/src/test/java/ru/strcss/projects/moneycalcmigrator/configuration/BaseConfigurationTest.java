package ru.strcss.projects.moneycalcmigrator.configuration;

import org.testng.annotations.Test;
import ru.strcss.projects.moneycalcmigrator.api.MigrationAPI;
import ru.strcss.projects.moneycalcmigrator.properties.MigrationProperties;

import static org.testng.Assert.assertNotNull;

/**
 * Created by Stormcss
 * Date: 09.12.2018
 */
public class BaseConfigurationTest {

    @Test
    public void testMigrationAPI() {
        BaseConfiguration baseConfiguration = new BaseConfiguration();
        MigrationProperties migrationProperties = new MigrationProperties();
        migrationProperties.setMoneyCalcServerPort("8080");
        migrationProperties.setMoneyCalcServerHost("http://localhost");

        MigrationAPI migrationAPI = baseConfiguration.migrationAPI(migrationProperties);

        assertNotNull(migrationAPI, "API implementation is not created!");
    }
}