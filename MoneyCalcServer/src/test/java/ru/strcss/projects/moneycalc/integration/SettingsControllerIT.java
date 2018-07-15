package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SettingsUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.integration.utils.IdsContainer;
import ru.strcss.projects.moneycalc.integration.utils.Pair;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.testng.Assert.*;
import static ru.strcss.projects.moneycalc.dto.crudcontainers.SpendingSectionSearchType.BY_ID;
import static ru.strcss.projects.moneycalc.dto.crudcontainers.SpendingSectionSearchType.BY_NAME;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.*;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.formatDateToString;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSettings;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSpendingSection;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.getMaxSpendingSectionId;

@Slf4j
public class SettingsControllerIT extends AbstractIT {

    @Test
    public void saveSettingsIncorrectDateFrom() {
        Pair<String, String> loginAndToken = savePersonGetLoginAndToken(service);
        String token = loginAndToken.getRight();

        Settings settingsIncorrect = generateSettings();
        settingsIncorrect.setPeriodFrom("");

        Response<MoneyCalcRs<Settings>> saveSettingsRs =
                sendRequest(service.saveSettings(token, new SettingsUpdateContainer(settingsIncorrect)));

        assertFalse(saveSettingsRs.isSuccessful(), "Incorrect Settings are saved!");
    }

    @Test
    public void saveDuplicateSpendingSections() {
        Pair<String, String> loginAndToken = savePersonGetLoginAndToken(service);
        String token = loginAndToken.getRight();
        SpendingSection spendingSection = generateSpendingSection();

        sendRequest(service.addSpendingSection(token, new SpendingSectionAddContainer(spendingSection)), Status.SUCCESS).body();
        Response<MoneyCalcRs<List<SpendingSection>>> addSectionRs =
                sendRequest(service.addSpendingSection(token, new SpendingSectionAddContainer(spendingSection)));

        assertFalse(addSectionRs.isSuccessful(), "Response is not failed!");
    }

    @Test
    public void getSettings() {
        Pair<IdsContainer, String> idsAndToken = savePersonGetIdsAndToken(service);
        IdsContainer idsContainer = idsAndToken.getLeft();
        String token = idsAndToken.getRight();

        MoneyCalcRs<Settings> getSettingsRs = sendRequest(service.getSettings(token), Status.SUCCESS).body();

        assertEquals(getSettingsRs.getPayload().getId(), idsContainer.getSettingsId(), "wrong settingsId!");
    }

    @Test
    public void settingsUpdate() {
        Pair<String, String> loginAndToken = savePersonGetLoginAndToken(service);
        String token = loginAndToken.getRight();
        Settings newSettings = generateSettings();
        newSettings.setPeriodTo(formatDateToString(generateDatePlus(ChronoUnit.YEARS, 1)));

        //Updating Settings
        MoneyCalcRs<Settings> updatedRs = sendRequest(service.saveSettings(token, new SettingsUpdateContainer(newSettings)), Status.SUCCESS).body();

        assertNotNull(updatedRs.getPayload(), "Payload is null!");
        assertEquals(newSettings.getPeriodTo(),
                updatedRs.getPayload().getPeriodTo(), "Settings were not updated!");

        //Checking that Person is ok after updating Settings
//        MoneyCalcRs<Identifications> identificationsRs = sendRequest(service.getIdentifications(token)).body();
//        assertNotNull(identificationsRs.getPayload(), "Identifications object was overwritten!");
    }


    @Test
    public void addSpendingSection() {
        int ignoredId = 6;
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection(1000, ignoredId);

        MoneyCalcRs<List<SpendingSection>> addSectionRs = addSpendingSectionGetRs(service, token, spendingSection);

        assertTrue(addSectionRs.getPayload().stream().anyMatch(section -> section.getName().equals(spendingSection.getName())),
                "Spending Section was not added!");
        assertTrue(addSectionRs.getPayload().stream().noneMatch(section -> section.getId() == ignoredId));
    }

    @Test
    public void addSpendingSection_correctIdIncrement() {
        int addedSectionsNum = 10;
        String token = savePersonGetToken(service);

        for (int i = 0; i < addedSectionsNum; i++) {
            SpendingSection spendingSection = generateSpendingSection();
            MoneyCalcRs<List<SpendingSection>> addSectionRs = addSpendingSectionGetRs(service, token, spendingSection);

            assertTrue(addSectionRs.getPayload().stream()
                    .anyMatch(section -> section.getName()
                            .equals(spendingSection.getName())), "Spending Section was not added!");
            assertEquals((int) addSectionRs.getPayload().stream()
                            .filter(section -> section.getName().equals(spendingSection.getName()))
                            .findAny().get().getSectionId(),
                    addSectionRs.getPayload().size() - 1, "Id has been incremented incorrectly!");
        }
    }

    @Test
    public void deleteSpendingSectionByName() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection();

        SpendingSectionDeleteContainer deleteContainerByName =
                new SpendingSectionDeleteContainer(spendingSection.getName(), BY_NAME);

        addSpendingSectionGetRs(service, token, spendingSection);

