package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.SpendingSectionSearchType;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SettingsUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.integration.utils.Generator;
import ru.strcss.projects.moneycalc.integration.utils.Pair;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.testng.Assert.*;
import static ru.strcss.projects.moneycalc.integration.utils.Generator.generateSpendingSection;
import static ru.strcss.projects.moneycalc.integration.utils.Utils.*;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.formatDateToString;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;

@Slf4j
public class SettingsControllerIT extends AbstractIT {

    @Test
    public void saveSettingsIncorrectLogin() {
        Pair<String, String> loginAndToken = savePersonGetLoginAndToken(service);
        String login = loginAndToken.getLeft();
        String token = loginAndToken.getRight();

        Settings settingsIncorrect = Generator.generateSettings(login);
        settingsIncorrect.setLogin("");

        AjaxRs<Settings> response = sendRequest(service.saveSettings(token, new SettingsUpdateContainer(settingsIncorrect))).body();

        assertEquals(response.getStatus(), Status.ERROR, "Incorrect Settings are saved!");
    }

    @Test
    public void saveDuplicateSpendingSections() {
        Pair<String, String> loginAndToken = savePersonGetLoginAndToken(service);
        String token = loginAndToken.getRight();
        SpendingSection spendingSection = generateSpendingSection();

        sendRequest(service.addSpendingSection(token, new SpendingSectionAddContainer(spendingSection)), Status.SUCCESS).body();
        AjaxRs<List<SpendingSection>> responseAddSection = sendRequest(service.addSpendingSection(token, new SpendingSectionAddContainer(spendingSection))).body();

        assertEquals(responseAddSection.getStatus(), Status.ERROR, responseAddSection.getMessage());
    }

    @Test
    public void getSettings() {
        Pair<String, String> loginAndToken = savePersonGetLoginAndToken(service);
        String login = loginAndToken.getLeft();
        String token = loginAndToken.getRight();

        AjaxRs<Settings> responseGetSettings = sendRequest(service.getSettings(token), Status.SUCCESS).body();

        assertEquals(responseGetSettings.getPayload().getLogin(), login, "returned Settings object has wrong login!");
        assertTrue(responseGetSettings.getPayload().getSections()
                .stream().allMatch(section -> section.getId() != null), "Some IDs in Spending Sections are null!");
    }

    @Test
    public void settingsUpdate() {
        Pair<String, String> loginAndToken = savePersonGetLoginAndToken(service);
        String login = loginAndToken.getLeft();
        String token = loginAndToken.getRight();
        Settings newSettings = Generator.generateSettings(login);
        newSettings.setPeriodTo(formatDateToString(generateDatePlus(ChronoUnit.YEARS, 1)));

        //Updating Settings
        AjaxRs<Settings> responseUpdated = sendRequest(service.saveSettings(token, new SettingsUpdateContainer(newSettings)), Status.SUCCESS).body();

        assertNotNull(responseUpdated.getPayload(), "Payload is null!");
        assertEquals(responseUpdated.getPayload().getLogin(), login, "returned Settings object has wrong login!");
        assertEquals(newSettings.getPeriodTo(),
                responseUpdated.getPayload().getPeriodTo(), "Settings were not updated!");

        //Checking that Person is ok after updating Settings
        AjaxRs<Identifications> responseIdentifications = sendRequest(service.getIdentifications(token)).body();
        assertNotNull(responseIdentifications.getPayload(), "Identifications object was overwritten!");
    }

    @Test
    public void addSpendingSection() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection();

        SpendingSectionAddContainer spendingSectionContainer =
                new SpendingSectionAddContainer(spendingSection);

