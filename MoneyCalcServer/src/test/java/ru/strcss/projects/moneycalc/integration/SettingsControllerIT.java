package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SettingsUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.entities.Settings;
import ru.strcss.projects.moneycalc.entities.SpendingSection;
import ru.strcss.projects.moneycalc.integration.utils.IdsContainer;
import ru.strcss.projects.moneycalc.integration.utils.Pair;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.testng.Assert.*;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.*;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSettings;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSpendingSection;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.getMaxSpendingSectionId;

@Slf4j
public class SettingsControllerIT extends AbstractIT {

    /**
     * Settings
     */
    @Test
    public void getSettings() {
        Pair<IdsContainer, String> idsAndToken = savePersonGetIdsAndToken(service);
        IdsContainer idsContainer = idsAndToken.getLeft();
        String token = idsAndToken.getRight();

        MoneyCalcRs<Settings> getSettingsRs = sendRequest(service.getSettings(token), Status.SUCCESS).body();

//        assertEquals(getSettingsRs.getPayload().getId(), idsContainer.getSettingsId(), "wrong settingsId!");
    }

    @Test
    public void settingsUpdate() {
        Pair<String, String> loginAndToken = savePersonGetLoginAndToken(service);
        String token = loginAndToken.getRight();
        Settings newSettings = generateSettings();
        newSettings.setPeriodTo(generateDatePlus(ChronoUnit.YEARS, 1));

        //Updating Settings
        MoneyCalcRs<Settings> updatedRs = sendRequest(service.updateSettings(token, new SettingsUpdateContainer(newSettings)), Status.SUCCESS).body();

        assertNotNull(updatedRs.getPayload(), "Payload is null!");
        assertEquals(newSettings.getPeriodTo(),
                updatedRs.getPayload().getPeriodTo(), "Settings were not updated!");
    }

    /**
     * Spending Sections
     */
    @Test
    public void addSpendingSection() {
        int ignoredId = 6;
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection(1000L, ignoredId);

        MoneyCalcRs<List<SpendingSection>> addSectionRs = addSpendingSectionGetRs(service, token, spendingSection);

        assertTrue(addSectionRs.getPayload().stream().anyMatch(section -> section.getName().equals(spendingSection.getName())),
                "Spending Section was not added!");
        assertTrue(addSectionRs.getPayload().stream().noneMatch(section -> section.getSectionId() == ignoredId));
    }

