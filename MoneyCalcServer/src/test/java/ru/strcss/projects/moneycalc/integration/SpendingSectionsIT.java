package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.integration.utils.Pair;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Status;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.addSpendingSectionGetRs;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.addSpendingSectionGetSectionId;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.deleteSpendingSectionByIdGetRs;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.savePersonGetLoginAndToken;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.savePersonGetToken;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.sendRequest;
import static ru.strcss.projects.moneycalc.moneycalcdto.dto.Status.SUCCESS;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSpendingSection;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.getMaxSpendingSectionId;

/**
 * Created by Stormcss
 * Date: 15.12.2018
 */
@Slf4j
public class SpendingSectionsIT extends AbstractIT {
    private final String SECTION_NOT_REMOVED = "Section is not removed!";
    private final String SECTION_NOT_ADDED = "Section is not added!";
    private final String SECTION_NOT_UPDATED = "Section is not updated!";

    @Test
    public void addSpendingSection() {
        int ignoredId = 6;
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection(1000L, ignoredId);

        MoneyCalcRs<List<SpendingSection>> addSectionRs = addSpendingSectionGetRs(service, token, spendingSection);

        assertTrue(addSectionRs.getPayload().stream().anyMatch(section -> section.getName().equals(spendingSection.getName())),
                SECTION_NOT_ADDED);
        assertTrue(addSectionRs.getPayload().stream().noneMatch(section -> section.getSectionId() == ignoredId));
    }

