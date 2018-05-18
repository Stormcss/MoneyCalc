package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import com.mongodb.WriteResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.SpendingSectionSearchType;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SettingsUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.SettingsDBConnection;
import ru.strcss.projects.moneycalc.moneycalcserver.mongo.PersonRepository;
import ru.strcss.projects.moneycalc.testutils.Generator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static ru.strcss.projects.moneycalc.testutils.Generator.*;

public class SettingsControllerTest {

    private SettingsDBConnection settingsDBConnection = mock(SettingsDBConnection.class);
    private PersonRepository repository = mock(PersonRepository.class);
    private SettingsController settingsController;

    @BeforeClass
    public void setUp() {
        User user = new User("login", "password", Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @BeforeGroups(groups = {"SuccessfulScenario", "incorrectContainers"})
    public void prepare_successfulScenario() {
        when(settingsDBConnection.updateSettings(any(Settings.class))).thenReturn(new WriteResult(1, true, new Object()));
        when(settingsDBConnection.getSettings(anyString())).thenReturn(generateSettings(UUID(), true));

        when(settingsDBConnection.addSpendingSection(anyString(), any(SpendingSectionAddContainer.class)))
                .thenReturn(new WriteResult(1, false, null));
        when(settingsDBConnection.getSpendingSectionList(anyString()))
                .thenReturn(Stream.generate(Generator::generateSpendingSection).limit(2).collect(Collectors.toList()));
        when(settingsDBConnection.updateSpendingSectionById(anyString(), any(SpendingSectionUpdateContainer.class)))
                .thenReturn(new WriteResult(1, true, null));
        when(settingsDBConnection.updateSpendingSectionByName(anyString(), any(SpendingSectionUpdateContainer.class)))
                .thenReturn(new WriteResult(1, true, null));
        when(settingsDBConnection.deleteSpendingSectionById(anyString(), any(SpendingSectionDeleteContainer.class)))
                .thenReturn(new WriteResult(1, true, null));
        when(settingsDBConnection.deleteSpendingSectionByName(anyString(), any(SpendingSectionDeleteContainer.class)))
                .thenReturn(new WriteResult(1, true, null));

        when(settingsDBConnection.isSpendingSectionNameNew(anyString(), anyString())).thenReturn(true);

        when(repository.existsByAccess_Login(anyString())).thenReturn(true);

        settingsController = new SettingsController(settingsDBConnection, repository);
    }

    @Test(groups = "SuccessfulScenario")
    public void testSaveSettings() {
        ResponseEntity<MoneyCalcRs<Settings>> settingsSaveRs = settingsController.saveSettings(
                new SettingsUpdateContainer(generateSettings(UUID(), false)));
        assertEquals(settingsSaveRs.getBody().getServerStatus(), Status.SUCCESS, "Settings were not saved!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testGetSettings() {
        ResponseEntity<MoneyCalcRs<Settings>> settingsGetRs = settingsController.getSettings();

        assertEquals(settingsGetRs.getBody().getServerStatus(), Status.SUCCESS, settingsGetRs.getBody().getMessage());
    }

    @Test(groups = "SuccessfulScenario")
    public void testAddSpendingSection() {
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionAddRs = settingsController.addSpendingSection(
                new SpendingSectionAddContainer(generateSpendingSection()));

        assertEquals(sectionAddRs.getBody().getServerStatus(), Status.SUCCESS, sectionAddRs.getBody().getMessage());
    }

    @Test(groups = "SuccessfulScenario")
    public void testUpdateSpendingSection_byName_ExistingName() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer("Name", generateSpendingSection("Name"), SpendingSectionSearchType.BY_NAME);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getBody().getServerStatus(), Status.SUCCESS, sectionUpdateRs.getBody().getMessage());
    }

    @Test(groups = "SuccessfulScenario")
    public void testUpdateSpendingSection_byName_NewName() {
        SpendingSectionUpdateContainer nameUpdateContainer = 
                new SpendingSectionUpdateContainer("Name", generateSpendingSection("newName"), SpendingSectionSearchType.BY_NAME);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getBody().getServerStatus(), Status.SUCCESS, sectionUpdateRs.getBody().getMessage());
    }