    @Test
    public void addDuplicateSpendingSections() {
        Pair<String, String> loginAndToken = savePersonGetLoginAndToken(service);
        String token = loginAndToken.getRight();
        SpendingSection spendingSection = generateSpendingSection();

        sendRequest(service.addSpendingSection(token, new SpendingSectionAddContainer(spendingSection)), Status.SUCCESS).body();
        Response<MoneyCalcRs<List<SpendingSection>>> addSectionRs =
                sendRequest(service.addSpendingSection(token, new SpendingSectionAddContainer(spendingSection)));

        assertFalse(addSectionRs.isSuccessful(), "Response is not failed!");
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
            assertEquals(addSectionRs.getPayload().stream()
                            .filter(section -> section.getName().equals(spendingSection.getName()))
                            .findAny().get().getSectionId().intValue(),
                    addSectionRs.getPayload().size(), "Id has been incremented incorrectly!");
        }
    }

    /**
     * Test case when we are adding spending section with name which has deleted section
     */
    @Test
    public void addSpendingSection_sectionWithDeletedName() {
        String token = savePersonGetToken(service);

        List<SpendingSection> sectionsRs = sendRequest(service.getSpendingSections(token), Status.SUCCESS).body().getPayload();
        SpendingSection deletedSection = sectionsRs.get(0);
        String deletedSectionName = deletedSection.getName();

        Response<MoneyCalcRs<List<SpendingSection>>> deleteRs = sendRequest(service.deleteSpendingSection(token, deletedSection.getSectionId()), Status.SUCCESS);
        Response<MoneyCalcRs<List<SpendingSection>>> addRs = sendRequest(service.addSpendingSection(token,
                new SpendingSectionAddContainer(generateSpendingSection(deletedSectionName))),
                Status.SUCCESS);


        // TODO: 15.11.2018 do assertions
    }

    @Test
    public void deleteSpendingSectionById() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection();

        int addedSectionId = addSpendingSectionGetSectionId(service, token, spendingSection);

        MoneyCalcRs<List<SpendingSection>> deleteSectionRs = deleteSpendingSectionByIdGetRs(service, token, addedSectionId);

        assertTrue(deleteSectionRs.getPayload().stream().noneMatch(section -> section.getName().equals(spendingSection.getName())),
                "Spending Section was not deleted!");
    }

    /**
     * Test case checking that spending section was deactivated after deletion, but not removed
     */
    @Test
    public void deleteSpendingSectionById_deactivationCheck() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection1 = generateSpendingSection();

        //Adding SpendingSection, adding Transaction to it
        int addedSection1Id = addSpendingSectionGetSectionId(service, token, spendingSection1);

        //deleting added Spending section and adding new spending section
        deleteSpendingSectionByIdGetRs(service, token, addedSection1Id);
        MoneyCalcRs<List<SpendingSection>> addingSection2Rs = addSpendingSectionGetRs(service, token, generateSpendingSection());

        assertTrue(addingSection2Rs.getPayload().stream().noneMatch(section -> section.getName().equals(spendingSection1.getName())),
                "Spending Section was not deleted!");
        assertEquals(getMaxSpendingSectionId(addingSection2Rs.getPayload()), addedSection1Id + 1,
                "Spending Section Id is not incremented!");

        //getting removed sections
        MoneyCalcRs<List<SpendingSection>> removedSectionsRs = sendRequest(service.getSpendingSectionsWithRemovedOnly(token)).body();
        assertEquals(removedSectionsRs.getPayload().size(), 1, "Removed section size is incorrect!");
        assertEquals(removedSectionsRs.getPayload().get(0).getName(), "#del_" + spendingSection1.getName(),
                "Removed section name is not equal!");
    }

    @Test
    public void updateSpendingSectionById() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection();
        int updatedSectionId = 1;

        MoneyCalcRs<List<SpendingSection>> getSectionsRs = sendRequest(service.getSpendingSections(token), Status.SUCCESS).body();

        String oldName = getSectionsRs.getPayload()
                .stream().filter(section -> section.getSectionId().equals(updatedSectionId)).findAny().get().getName();
        spendingSection.setBudget(ThreadLocalRandom.current().nextLong(1_000_000));

        SpendingSectionUpdateContainer updateContainerById = new SpendingSectionUpdateContainer(updatedSectionId, spendingSection);

        MoneyCalcRs<List<SpendingSection>> updateSectionRs =
                sendRequest(service.updateSpendingSection(token, updateContainerById), Status.SUCCESS).body();

        assertTrue(updateSectionRs.getPayload().stream().noneMatch(section -> section.getName().equals(oldName)),
                "Spending Section was not updated!");
        assertEquals(getSectionsRs.getPayload().size(), updateSectionRs.getPayload().size(), "Number of Spending Sections has changed!");
        assertTrue(updateSectionRs.getPayload().stream().noneMatch(section -> section.getId() == null), "Some Section IDs are null!");
    }

    @Test
    public void updateSpendingSectionById_onlyBudget() {
        String token = savePersonGetToken(service);
        int updatedSectionId = 1;

        List<SpendingSection> getSectionsRs = sendRequest(service.getSpendingSections(token), Status.SUCCESS).body().getPayload();

        Long oldBudget = getSectionsRs
                .stream().filter(section -> section.getSectionId().equals(updatedSectionId)).findAny().get().getBudget();
        //setting new budget
        getSectionsRs.get(updatedSectionId - 1).setBudget(ThreadLocalRandom.current().nextLong(1_000_000));

        SpendingSectionUpdateContainer updateContainerById =
                new SpendingSectionUpdateContainer(updatedSectionId, getSectionsRs.get(updatedSectionId - 1));

        List<SpendingSection> updateSectionRs =
                sendRequest(service.updateSpendingSection(token, updateContainerById), Status.SUCCESS).body().getPayload();

        assertNotEquals(updateSectionRs.get(updatedSectionId).getBudget(), oldBudget,
                "Spending Section was not updated!");
        assertEquals(getSectionsRs.size(), updateSectionRs.size(), "Number of Spending Sections has changed!");
        assertTrue(updateSectionRs.stream().noneMatch(section -> section.getId() == null), "Some Section Ids are null!");
    }

    @Test
    public void updateSpendingSection_duplicateNames() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection1 = generateSpendingSection();
        SpendingSection spendingSection2 = generateSpendingSection();

        addSpendingSectionGetRs(service, token, spendingSection1);
        addSpendingSectionGetRs(service, token, spendingSection2);

        sendRequest(service.updateSpendingSection(token, new SpendingSectionUpdateContainer(spendingSection1.getSectionId(),
                spendingSection2)), Status.ERROR);
    }

    @Test
    public void getSpendingSections() {
        String token = savePersonGetToken(service);

        MoneyCalcRs<List<SpendingSection>> getSectionsRs = sendRequest(service.getSpendingSections(token), Status.SUCCESS).body();

        assertEquals(getSectionsRs.getPayload().size(), 2, "Wrong number of spending sections is returned!");
    }

    @Test
    public void getSpendingSections_withNonAdded() {
        String token = savePersonGetToken(service);
        int nonAddedId = 1;
        SpendingSection spendingSection = new SpendingSection(null, null, nonAddedId, null, "Renamed",
                false, false, 1000L);

        sendRequest(service.updateSpendingSection(token, new SpendingSectionUpdateContainer(nonAddedId, spendingSection)), Status.SUCCESS);

        MoneyCalcRs<List<SpendingSection>> getSectionsRs = sendRequest(service.getSpendingSectionsWithNonAdded(token), Status.SUCCESS).body();

        assertEquals(getSectionsRs.getPayload().size(), 2, "Wrong number of spending sections is returned!");
        assertFalse(getSectionsRs.getPayload().get(nonAddedId).getIsAdded(), "Section is still added!");
    }

    @Test
    public void getSpendingSections_withRemoved() {
        String token = savePersonGetToken(service);
        int removedId = 1;

        sendRequest(service.deleteSpendingSection(token, removedId), Status.SUCCESS);

        MoneyCalcRs<List<SpendingSection>> getWithRemovedSectionsRs =
                sendRequest(service.getSpendingSectionsWithRemoved(token), Status.SUCCESS).body();
        assertEquals(getWithRemovedSectionsRs.getPayload().size(), 2, "Wrong number of spending sections is returned!");
        assertTrue(getWithRemovedSectionsRs.getPayload().get(removedId).getIsRemoved(), "Section is not removed!");

        MoneyCalcRs<List<SpendingSection>> getRemovedSectionsOnlyRs =
                sendRequest(service.getSpendingSectionsWithRemovedOnly(token), Status.SUCCESS).body();
        assertEquals(getRemovedSectionsOnlyRs.getPayload().size(), 1, "Wrong number of spending sections is returned!");
        assertTrue(getRemovedSectionsOnlyRs.getPayload().get(removedId).getIsRemoved(), "Section is not removed!");
    }

    @Test
    public void getSpendingSections_logoId() {
        String token = savePersonGetToken(service);

        SpendingSection spendingSection = generateSpendingSection();
        Integer savedLogoId = spendingSection.getLogoId();

        int sectionId = addSpendingSectionGetSectionId(service, token, spendingSection);

        MoneyCalcRs<List<SpendingSection>> getSectionsRs = sendRequest(service.getSpendingSections(token), Status.SUCCESS).body();

        Integer logoId = getSectionsRs.getPayload().stream()
                .filter(section -> section.getSectionId().equals(sectionId))
                .map(SpendingSection::getLogoId)
                .findAny()
                .orElseThrow(() -> new RuntimeException("No item found"));
        assertEquals(logoId, savedLogoId, "Logo Id is not the same!");
    }
}