    @Test
    public void addDuplicateSpendingSections() {
        Pair<String, String> loginAndToken = savePersonGetLoginAndToken(service);
        String token = loginAndToken.getRight();
        SpendingSection spendingSection = generateSpendingSection();

        sendRequest(service.addSpendingSection(token, spendingSection), SUCCESS).body();
        Response<MoneyCalcRs<List<SpendingSection>>> addSectionRs =
                sendRequest(service.addSpendingSection(token, spendingSection));

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
                            .equals(spendingSection.getName())), SECTION_NOT_ADDED);
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

        List<SpendingSection> sectionsRs =
                sendRequest(service.getSpendingSections(token), SUCCESS).body().getPayload();
        SpendingSection deletedSection = sectionsRs.get(0);
        String deletedSectionName = deletedSection.getName();

        MoneyCalcRs<List<SpendingSection>> deleteRs =
                sendRequest(service.deleteSpendingSection(token, deletedSection.getSectionId()), SUCCESS).body();
        assertTrue(deleteRs.getPayload().stream().noneMatch(sections -> sections.getName().equals(deletedSectionName)), SECTION_NOT_REMOVED);

        MoneyCalcRs<List<SpendingSection>> addRs = sendRequest(service.addSpendingSection(token,
                generateSpendingSection(deletedSectionName)), SUCCESS).body();
        assertEquals(addRs.getPayload().get(addRs.getPayload().size() - 1).getName(), deletedSectionName, SECTION_NOT_ADDED);

        MoneyCalcRs<List<SpendingSection>> removedSections = sendRequest(service.getSpendingSectionsWithRemovedOnly(token), SUCCESS).body();

        assertEquals(removedSections.getPayload().get(0).getName(), "#del_" + deletedSectionName, "Deleted section has wrong name!");
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
                SECTION_NOT_REMOVED);
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

        MoneyCalcRs<List<SpendingSection>> getSectionsRs = sendRequest(service.getSpendingSections(token), SUCCESS).body();

        String oldName = getSectionsRs.getPayload()
                .stream().filter(section -> section.getSectionId().equals(updatedSectionId)).findAny().get().getName();
        spendingSection.setBudget(ThreadLocalRandom.current().nextLong(1_000_000));

        SpendingSectionUpdateContainer updateContainerById = new SpendingSectionUpdateContainer(updatedSectionId, spendingSection);

        MoneyCalcRs<List<SpendingSection>> updateSectionRs =
                sendRequest(service.updateSpendingSection(token, updateContainerById), SUCCESS).body();

        assertTrue(updateSectionRs.getPayload().stream().noneMatch(section -> section.getName().equals(oldName)),
                "Spending Section was not updated!");
        assertEquals(getSectionsRs.getPayload().size(), updateSectionRs.getPayload().size(), "Number of Spending Sections has changed!");
        assertTrue(updateSectionRs.getPayload().stream().noneMatch(section -> section.getId() == null), "Some Section IDs are null!");
    }

    @Test
    public void updateSpendingSectionById_onlyBudget() {
        String token = savePersonGetToken(service);
        int updatedSectionId = 1;

        List<SpendingSection> getSectionsRs = sendRequest(service.getSpendingSections(token), SUCCESS).body().getPayload();

        Long oldBudget = getSectionsRs
                .stream().filter(section -> section.getSectionId().equals(updatedSectionId)).findAny().get().getBudget();
        //setting new budget
        getSectionsRs.get(updatedSectionId - 1).setBudget(ThreadLocalRandom.current().nextLong(1_000_000));

        SpendingSectionUpdateContainer updateContainerById =
                new SpendingSectionUpdateContainer(updatedSectionId, getSectionsRs.get(updatedSectionId - 1));

        List<SpendingSection> updateSectionRs =
                sendRequest(service.updateSpendingSection(token, updateContainerById), SUCCESS).body().getPayload();

        assertNotEquals(updateSectionRs.get(updatedSectionId - 1).getBudget(), oldBudget, SECTION_NOT_UPDATED);
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

    /**
     * Checking that updating single SpendingSection won't lead to updating others
     */
    @Test
    public void shouldUpdateOnlyOneSpendingSection() {
        String token = savePersonGetToken(service);

        String name1 = "name";
        String newName = "newName!";

        addSpendingSectionGetRs(service, token, generateSpendingSection(name1));
        Integer updatedSectionId = addSpendingSectionGetRs(service, token, generateSpendingSection("name1"))
                .getPayload().stream().reduce((first, second) -> second).orElse(null).getSectionId();

        SpendingSection newSpendingSection = generateSpendingSection(newName);
        sendRequest(service.updateSpendingSection(token, new SpendingSectionUpdateContainer(updatedSectionId, newSpendingSection)), SUCCESS).body();

        List<SpendingSection> spendingSectionList = sendRequest(service.getSpendingSections(token), SUCCESS).body().getPayload();

        assertEquals(spendingSectionList.get(2).getName(), name1, "Old SpendingSection name has changed!");
        assertEquals(spendingSectionList.get(3).getName(), newName, "SpendingSection name has not changed!");
    }

    @Test
    public void getSpendingSections() {
        String token = savePersonGetToken(service);

        MoneyCalcRs<List<SpendingSection>> getSectionsRs = sendRequest(service.getSpendingSections(token), SUCCESS).body();

        assertEquals(getSectionsRs.getPayload().size(), 2, "Wrong number of spending sections is returned!");
    }

    @Test
    public void getSpendingSections_withNonAdded() {
        String token = savePersonGetToken(service);
        int nonAddedId = 1;
        SpendingSection spendingSection = new SpendingSection(null, null, nonAddedId, null, "Renamed",
                false, false, 1000L);

        sendRequest(service.updateSpendingSection(token, new SpendingSectionUpdateContainer(nonAddedId, spendingSection)), SUCCESS);

        MoneyCalcRs<List<SpendingSection>> getSectionsRs = sendRequest(service.getSpendingSectionsWithNonAdded(token), SUCCESS).body();

        assertEquals(getSectionsRs.getPayload().size(), 2, "Wrong number of spending sections is returned!");
        assertFalse(getSectionsRs.getPayload().get(nonAddedId - 1).getIsAdded(), "Section is still added!");
    }

    @Test
    public void getSpendingSections_withRemoved() {
        String token = savePersonGetToken(service);
        int removedId = 1;

        sendRequest(service.deleteSpendingSection(token, removedId), SUCCESS);

        MoneyCalcRs<List<SpendingSection>> getWithRemovedSectionsRs =
                sendRequest(service.getSpendingSectionsWithRemoved(token), SUCCESS).body();
        assertEquals(getWithRemovedSectionsRs.getPayload().size(), 2, "Wrong number of spending sections is returned!");
        assertTrue(getWithRemovedSectionsRs.getPayload().get(removedId - 1).getIsRemoved(), SECTION_NOT_REMOVED);

        MoneyCalcRs<List<SpendingSection>> getRemovedSectionsOnlyRs =
                sendRequest(service.getSpendingSectionsWithRemovedOnly(token), SUCCESS).body();
        assertEquals(getRemovedSectionsOnlyRs.getPayload().size(), 1, "Wrong number of spending sections is returned!");
        assertTrue(getRemovedSectionsOnlyRs.getPayload().get(removedId - 1).getIsRemoved(), SECTION_NOT_REMOVED);
    }

    @Test
    public void getSpendingSections_logoId() {
        String token = savePersonGetToken(service);

        SpendingSection spendingSection = generateSpendingSection();
        Integer savedLogoId = spendingSection.getLogoId();

        int sectionId = addSpendingSectionGetSectionId(service, token, spendingSection);

        MoneyCalcRs<List<SpendingSection>> getSectionsRs = sendRequest(service.getSpendingSections(token), SUCCESS).body();

        Integer logoId = getSectionsRs.getPayload().stream()
                .filter(section -> section.getSectionId().equals(sectionId))
                .map(SpendingSection::getLogoId)
                .findAny()
                .orElseThrow(() -> new RuntimeException("No item found"));
        assertEquals(logoId, savedLogoId, "Logo Id is not the same!");
    }
}
