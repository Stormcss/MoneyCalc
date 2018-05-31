package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection;

import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.crudcontainers.SpendingSectionSearchType;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.mongo.PersonRepository;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;
import static ru.strcss.projects.moneycalc.testutils.Generator.*;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.assertReturnedSectionsOrder;

public class SettingsDBConnectionTest {

    private PersonRepository repository = mock(PersonRepository.class);
    private MongoTemplate mongoTemplate = mock(MongoTemplate.class);
    private SettingsDBConnection settingsDBConnection;

    private int sectionsCount = 5;

    @BeforeGroups(groups = "SuccessfulScenario")
    public void setUp() {
        when(repository.findSettingsByAccess_Login(anyString()))
                .thenReturn(Person.builder().settings(generateSettings(UUID(), true, true)).build());
        when(mongoTemplate.updateMulti(any(Query.class), any(Update.class), anyString()))
                .thenReturn(new WriteResult(1, false, new Object()));
        when(mongoTemplate.updateMulti(any(Query.class), any(Update.class), eq(Person.class)))
                .thenReturn(new WriteResult(1, false, new Object()));
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), anyString()))
                .thenReturn(new WriteResult(1, false, new Object()));
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(Person.class)))
                .thenReturn(new WriteResult(1, false, new Object()));

        when(mongoTemplate.aggregate(any(Aggregation.class), eq(Person.class), eq(SpendingSection.class)))
                .thenReturn(new AggregationResults<>(Arrays.asList(generateSpendingSection()), new BasicDBObject() {
                }));

        settingsDBConnection = new SettingsDBConnection(repository, mongoTemplate);
    }

    @BeforeGroups(groups = "maxIdCheck")
    public void setUp_maxIdCheck() {
        when(mongoTemplate.aggregate(any(Aggregation.class), eq(Person.class), eq(SpendingSection.class)))
                .thenReturn(new AggregationResults<>(generateSpendingSectionList(sectionsCount, false), new BasicDBObject() {
                }));

        settingsDBConnection = new SettingsDBConnection(repository, mongoTemplate);
    }

    @BeforeGroups(groups = "sectionsReversed")
    public void setUp_sectionsReversed() {
        when(repository.findSettingsByAccess_Login(anyString()))
                .thenReturn(Person.builder().settings(generateSettings(UUID(), true, false)).build());

        settingsDBConnection = new SettingsDBConnection(repository, mongoTemplate);
    }

    @Test(groups = "SuccessfulScenario")
    public void testGetSettings() {
        Settings settings = settingsDBConnection.getSettings("login");

        assertNotNull(settings, "Settings is null!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testUpdateSettings() {
        Settings settings = generateSettings("login", false, false);

        WriteResult writeResult = settingsDBConnection.updateSettings(settings);

        assertNotNull(writeResult, "WriteResult is null!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testAddSpendingSection() {
        SpendingSectionAddContainer spendingSectionContainer = new SpendingSectionAddContainer(generateSpendingSection());

        WriteResult writeResult = settingsDBConnection.addSpendingSection("login", spendingSectionContainer);

        assertNotNull(writeResult, "WriteResult is null!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testDeleteSpendingSectionByName() {
        SpendingSectionDeleteContainer deleteContainer =
                new SpendingSectionDeleteContainer("deleteContainer", SpendingSectionSearchType.BY_NAME);

        WriteResult writeResult = settingsDBConnection.deleteSpendingSectionByName("login", deleteContainer);

        assertNotNull(writeResult, "WriteResult is null!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testDeleteSpendingSectionById() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer("1", SpendingSectionSearchType.BY_ID);

        WriteResult writeResult = settingsDBConnection.deleteSpendingSectionById("login", deleteContainer);

        assertNotNull(writeResult, "WriteResult is null!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testUpdateSpendingSectionById() {
        SpendingSectionUpdateContainer updateContainer = new SpendingSectionUpdateContainer("1", generateSpendingSection(), SpendingSectionSearchType.BY_ID);

        WriteResult writeResult = settingsDBConnection.updateSpendingSectionById("login", updateContainer);

        assertNotNull(writeResult, "WriteResult is null!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testUpdateSpendingSectionByName() {
        SpendingSectionUpdateContainer updateContainer = new SpendingSectionUpdateContainer("Name", generateSpendingSection(), SpendingSectionSearchType.BY_NAME);

        WriteResult writeResult = settingsDBConnection.updateSpendingSectionByName("login", updateContainer);

        assertNotNull(writeResult, "WriteResult is null!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testGetSpendingSectionList() {
        List<SpendingSection> sectionList = settingsDBConnection.getSpendingSectionList("login");

        assertNotNull(sectionList, "SectionList is null!");
        assertTrue(sectionList.size() > 0, "SectionList is empty!");
        assertReturnedSectionsOrder(sectionList);
    }

    @Test(groups = "SuccessfulScenario")
    public void testGetSpendingSectionByName() {
        SpendingSection writeResult = settingsDBConnection.getSpendingSectionByName("login", "sectionName");

        assertNotNull(writeResult, "WriteResult is null!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testGetSpendingSectionByID() {
        SpendingSection writeResult = settingsDBConnection.getSpendingSectionByID("login", 1);

        assertNotNull(writeResult, "WriteResult is null!");
    }

    @Test(groups = "maxIdCheck", dependsOnGroups = "SuccessfulScenario")
    public void testGetMaxSpendingSectionId() {
        Integer maxSpendingSectionId = settingsDBConnection.getMaxSpendingSectionId("login");

        assertNotNull(maxSpendingSectionId, "maxSpendingSectionId is null!");
        assertEquals((int) maxSpendingSectionId, sectionsCount - 1, "maxSpendingSectionId is incorrect!");
    }

    @Test(groups = "sectionsReversed", dependsOnGroups = "SuccessfulScenario")
    public void testGetSpendingSectionList_sectionsReversed() {
        List<SpendingSection> sectionList = settingsDBConnection.getSpendingSectionList("login");

        assertNotNull(sectionList, "SectionList is null!");
        assertTrue(sectionList.size() > 0, "SectionList is empty!");
        assertReturnedSectionsOrder(sectionList);
    }

}