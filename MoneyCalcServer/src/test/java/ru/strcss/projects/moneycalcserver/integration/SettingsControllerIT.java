package ru.strcss.projects.moneycalcserver.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.LoginGetContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.SpendingSectionSearchType;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalcserver.integration.utils.Generator;
import ru.strcss.projects.moneycalcserver.integration.utils.Utils;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.testng.Assert.*;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.formatDateToString;
import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;

@Slf4j
public class SettingsControllerIT extends AbstractControllerIT {

    @Test
    public void saveSettingsIncorrectLogin() {
        String login = Utils.savePersonGetLogin(service);

        Settings settingsIncorrect = Generator.generateSettings(login);
        settingsIncorrect.setLogin("");

        AjaxRs<Settings> response = Utils.sendRequest(service.saveSettings(settingsIncorrect)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Incorrect Settings are saved!");
    }

    @Test
    public void saveDuplicateSpendingSections() {
        String login = Utils.savePersonGetLogin(service);
        SpendingSection spendingSection1 = Generator.generateSpendingSection();

        Utils.sendRequest(service.addSpendingSection(new SpendingSectionAddContainer(login, spendingSection1)), Status.SUCCESS).body();
        AjaxRs<List<SpendingSection>> responseAddSection = Utils.sendRequest(service.addSpendingSection(new SpendingSectionAddContainer(login, spendingSection1)), Status.SUCCESS).body();

        assertEquals(responseAddSection.getStatus(), Status.ERROR, responseAddSection.getMessage());
    }

    @Test
    public void getSettings() {
        String login = Utils.savePersonGetLogin(service);

        //Getting Settings
        AjaxRs<Settings> responseGetSettings = Utils.sendRequest(service.getSettings(new LoginGetContainer(login)), Status.SUCCESS).body();

        log.debug("Settings: {}", responseGetSettings.getPayload());

        assertEquals(responseGetSettings.getPayload().getLogin(), login, "returned Settings object has wrong login!");
        assertTrue(responseGetSettings.getPayload().getSections()
                .stream().allMatch(section -> section.getId() != null), "Some IDs in Spending Sections are null!");
    }

    @Test
    public void settingsUpdate() {
        String login = Utils.savePersonGetLogin(service);
        Settings newSettings = Generator.generateSettings(login);
        newSettings.setPeriodTo(formatDateToString(generateDatePlus(ChronoUnit.YEARS, 1)));

        //Requesting settings
        AjaxRs<Settings> responseGetSettings = Utils.sendRequest(service.getSettings(new LoginGetContainer(login)), Status.SUCCESS).body();

        //Updating Settings
        AjaxRs<Settings> responseUpdated = Utils.sendRequest(service.saveSettings(newSettings), Status.SUCCESS).body();

        assertNotNull(responseUpdated.getPayload(), "Payload is null!");
        assertEquals(responseUpdated.getPayload().getLogin(), login, "returned Settings object has wrong login!");
        assertEquals(newSettings.getPeriodTo(),
                responseUpdated.getPayload().getPeriodTo(), "Settings were not updated!");

        //Checking that Person is ok after updating Settings
        AjaxRs<Identifications> responseIdentifications = Utils.sendRequest(service.getIdentifications(login)).body();
        assertNotNull(responseIdentifications.getPayload(), "Identifications object was overwritten!");

        log.debug("Settings before update: {}", responseGetSettings.getPayload());
        log.debug("Settings after update: {}", responseUpdated.getPayload());
    }

    @Test
    public void addSpendingSection() {
        String login = Utils.savePersonGetLogin(service);
        SpendingSection spendingSection = Generator.generateSpendingSection();
        SpendingSectionAddContainer spendingSectionContainer =
                new SpendingSectionAddContainer(login, spendingSection);

        AjaxRs<List<SpendingSection>> responseAddSection = Utils.sendRequest(service.addSpendingSection(spendingSectionContainer), Status.SUCCESS).body();
        assertTrue(responseAddSection.getPayload().stream().anyMatch(section -> section.getName().equals(spendingSection.getName())),
                "Spending Section was not added!");
    }

    @Test
    public void deleteSpendingSectionByName() {
        String login = Utils.savePersonGetLogin(service);
        SpendingSection spendingSection = Generator.generateSpendingSection();
        SpendingSectionDeleteContainer deleteContainerByName =
                new SpendingSectionDeleteContainer(login, spendingSection.getName(), SpendingSectionSearchType.BY_NAME);

        AjaxRs<List<SpendingSection>> responseDeleteSection = Utils.sendRequest(service.deleteSpendingSection(deleteContainerByName), Status.SUCCESS).body();
        assertTrue(responseDeleteSection.getPayload().stream().noneMatch(section -> section.getName().equals(spendingSection.getName())),
                "Spending Section was not deleted!");
    }

    @Test
    public void deleteSpendingSectionById() {
        String login = Utils.savePersonGetLogin(service);
        SpendingSection spendingSection = Generator.generateSpendingSection();
        SpendingSectionDeleteContainer deleteContainerById =
                new SpendingSectionDeleteContainer(login, "" + spendingSection.getId(), SpendingSectionSearchType.BY_ID);

        AjaxRs<List<SpendingSection>> responseDeleteSection = Utils.sendRequest(service.deleteSpendingSection(deleteContainerById), Status.SUCCESS).body();
        assertTrue(responseDeleteSection.getPayload().stream().noneMatch(section -> section.getName().equals(spendingSection.getName())),
                "Spending Section was not deleted!");
    }

    @Test
    public void updateSpendingSectionByName() {
        String login = Utils.savePersonGetLogin(service);
        SpendingSection spendingSection = Generator.generateSpendingSection();

        log.debug("Saved SpendingSection: {}", spendingSection);

        SpendingSectionAddContainer spendingSectionContainer = new SpendingSectionAddContainer(login, spendingSection);
        AjaxRs<List<SpendingSection>> responseAddSpendingSection = Utils.sendRequest(service.addSpendingSection(spendingSectionContainer), Status.SUCCESS).body();

        Integer oldBudget = spendingSection.getBudget();
        spendingSection.setBudget(ThreadLocalRandom.current().nextInt(1_000_000));

        SpendingSectionUpdateContainer updateContainerByName =
                new SpendingSectionUpdateContainer(login, "" + spendingSection.getName(), SpendingSectionSearchType.BY_NAME, spendingSection);

        AjaxRs<List<SpendingSection>> responseUpdateSection = Utils.sendRequest(service.updateSpendingSection(updateContainerByName), Status.SUCCESS).body();
        assertTrue(responseUpdateSection.getPayload().stream().noneMatch(section -> section.getBudget().equals(oldBudget)),
                "Spending Section was not updated!");
        assertEquals(responseAddSpendingSection.getPayload().size(), responseUpdateSection.getPayload().size(), "Number of Spending Sections has changed!");
        assertTrue(responseUpdateSection.getPayload().stream().noneMatch(section -> section.getId() == null), "Some Section IDs are null!");
    }

    @Test
    public void updateSpendingSectionById() {
        String login = Utils.savePersonGetLogin(service);

        SpendingSection spendingSection = Generator.generateSpendingSection();
        Integer updatedSectionID = 0;

        log.debug("Saved SpendingSection: {}", spendingSection);

//        SpendingSectionAddContainer spendingSectionContainer = new SpendingSectionAddContainer(login, spendingSection);
//        AjaxRs<List<SpendingSection>> responseAddSpendingSection = sendRequest(service.addSpendingSection(spendingSectionContainer), Status.SUCCESS).body();

        AjaxRs<List<SpendingSection>> responseGetSections = Utils.sendRequest(service.getSpendingSections(new LoginGetContainer(login)), Status.SUCCESS).body();

        String oldName = responseGetSections.getPayload().stream().filter(section -> section.getId().equals(updatedSectionID)).findAny().get().getName();
        spendingSection.setBudget(ThreadLocalRandom.current().nextInt(1_000_000));

        SpendingSectionUpdateContainer updateContainerById =
                new SpendingSectionUpdateContainer(login, Integer.toString(updatedSectionID), SpendingSectionSearchType.BY_ID, spendingSection);

        AjaxRs<List<SpendingSection>> responseUpdateSection = Utils.sendRequest(service.updateSpendingSection(updateContainerById), Status.SUCCESS).body();
        assertTrue(responseUpdateSection.getPayload().stream().noneMatch(section -> section.getName().equals(oldName)),
                "Spending Section was not updated!");
        assertEquals(responseGetSections.getPayload().size(), responseUpdateSection.getPayload().size(), "Number of Spending Sections has changed!");
        assertTrue(responseUpdateSection.getPayload().stream().noneMatch(section -> section.getId() == null), "Some Section IDs are null!");
    }

    @Test
    public void updateSpendingSection_duplicateNames(){
        // TODO: 27.02.2018 finish test
    }

    @Test
    public void getSpendingSections(){
        String login = Utils.savePersonGetLogin(service);
        AjaxRs<List<SpendingSection>> responseGetSections = Utils.sendRequest(service.getSpendingSections(new LoginGetContainer(login)), Status.SUCCESS).body();
        assertEquals(responseGetSections.getPayload().size(), 2, "Wrong number of spending sections is returned!");
    }
}