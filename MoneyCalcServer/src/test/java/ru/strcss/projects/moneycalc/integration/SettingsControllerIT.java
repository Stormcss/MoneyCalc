package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.integration.utils.Pair;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;

import java.time.temporal.ChronoUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.savePersonGetLoginAndToken;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.savePersonGetToken;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.sendRequest;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSettings;

@Slf4j
public class SettingsControllerIT extends AbstractIT {

    @Test
    public void getSettings() {
        String token = savePersonGetToken(service);

        Settings getSettingsRs = sendRequest(service.getSettings(token)).body();

        assertNotNull(getSettingsRs, "Settings are null!");
        assertTrue(getSettingsRs.isValid().isValidated(), "Settings are not valid!");
    }

    @Test
    public void settingsUpdate() {
        Pair<String, String> loginAndToken = savePersonGetLoginAndToken(service);
        String token = loginAndToken.getRight();
        Settings newSettings = generateSettings();
        newSettings.setPeriodTo(generateDatePlus(ChronoUnit.YEARS, 1));

        //Updating Settings
        Settings updatedRs = sendRequest(service.updateSettings(token, newSettings)).body();

        assertNotNull(updatedRs, "Settings is null!");
        assertEquals(updatedRs.getPeriodTo(), newSettings.getPeriodTo(), "Settings were not updated!");
    }
}