    @Test(groups = "SuccessfulScenario")
    public void testUpdateSpendingSection_byId_ExistingId() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer("1", generateSpendingSection(5000, 1), SpendingSectionSearchType.BY_ID);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getBody().getServerStatus(), Status.SUCCESS, sectionUpdateRs.getBody().getMessage());
    }

    @Test(groups = "SuccessfulScenario")
    public void testDeleteSpendingSection_byId() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer("1", SpendingSectionSearchType.BY_ID);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionDeleteRs = settingsController.deleteSpendingSection(deleteContainer);

        assertEquals(sectionDeleteRs.getBody().getServerStatus(), Status.SUCCESS, sectionDeleteRs.getBody().getMessage());
        assertTrue(sectionDeleteRs.getBody().getPayload().size() > 0, "Size of returned sections is 0!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testDeleteSpendingSection_byName() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer("name", SpendingSectionSearchType.BY_NAME);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionDeleteRs = settingsController.deleteSpendingSection(deleteContainer);

        assertEquals(sectionDeleteRs.getBody().getServerStatus(), Status.SUCCESS, sectionDeleteRs.getBody().getMessage());
        assertTrue(sectionDeleteRs.getBody().getPayload().size() > 0, "Size of returned sections is 0!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testGetSpendingSections() {
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionGetRs = settingsController.getSpendingSections();

        assertEquals(sectionGetRs.getBody().getServerStatus(), Status.SUCCESS, sectionGetRs.getBody().getMessage());
        assertTrue(sectionGetRs.getBody().getPayload().size() > 0, "Size of returned sections is 0!");
    }

    @Test(groups = "incorrectContainers")
    public void testSaveSettings_Empty_Settings() {
        ResponseEntity<MoneyCalcRs<Settings>> settingsSaveRs = settingsController.saveSettings(new SettingsUpdateContainer(null));
        assertEquals(settingsSaveRs.getBody().getServerStatus(), Status.ERROR, settingsSaveRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testSaveSettings_Settings_EmptyAll() {
        ResponseEntity<MoneyCalcRs<Settings>> settingsSaveRs = settingsController.saveSettings(new SettingsUpdateContainer(Settings.builder().build()));
        assertEquals(settingsSaveRs.getBody().getServerStatus(), Status.ERROR, settingsSaveRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testAddSpendingSection_Empty_SpendingSection() {
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionAddRs = settingsController.addSpendingSection(new SpendingSectionAddContainer(null));
        assertEquals(sectionAddRs.getBody().getServerStatus(), Status.ERROR, sectionAddRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testAddSpendingSection_SpendingSection_Empty_All() {
        SpendingSection spendingSection = SpendingSection.builder().build();
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionAddRs = settingsController.addSpendingSection(new SpendingSectionAddContainer(spendingSection));

        assertEquals(sectionAddRs.getBody().getServerStatus(), Status.ERROR, sectionAddRs.getBody().getMessage());
    }


    @Test(groups = "incorrectContainers")
    public void testUpdateSpendingSection_byId_Empty_idOrName() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer(null, generateSpendingSection(5000, 1), SpendingSectionSearchType.BY_ID);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getBody().getServerStatus(), Status.ERROR, sectionUpdateRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testUpdateSpendingSection_SpendingSection_emptyAll() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer("1", SpendingSection.builder().build(), SpendingSectionSearchType.BY_ID);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getBody().getServerStatus(), Status.ERROR, sectionUpdateRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testUpdateSpendingSection_byId_Empty_SpendingSection() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer("1", null, SpendingSectionSearchType.BY_ID);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getBody().getServerStatus(), Status.ERROR, sectionUpdateRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testUpdateSpendingSection_Empty_SearchType() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer("Name", generateSpendingSection(5000, 1), null);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getBody().getServerStatus(), Status.ERROR, sectionUpdateRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testDeleteSpendingSection_byId_Empty_idOrName() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer(null, SpendingSectionSearchType.BY_ID);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionDeleteRs = settingsController.deleteSpendingSection(deleteContainer);

        assertEquals(sectionDeleteRs.getBody().getServerStatus(), Status.ERROR, sectionDeleteRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testDeleteSpendingSection_byName_Empty_idOrName() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer(null, SpendingSectionSearchType.BY_NAME);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionDeleteRs = settingsController.deleteSpendingSection(deleteContainer);

        assertEquals(sectionDeleteRs.getBody().getServerStatus(), Status.ERROR, sectionDeleteRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testDeleteSpendingSection_Empty_searchType() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer("1", null);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionDeleteRs = settingsController.deleteSpendingSection(deleteContainer);

        assertEquals(sectionDeleteRs.getBody().getServerStatus(), Status.ERROR, sectionDeleteRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testDeleteSpendingSection_Empty_all() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer(null, null);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionDeleteRs = settingsController.deleteSpendingSection(deleteContainer);

        assertEquals(sectionDeleteRs.getBody().getServerStatus(), Status.ERROR, sectionDeleteRs.getBody().getMessage());
    }
}