        MoneyCalcRs<List<SpendingSection>> deleteSectionRs =
                sendRequest(service.deleteSpendingSection(token, deleteContainerByName), Status.SUCCESS).body();
        assertTrue(deleteSectionRs.getPayload().stream().noneMatch(section -> section.getName().equals(spendingSection.getName())),
                "Spending Section was not deleted!");
    }

    @Test
    public void deleteSpendingSectionById() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection();

        int addedSectionId = addSpendingSectionGetId(service, token, spendingSection);

        MoneyCalcRs<List<SpendingSection>> deleteSectionRs = deleteSpendingSectionByIdGetRs(service, token, "" + addedSectionId);

        assertTrue(deleteSectionRs.getPayload().stream().noneMatch(section -> section.getName().equals(spendingSection.getName())),
                "Spending Section was not deleted!");
    }

    @Test
    public void updateSpendingSectionByName() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection();

        MoneyCalcRs<List<SpendingSection>> addSpendingSectionRs = addSpendingSectionGetRs(service, token, spendingSection);

        Integer oldBudget = spendingSection.getBudget();
        spendingSection.setBudget(ThreadLocalRandom.current().nextInt(1_000_000));

        SpendingSectionUpdateContainer updateContainerByName =
                new SpendingSectionUpdateContainer("" + spendingSection.getName(), spendingSection, BY_NAME);

        MoneyCalcRs<List<SpendingSection>> updateSectionRs =
                sendRequest(service.updateSpendingSection(token, updateContainerByName), Status.SUCCESS).body();
        assertTrue(updateSectionRs.getPayload().stream().noneMatch(section -> section.getBudget().equals(oldBudget)),
                "Spending Section was not updated!");
        assertEquals(addSpendingSectionRs.getPayload().size(), updateSectionRs.getPayload().size(),
                "Number of Spending Sections has changed!");
        assertTrue(updateSectionRs.getPayload().stream().noneMatch(section -> section.getId() == null),
                "Some Section IDs are null!");
    }

    @Test
    public void updateSpendingSectionById() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection();
        Integer updatedSectionID = 0;

        MoneyCalcRs<List<SpendingSection>> getSectionsRs = sendRequest(service.getSpendingSections(token), Status.SUCCESS).body();

        String oldName = getSectionsRs.getPayload()
                .stream().filter(section -> section.getSectionId().equals(updatedSectionID)).findAny().get().getName();
        spendingSection.setBudget(ThreadLocalRandom.current().nextInt(1_000_000));

        SpendingSectionUpdateContainer updateContainerById =
                new SpendingSectionUpdateContainer(Integer.toString(updatedSectionID), spendingSection, BY_ID);

        MoneyCalcRs<List<SpendingSection>> updateSectionRs =
                sendRequest(service.updateSpendingSection(token, updateContainerById), Status.SUCCESS).body();
        assertTrue(updateSectionRs.getPayload().stream().noneMatch(section -> section.getName().equals(oldName)),
                "Spending Section was not updated!");
        assertEquals(getSectionsRs.getPayload().size(), updateSectionRs.getPayload().size(), "Number of Spending Sections has changed!");
        assertTrue(updateSectionRs.getPayload().stream().noneMatch(section -> section.getId() == null), "Some Section IDs are null!");
    }

    @Test
    public void updateSpendingSection_duplicateNames() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection1 = generateSpendingSection();
        SpendingSection spendingSection2 = generateSpendingSection();

        addSpendingSectionGetRs(service, token, spendingSection1);
        addSpendingSectionGetRs(service, token, spendingSection2);

        Response<MoneyCalcRs<List<SpendingSection>>> updateSectionRs = sendRequest(service.updateSpendingSection(token,
                new SpendingSectionUpdateContainer(spendingSection1.getName(),
                        spendingSection2, BY_NAME)));

        assertFalse(updateSectionRs.isSuccessful(), "Response is not failed!");
    }

    @Test
    public void getSpendingSections() {
        String token = savePersonGetToken(service);

        MoneyCalcRs<List<SpendingSection>> getSectionsRs = sendRequest(service.getSpendingSections(token), Status.SUCCESS).body();

        assertEquals(getSectionsRs.getPayload().size(), 2, "Wrong number of spending sections is returned!");
    }

    /**
     * Test case checking that spending section was deactivated after deletion, but not removed
     */
    @Test
    public void deleteSpendingSectionById_deactivationCheck() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection1 = generateSpendingSection();

        //Adding SpendingSection, adding Transaction to it
        int addedSection1Id = addSpendingSectionGetId(service, token, spendingSection1);

        //deleting added Spending section and adding new spending section
        deleteSpendingSectionByIdGetRs(service, token, "" + addedSection1Id);
        MoneyCalcRs<List<SpendingSection>> addingSection2Rs = addSpendingSectionGetRs(service, token, generateSpendingSection());

        assertTrue(addingSection2Rs.getPayload().stream().noneMatch(section -> section.getName().equals(spendingSection1.getName())),
                "Spending Section was not deleted!");
        assertEquals(getMaxSpendingSectionId(addingSection2Rs.getPayload()), addedSection1Id + 1,
                "Spending Section Id is not incremented!");
    }
}