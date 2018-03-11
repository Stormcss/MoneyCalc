package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection;

import com.mongodb.BasicDBObject;
import com.mongodb.WriteResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.testng.annotations.BeforeClass;
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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static ru.strcss.projects.moneycalc.testutils.Generator.*;

public class SettingsDBConnectionTest {

    private PersonRepository repository = mock(PersonRepository.class);
    private MongoTemplate mongoTemplate = mock(MongoTemplate.class);
    private SettingsDBConnection settingsDBConnection;

    @BeforeClass(groups = "SuccessfulScenario")
    public void setUp() {
        when(repository.findSettingsByAccess_Login(anyString()))
                .thenReturn(Person.builder().settings(generateSettings(UUID(), true)).build());
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

    @Test(groups = "SuccessfulScenario")
    public void testGetSettings() {
        Settings settings = settingsDBConnection.getSettings("login");

        assertNotNull(settings, "Settings is null!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testUpdateSettings() {
        WriteResult writeResult = settingsDBConnection.updateSettings(generateSettings("login", false));

        assertNotNull(writeResult, "WriteResult is null!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testAddSpendingSection() {
        WriteResult writeResult = settingsDBConnection.addSpendingSection("login", new SpendingSectionAddContainer(generateSpendingSection()));

        assertNotNull(writeResult, "WriteResult is null!");
    }

    @Test
    public void testDeleteSpendingSectionByName() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer("deleteContainer", SpendingSectionSearchType.BY_NAME);

        WriteResult writeResult = settingsDBConnection.deleteSpendingSectionByName("login", deleteContainer);

        assertNotNull(writeResult, "WriteResult is null!");
    }

    @Test
    public void testDeleteSpendingSectionById() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer("1", SpendingSectionSearchType.BY_ID);

        WriteResult writeResult = settingsDBConnection.deleteSpendingSectionById("login", deleteContainer);

        assertNotNull(writeResult, "WriteResult is null!");

    }

    @Test
    public void testUpdateSpendingSectionById() {
        SpendingSectionUpdateContainer updateContainer = new SpendingSectionUpdateContainer("1", generateSpendingSection(), SpendingSectionSearchType.BY_ID);

        WriteResult writeResult = settingsDBConnection.updateSpendingSectionById("login", updateContainer);

        assertNotNull(writeResult, "WriteResult is null!");
    }

    @Test
    public void testUpdateSpendingSectionByName() {
        SpendingSectionUpdateContainer updateContainer = new SpendingSectionUpdateContainer("Name", generateSpendingSection(), SpendingSectionSearchType.BY_NAME);

        WriteResult writeResult = settingsDBConnection.updateSpendingSectionByName("login", updateContainer);

        assertNotNull(writeResult, "WriteResult is null!");
    }

    @Test
    public void testGetSpendingSectionList() {
        List<SpendingSection> sectionList = settingsDBConnection.getSpendingSectionList("login");

        assertNotNull(sectionList, "SectionList is null!");
        assertTrue(sectionList.size() > 0, "SectionList is empty!");
    }

    @Test
    public void testGetSpendingSectionByName() {
        SpendingSection writeResult = settingsDBConnection.getSpendingSectionByName("login", "sectionName");

        assertNotNull(writeResult, "WriteResult is null!");
    }

    @Test(enabled = false)
    public void testGetSpendingSectionByID() {
        SpendingSection writeResult = settingsDBConnection.getSpendingSectionByID("login", 1);

        assertNotNull(writeResult, "WriteResult is null!");
    }

    @Test(enabled = false)
    public void testGetMaxSpendingSectionId() {
        Integer maxSpendingSectionId = settingsDBConnection.getMaxSpendingSectionId("login");

        assertNotNull(maxSpendingSectionId, "maxSpendingSectionId is null!");
        assertTrue(maxSpendingSectionId >= 0, "maxSpendingSectionId is < 0!");

    }

    @Test(enabled = false)
    public void testIsSpendingSectionNameNew() {
    }

    @Test(enabled = false)
    public void testIsSpendingSectionIDExists() {
    }
}