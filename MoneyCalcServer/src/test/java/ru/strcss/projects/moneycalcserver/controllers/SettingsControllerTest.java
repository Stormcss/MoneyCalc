package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.SpendingSectionSearchType;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalcserver.controllers.utils.Generator;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.testng.Assert.*;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.savePersonGetLogin;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

@Slf4j
public class SettingsControllerTest extends AbstractControllerTest {

    @Test
    public void saveSettingsIncorrectLogin() {
        String login = savePersonGetLogin(service);

        Settings settingsIncorrect = Generator.generateSettings(login, 2);
        settingsIncorrect.setLogin("");

        AjaxRs<Settings> response = sendRequest(service.saveSettings(settingsIncorrect)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Incorrect Settings are saved!");
    }

    @Test
    public void saveSettingsDuplicateSections() {
        String login = savePersonGetLogin(service);

        Settings incorrectSettings = Generator.generateSettings(login, 5);

        incorrectSettings.getSections().get(0).setId(incorrectSettings.getSections().get(1).getId());

        AjaxRs<Settings> response = sendRequest(service.saveSettings(incorrectSettings)).body();

        assertEquals(response.getStatus(), Status.ERROR, response.getMessage());
    }

    @Test
    public void getSettings() {
//        String login = "2649AA313EF74CBABEC0B0E0AEF3E6A7";
        String login = savePersonGetLogin(service);

        //Getting Settings
        AjaxRs<Settings> responseGetSettings = sendRequest(service.getSettings(login), Status.SUCCESS).body();

        log.debug("Settings: {}", responseGetSettings.getPayload());

        assertEquals(responseGetSettings.getPayload().getLogin(), login, "returned Settings object has wrong login!");
        assertTrue(responseGetSettings.getPayload().getSections()
                .stream().allMatch(section -> section.getId() != null), "Some IDs in Spending Sections are null!");
    }

    @Test
    public void saveSettingsUpdate() {

        String login = savePersonGetLogin(service);
        Settings newSettings = Generator.generateSettings(login, 2);

        //Requesting settings
        AjaxRs<Settings> responseGetSettings = sendRequest(service.getSettings(login), Status.SUCCESS).body();

        //Updating Settings again
        AjaxRs<Settings> responseUpdate = sendRequest(service.saveSettings(newSettings), Status.SUCCESS).body();
        assertNotNull(responseUpdate.getPayload(), "Payload is null!");
        assertNotNull(responseUpdate.getPayload().getSections(), "Settings object is empty!");
        assertEquals(responseUpdate.getPayload().getLogin(), login, "returned Settings object has wrong login!");
        assertNotEquals(responseUpdate.getPayload().getSections().get(0).getName(),
                responseGetSettings.getPayload().getSections().get(0).getName(), "Settings were not updated!");


        //Checking that Person is ok after updating Settings
        AjaxRs<Identifications> responseIdentifications = sendRequest(service.getIdentifications(login)).body();
        assertNotNull(responseIdentifications.getPayload(), "Identifications object was overwritten!");

        //Requesting updated settings
        AjaxRs<Settings> responseGetUpdated = sendRequest(service.getSettings(login), Status.SUCCESS).body();

        assertNotNull(responseGetUpdated.getPayload(), "Payload is null!");
        assertNotNull(responseGetUpdated.getPayload().getSections(), "Settings object is empty!");
        assertEquals(responseGetUpdated.getPayload().getSections().get(0).getName(),
                newSettings.getSections().get(0).getName(), "Settings were not updated!");

        log.debug("Settings before update: {}", responseGetSettings.getPayload());
        log.debug("Settings after update: {}", responseGetUpdated.getPayload());
    }

    @Test
    public void addSpendingSection() {
        String login = savePersonGetLogin(service);
        SpendingSection spendingSection = Generator.generateSpendingSection();
        SpendingSectionAddContainer spendingSectionContainer =
                new SpendingSectionAddContainer(login, spendingSection);

        AjaxRs<List<SpendingSection>> responseAddSection = sendRequest(service.addSpendingSection(spendingSectionContainer), Status.SUCCESS).body();
        assertTrue(responseAddSection.getPayload().stream().anyMatch(section -> section.getName().equals(spendingSection.getName())),
                "Spending Section was not added!");
    }

    @Test
    public void deleteSpendingSectionByName() {
        String login = savePersonGetLogin(service);
        SpendingSection spendingSection = Generator.generateSpendingSection();
        SpendingSectionDeleteContainer deleteContainerByName =
                new SpendingSectionDeleteContainer(login, spendingSection.getName(), SpendingSectionSearchType.BY_NAME);

        AjaxRs<List<SpendingSection>> responseDeleteSection = sendRequest(service.deleteSpendingSection(deleteContainerByName), Status.SUCCESS).body();
        assertTrue(responseDeleteSection.getPayload().stream().noneMatch(section -> section.getName().equals(spendingSection.getName())),
                "Spending Section was not deleted!");
    }

    @Test
    public void deleteSpendingSectionById() {
        String login = savePersonGetLogin(service);
        SpendingSection spendingSection = Generator.generateSpendingSection();
        SpendingSectionDeleteContainer deleteContainerById =
                new SpendingSectionDeleteContainer(login, "" + spendingSection.getId(), SpendingSectionSearchType.BY_ID);

        AjaxRs<List<SpendingSection>> responseDeleteSection = sendRequest(service.deleteSpendingSection(deleteContainerById), Status.SUCCESS).body();
        assertTrue(responseDeleteSection.getPayload().stream().noneMatch(section -> section.getName().equals(spendingSection.getName())),
                "Spending Section was not deleted!");
    }

    @Test
    public void updateSpendingSectionByName() {
        String login = savePersonGetLogin(service);
        SpendingSection spendingSection = Generator.generateSpendingSection();

        log.debug("Saved SpendingSection: {}", spendingSection);

        SpendingSectionAddContainer spendingSectionContainer = new SpendingSectionAddContainer(login, spendingSection);
        AjaxRs<List<SpendingSection>> responseAddSpendingSection = sendRequest(service.addSpendingSection(spendingSectionContainer), Status.SUCCESS).body();

        Integer oldBudget = spendingSection.getBudget();
        spendingSection.setBudget(ThreadLocalRandom.current().nextInt(1_000_000));

        SpendingSectionUpdateContainer updateContainerByName =
                new SpendingSectionUpdateContainer(login, "" + spendingSection.getName(), SpendingSectionSearchType.BY_NAME, spendingSection);

        AjaxRs<List<SpendingSection>> responseDeleteSection = sendRequest(service.updateSpendingSection(updateContainerByName), Status.SUCCESS).body();
        assertTrue(responseDeleteSection.getPayload().stream().noneMatch(section -> section.getBudget().equals(oldBudget)),
                "Spending Section was not updated!");
        assertEquals(responseAddSpendingSection.getPayload().size(), responseDeleteSection.getPayload().size(), "Number of Spending Sections has changed!");
    }

    @Test
    public void updateSpendingSectionById() {
        String login = savePersonGetLogin(service);
        SpendingSection spendingSection = Generator.generateSpendingSection();

        log.debug("Saved SpendingSection: {}", spendingSection);

        SpendingSectionAddContainer spendingSectionContainer = new SpendingSectionAddContainer(login, spendingSection);
        AjaxRs<List<SpendingSection>> responseAddSpendingSection = sendRequest(service.addSpendingSection(spendingSectionContainer), Status.SUCCESS).body();

        Integer oldBudget = spendingSection.getBudget();
        spendingSection.setBudget(ThreadLocalRandom.current().nextInt(1_000_000));

        SpendingSectionUpdateContainer updateContainerById =
                new SpendingSectionUpdateContainer(login, "" + spendingSection.getId(), SpendingSectionSearchType.BY_ID, spendingSection);

        AjaxRs<List<SpendingSection>> responseDeleteSection = sendRequest(service.updateSpendingSection(updateContainerById), Status.SUCCESS).body();
        assertTrue(responseDeleteSection.getPayload().stream().noneMatch(section -> section.getBudget().equals(oldBudget)),
                "Spending Section was not updated!");
        assertEquals(responseAddSpendingSection.getPayload().size(), responseDeleteSection.getPayload().size(), "Number of Spending Sections has changed!");
    }
}