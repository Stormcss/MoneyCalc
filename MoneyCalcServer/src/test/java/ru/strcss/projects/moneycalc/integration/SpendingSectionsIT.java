package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.integration.utils.Pair;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
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

        SpendingSectionsSearchRs addSectionRs = addSpendingSectionGetRs(service, token, spendingSection);

        assertTrue(addSectionRs.getItems().stream().anyMatch(section -> section.getName().equals(spendingSection.getName())),
                SECTION_NOT_ADDED);
        assertTrue(addSectionRs.getItems().stream().noneMatch(section -> section.getSectionId() == ignoredId));
    }

    @Test
    public void addDuplicateSpendingSections() {
        Pair<String, String> loginAndToken = savePersonGetLoginAndToken(service);
        String token = loginAndToken.getRight();
        SpendingSection spendingSection = generateSpendingSection();

        sendRequest(service.addSpendingSection(token, spendingSection));
        Response<SpendingSectionsSearchRs> addSectionRs = sendRequest(service.addSpendingSection(token, spendingSection),
                HttpStatus.BAD_REQUEST);

        assertFalse(addSectionRs.isSuccessful(), "Response is not failed!");
    }

    @Test
    public void addSpendingSectionCorrectIdIncrement() {
        int addedSectionsNum = 10;
        String token = savePersonGetToken(service);

        for (int i = 0; i < addedSectionsNum; i++) {
            SpendingSection spendingSection = generateSpendingSection();
            SpendingSectionsSearchRs addSectionRs = addSpendingSectionGetRs(service, token, spendingSection);

            assertTrue(addSectionRs.getItems().stream()
                    .anyMatch(section -> section.getName()
                            .equals(spendingSection.getName())), SECTION_NOT_ADDED);
            assertEquals(addSectionRs.getItems().stream()
                            .filter(section -> section.getName().equals(spendingSection.getName()))
                            .findAny().get().getSectionId().intValue(),
                    addSectionRs.getItems().size(), "Id has been incremented incorrectly!");
        }
    }

    /**
     * Test case when we are adding spending section with name which has deleted section
     */
    @Test
    public void addSpendingSectionSectionWithDeletedName() {
        String token = savePersonGetToken(service);

        List<SpendingSection> sectionsRs = sendRequest(service.getSpendingSections(token)).body().getItems();
        SpendingSection deletedSection = sectionsRs.get(0);
        String deletedSectionName = deletedSection.getName();

        SpendingSectionsSearchRs deleteRs =
                sendRequest(service.deleteSpendingSection(token, deletedSection.getSectionId())).body();
        assertTrue(deleteRs.getItems().stream().noneMatch(sections -> sections.getName().equals(deletedSectionName)), SECTION_NOT_REMOVED);

        SpendingSectionsSearchRs addRs = sendRequest(service.addSpendingSection(token,
                generateSpendingSection(deletedSectionName))).body();
        assertEquals(addRs.getItems().get(addRs.getItems().size() - 1).getName(), deletedSectionName, SECTION_NOT_ADDED);

        SpendingSectionsSearchRs removedSections = sendRequest(service.getSpendingSectionsWithRemovedOnly(token)).body();

        assertEquals(removedSections.getItems().get(0).getName(), "#del_" + deletedSectionName, "Deleted section has wrong name!");
    }

    @Test
    public void deleteSpendingSectionById() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection();

        int addedSectionId = addSpendingSectionGetSectionId(service, token, spendingSection);

        SpendingSectionsSearchRs deleteSectionRs = deleteSpendingSectionByIdGetRs(service, token, addedSectionId);

        assertTrue(deleteSectionRs.getItems().stream().noneMatch(section -> section.getName().equals(spendingSection.getName())),
                "Spending Section was not deleted!");
    }

    /**
     * Test case checking that spending section was deactivated after deletion, but not removed
     */
    @Test
    public void deleteSpendingSectionByIdDeactivationCheck() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection1 = generateSpendingSection();

        //Adding SpendingSection, adding Transaction to it
        int addedSection1Id = addSpendingSectionGetSectionId(service, token, spendingSection1);

        //deleting added Spending section and adding new spending section
        deleteSpendingSectionByIdGetRs(service, token, addedSection1Id);
        SpendingSectionsSearchRs addingSection2Rs = addSpendingSectionGetRs(service, token, generateSpendingSection());

        assertTrue(addingSection2Rs.getItems().stream().noneMatch(section -> section.getName().equals(spendingSection1.getName())),
                SECTION_NOT_REMOVED);
        assertEquals(getMaxSpendingSectionId(addingSection2Rs.getItems()), addedSection1Id + 1,
                "Spending Section Id is not incremented!");

        //getting removed sections
        SpendingSectionsSearchRs removedSectionsRs = sendRequest(service.getSpendingSectionsWithRemovedOnly(token)).body();
        assertEquals(removedSectionsRs.getItems().size(), 1, "Removed section size is incorrect!");
        assertEquals(removedSectionsRs.getItems().get(0).getName(), "#del_" + spendingSection1.getName(),
                "Removed section name is not equal!");
    }

    @Test
    public void updateSpendingSectionById() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection = generateSpendingSection();
        int updatedSectionId = 1;

        SpendingSectionsSearchRs getSectionsRs = sendRequest(service.getSpendingSections(token)).body();

        String oldName = getSectionsRs.getItems()
                .stream().filter(section -> section.getSectionId().equals(updatedSectionId)).findAny().get().getName();
        spendingSection.setBudget(ThreadLocalRandom.current().nextLong(1_000_000));

        SpendingSectionUpdateContainer updateContainerById = new SpendingSectionUpdateContainer(updatedSectionId, spendingSection);

        SpendingSectionsSearchRs updateSectionRs =
                sendRequest(service.updateSpendingSection(token, updateContainerById)).body();

        assertTrue(updateSectionRs.getItems().stream().noneMatch(section -> section.getName().equals(oldName)),
                "Spending Section was not updated!");
        assertEquals(getSectionsRs.getItems().size(), updateSectionRs.getItems().size(), "Number of Spending Sections has changed!");
        assertEquals(updateSectionRs.getItems().size(), (int) updateSectionRs.getCount(), "Incorrect count value!");
        assertTrue(updateSectionRs.getItems().stream().noneMatch(section -> section.getId() == null), "Some Section IDs are null!");
    }

    @Test
    public void updateSpendingSectionByIdOnlyBudget() {
        String token = savePersonGetToken(service);
        int updatedSectionId = 1;

        List<SpendingSection> getSectionsRs = sendRequest(service.getSpendingSections(token)).body().getItems();

        Long oldBudget = getSectionsRs
                .stream().filter(section -> section.getSectionId().equals(updatedSectionId)).findAny().get().getBudget();
        //setting new budget
        getSectionsRs.get(updatedSectionId - 1).setBudget(ThreadLocalRandom.current().nextLong(1_000_000));

        SpendingSectionUpdateContainer updateContainerById =
                new SpendingSectionUpdateContainer(updatedSectionId, getSectionsRs.get(updatedSectionId - 1));

        List<SpendingSection> updateSectionRs =
                sendRequest(service.updateSpendingSection(token, updateContainerById)).body().getItems();

        assertNotEquals(updateSectionRs.get(updatedSectionId - 1).getBudget(), oldBudget, SECTION_NOT_UPDATED);
        assertEquals(getSectionsRs.size(), updateSectionRs.size(), "Number of Spending Sections has changed!");
        assertTrue(updateSectionRs.stream().noneMatch(section -> section.getId() == null), "Some Section Ids are null!");
    }

    @Test
    public void updateSpendingSectionDuplicateNames() {
        String token = savePersonGetToken(service);
        SpendingSection spendingSection1 = generateSpendingSection();
        SpendingSection spendingSection2 = generateSpendingSection();

        addSpendingSectionGetRs(service, token, spendingSection1);
        addSpendingSectionGetRs(service, token, spendingSection2);

        sendRequest(service.updateSpendingSection(token, new SpendingSectionUpdateContainer(spendingSection1.getSectionId(),
                spendingSection2)), HttpStatus.BAD_REQUEST);
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
                .getItems().stream().reduce((first, second) -> second).orElse(null).getSectionId();

        SpendingSection newSpendingSection = generateSpendingSection(newName);
        sendRequest(service.updateSpendingSection(token, new SpendingSectionUpdateContainer(updatedSectionId, newSpendingSection)));

        List<SpendingSection> spendingSectionList = sendRequest(service.getSpendingSections(token)).body().getItems();

        assertEquals(spendingSectionList.get(2).getName(), name1, "Old SpendingSection name has changed!");
        assertEquals(spendingSectionList.get(3).getName(), newName, "SpendingSection name has not changed!");
    }

    @Test
    public void getSpendingSections() {
        String token = savePersonGetToken(service);

        SpendingSectionsSearchRs getSectionsRs = sendRequest(service.getSpendingSections(token)).body();

        assertEquals(getSectionsRs.getItems().size(), 2, "Wrong number of spending sections is returned!");
    }

    @Test
    public void getSpendingSectionsWithNonAdded() {
        String token = savePersonGetToken(service);
        int nonAddedId = 1;
        SpendingSection spendingSection = new SpendingSection(null, null, nonAddedId, null, "Renamed",
                false, false, 1000L);

        sendRequest(service.updateSpendingSection(token, new SpendingSectionUpdateContainer(nonAddedId, spendingSection)));

        SpendingSectionsSearchRs getSectionsRs = sendRequest(service.getSpendingSectionsWithNonAdded(token)).body();

        assertEquals(getSectionsRs.getItems().size(), 2, "Wrong number of spending sections is returned!");
        assertFalse(getSectionsRs.getItems().get(nonAddedId - 1).getIsAdded(), "Section is still added!");
    }

    @Test
    public void getSpendingSectionsWithRemoved() {
        String token = savePersonGetToken(service);
        int removedId = 1;

        sendRequest(service.deleteSpendingSection(token, removedId));

        SpendingSectionsSearchRs getWithRemovedSectionsRs =
                sendRequest(service.getSpendingSectionsWithRemoved(token)).body();
        assertEquals(getWithRemovedSectionsRs.getItems().size(), 2, "Wrong number of spending sections is returned!");
        assertTrue(getWithRemovedSectionsRs.getItems().get(removedId - 1).getIsRemoved(), SECTION_NOT_REMOVED);

        SpendingSectionsSearchRs getRemovedSectionsOnlyRs =
                sendRequest(service.getSpendingSectionsWithRemovedOnly(token)).body();
        assertEquals(getRemovedSectionsOnlyRs.getItems().size(), 1, "Wrong number of spending sections is returned!");
        assertTrue(getRemovedSectionsOnlyRs.getItems().get(removedId - 1).getIsRemoved(), SECTION_NOT_REMOVED);
    }

    @Test
    public void getSpendingSectionsLogoId() {
        String token = savePersonGetToken(service);

        SpendingSection spendingSection = generateSpendingSection();
        Integer savedLogoId = spendingSection.getLogoId();

        int sectionId = addSpendingSectionGetSectionId(service, token, spendingSection);

        SpendingSectionsSearchRs getSectionsRs = sendRequest(service.getSpendingSections(token)).body();

        Integer logoId = getSectionsRs.getItems().stream()
                .filter(section -> section.getSectionId().equals(sectionId))
                .map(SpendingSection::getLogoId)
                .findAny()
                .orElseThrow(() -> new RuntimeException("No item found"));
        assertEquals(logoId, savedLogoId, "Logo Id is not the same!");
    }
}