        AjaxRs<List<SpendingSection>> responseAddSection = sendRequest(service.addSpendingSection(token, spendingSectionContainer), Status.SUCCESS).body();
        assertTrue(responseAddSection.getPayload().stream().anyMatch(section -> section.getName().equals(spendingSection.getName())),
                "Spending Section was not added!");
    }

    @Test
    public void deleteSpendingSectionByName() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection();

        SpendingSectionDeleteContainer deleteContainerByName =
                new SpendingSectionDeleteContainer(spendingSection.getName(), SpendingSectionSearchType.BY_NAME);

        AjaxRs<List<SpendingSection>> responseDeleteSection = sendRequest(service.deleteSpendingSection(token, deleteContainerByName), Status.SUCCESS).body();
        assertTrue(responseDeleteSection.getPayload().stream().noneMatch(section -> section.getName().equals(spendingSection.getName())),
                "Spending Section was not deleted!");
    }

    @Test
    public void deleteSpendingSectionById() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection();

        SpendingSectionDeleteContainer deleteContainerById =
                new SpendingSectionDeleteContainer("" + spendingSection.getId(), SpendingSectionSearchType.BY_ID);

        AjaxRs<List<SpendingSection>> responseDeleteSection = sendRequest(service.deleteSpendingSection(token, deleteContainerById), Status.SUCCESS).body();
        assertTrue(responseDeleteSection.getPayload().stream().noneMatch(section -> section.getName().equals(spendingSection.getName())),
                "Spending Section was not deleted!");
    }

    @Test
    public void updateSpendingSectionByName() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection();

        log.debug("Saved SpendingSection: {}", spendingSection);

        SpendingSectionAddContainer spendingSectionContainer = new SpendingSectionAddContainer(spendingSection);
        AjaxRs<List<SpendingSection>> responseAddSpendingSection = sendRequest(service.addSpendingSection(token, spendingSectionContainer), Status.SUCCESS).body();

        Integer oldBudget = spendingSection.getBudget();
        spendingSection.setBudget(ThreadLocalRandom.current().nextInt(1_000_000));

        SpendingSectionUpdateContainer updateContainerByName =
                new SpendingSectionUpdateContainer("" + spendingSection.getName(), spendingSection, SpendingSectionSearchType.BY_NAME);

        AjaxRs<List<SpendingSection>> responseUpdateSection = sendRequest(service.updateSpendingSection(token, updateContainerByName), Status.SUCCESS).body();
        assertTrue(responseUpdateSection.getPayload().stream().noneMatch(section -> section.getBudget().equals(oldBudget)),
                "Spending Section was not updated!");
        assertEquals(responseAddSpendingSection.getPayload().size(), responseUpdateSection.getPayload().size(), "Number of Spending Sections has changed!");
        assertTrue(responseUpdateSection.getPayload().stream().noneMatch(section -> section.getId() == null), "Some Section IDs are null!");
    }

    @Test
    public void updateSpendingSectionById() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection();
        Integer updatedSectionID = 0;

        AjaxRs<List<SpendingSection>> responseGetSections = sendRequest(service.getSpendingSections(token), Status.SUCCESS).body();

        String oldName = responseGetSections.getPayload().stream().filter(section -> section.getId().equals(updatedSectionID)).findAny().get().getName();
        spendingSection.setBudget(ThreadLocalRandom.current().nextInt(1_000_000));

        SpendingSectionUpdateContainer updateContainerById =
                new SpendingSectionUpdateContainer(Integer.toString(updatedSectionID), spendingSection, SpendingSectionSearchType.BY_ID);

        AjaxRs<List<SpendingSection>> responseUpdateSection = sendRequest(service.updateSpendingSection(token, updateContainerById), Status.SUCCESS).body();
        assertTrue(responseUpdateSection.getPayload().stream().noneMatch(section -> section.getName().equals(oldName)),
                "Spending Section was not updated!");
        assertEquals(responseGetSections.getPayload().size(), responseUpdateSection.getPayload().size(), "Number of Spending Sections has changed!");
        assertTrue(responseUpdateSection.getPayload().stream().noneMatch(section -> section.getId() == null), "Some Section IDs are null!");
    }

    @Test
    public void updateSpendingSection_duplicateNames() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection1 = generateSpendingSection();
        SpendingSection spendingSection2 = generateSpendingSection();

        sendRequest(service.addSpendingSection(token, new SpendingSectionAddContainer(spendingSection1)), Status.SUCCESS).body();
        sendRequest(service.addSpendingSection(token, new SpendingSectionAddContainer(spendingSection2)), Status.SUCCESS).body();

        AjaxRs<List<SpendingSection>> responseUpdateSection =
                sendRequest(service.updateSpendingSection(token, new SpendingSectionUpdateContainer(spendingSection1.getName(),
                        spendingSection2, SpendingSectionSearchType.BY_NAME))).body();

        assertEquals(responseUpdateSection.getStatus(), Status.ERROR, responseUpdateSection.getMessage());
    }

    @Test
    public void getSpendingSections() {
        String token = savePersonGetToken(service);

        AjaxRs<List<SpendingSection>> responseGetSections = sendRequest(service.getSpendingSections(token), Status.SUCCESS).body();

        assertEquals(responseGetSections.getPayload().size(), 2, "Wrong number of spending sections is returned!");
    }
}