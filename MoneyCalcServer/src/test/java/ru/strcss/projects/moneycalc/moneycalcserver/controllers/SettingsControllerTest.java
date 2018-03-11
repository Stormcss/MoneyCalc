package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import com.mongodb.WriteResult;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
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
        AjaxRs<Settings> settingsSaveRs = settingsController.saveSettings(new SettingsUpdateContainer(generateSettings(UUID(), false)));
        assertEquals(settingsSaveRs.getStatus(), Status.SUCCESS, "Settings were not saved!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testGetSettings() {
        AjaxRs<Settings> settingsGetRs = settingsController.getSettings();

        assertEquals(settingsGetRs.getStatus(), Status.SUCCESS, settingsGetRs.getMessage());
    }

    @Test(groups = "SuccessfulScenario")
    public void testAddSpendingSection() {
        AjaxRs<List<SpendingSection>> sectionAddRs = settingsController.addSpendingSection(new SpendingSectionAddContainer(generateSpendingSection()));

        assertEquals(sectionAddRs.getStatus(), Status.SUCCESS, sectionAddRs.getMessage());
    }

    @Test(groups = "SuccessfulScenario")
    public void testUpdateSpendingSection_byName_ExistingName() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer("Name", generateSpendingSection("Name"), SpendingSectionSearchType.BY_NAME);
        AjaxRs<List<SpendingSection>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getStatus(), Status.SUCCESS, sectionUpdateRs.getMessage());
    }

    @Test(groups = "SuccessfulScenario")
    public void testUpdateSpendingSection_byName_NewName() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer("Name", generateSpendingSection("newName"), SpendingSectionSearchType.BY_NAME);
        AjaxRs<List<SpendingSection>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getStatus(), Status.SUCCESS, sectionUpdateRs.getMessage());
    }

    @Test(groups = "SuccessfulScenario")
    public void testUpdateSpendingSection_byId_ExistingId() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer("1", generateSpendingSection(5000, 1), SpendingSectionSearchType.BY_ID);
        AjaxRs<List<SpendingSection>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getStatus(), Status.SUCCESS, sectionUpdateRs.getMessage());
    }

    @Test(groups = "SuccessfulScenario")
    public void testDeleteSpendingSection_byId() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer("1", SpendingSectionSearchType.BY_ID);
        AjaxRs<List<SpendingSection>> sectionDeleteRs = settingsController.deleteSpendingSection(deleteContainer);

        assertEquals(sectionDeleteRs.getStatus(), Status.SUCCESS, sectionDeleteRs.getMessage());
        assertTrue(sectionDeleteRs.getPayload().size() > 0, "Size of returned sections is 0!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testDeleteSpendingSection_byName() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer("name", SpendingSectionSearchType.BY_NAME);
        AjaxRs<List<SpendingSection>> sectionDeleteRs = settingsController.deleteSpendingSection(deleteContainer);

        assertEquals(sectionDeleteRs.getStatus(), Status.SUCCESS, sectionDeleteRs.getMessage());
        assertTrue(sectionDeleteRs.getPayload().size() > 0, "Size of returned sections is 0!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testGetSpendingSections() {
        AjaxRs<List<SpendingSection>> sectionGetRs = settingsController.getSpendingSections();

        assertEquals(sectionGetRs.getStatus(), Status.SUCCESS, sectionGetRs.getMessage());
        assertTrue(sectionGetRs.getPayload().size() > 0, "Size of returned sections is 0!");
    }

    @Test(groups = "incorrectContainers")
    public void testSaveSettings_Empty_Settings() {
        AjaxRs<Settings> settingsSaveRs = settingsController.saveSettings(new SettingsUpdateContainer(null));
        assertEquals(settingsSaveRs.getStatus(), Status.ERROR, settingsSaveRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testSaveSettings_Settings_EmptyAll() {
        AjaxRs<Settings> settingsSaveRs = settingsController.saveSettings(new SettingsUpdateContainer(Settings.builder().build()));
        assertEquals(settingsSaveRs.getStatus(), Status.ERROR, settingsSaveRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testAddSpendingSection_Empty_SpendingSection() {
        AjaxRs<List<SpendingSection>> sectionAddRs = settingsController.addSpendingSection(new SpendingSectionAddContainer(null));
        assertEquals(sectionAddRs.getStatus(), Status.ERROR, sectionAddRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testAddSpendingSection_SpendingSection_Empty_All() {
        SpendingSection spendingSection = SpendingSection.builder().build();
        AjaxRs<List<SpendingSection>> sectionAddRs = settingsController.addSpendingSection(new SpendingSectionAddContainer(spendingSection));

        assertEquals(sectionAddRs.getStatus(), Status.ERROR, sectionAddRs.getMessage());
    }


    @Test(groups = "incorrectContainers")
    public void testUpdateSpendingSection_byId_Empty_idOrName() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer(null, generateSpendingSection(5000, 1), SpendingSectionSearchType.BY_ID);
        AjaxRs<List<SpendingSection>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getStatus(), Status.ERROR, sectionUpdateRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testUpdateSpendingSection_SpendingSection_emptyAll() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer("1", SpendingSection.builder().build(), SpendingSectionSearchType.BY_ID);
        AjaxRs<List<SpendingSection>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getStatus(), Status.ERROR, sectionUpdateRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testUpdateSpendingSection_byId_Empty_SpendingSection() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer("1", null, SpendingSectionSearchType.BY_ID);
        AjaxRs<List<SpendingSection>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getStatus(), Status.ERROR, sectionUpdateRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testUpdateSpendingSection_Empty_SearchType() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer("Name", generateSpendingSection(5000, 1), null);
        AjaxRs<List<SpendingSection>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getStatus(), Status.ERROR, sectionUpdateRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testDeleteSpendingSection_byId_Empty_idOrName() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer(null, SpendingSectionSearchType.BY_ID);
        AjaxRs<List<SpendingSection>> sectionDeleteRs = settingsController.deleteSpendingSection(deleteContainer);

        assertEquals(sectionDeleteRs.getStatus(), Status.ERROR, sectionDeleteRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testDeleteSpendingSection_byName_Empty_idOrName() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer(null, SpendingSectionSearchType.BY_NAME);
        AjaxRs<List<SpendingSection>> sectionDeleteRs = settingsController.deleteSpendingSection(deleteContainer);

        assertEquals(sectionDeleteRs.getStatus(), Status.ERROR, sectionDeleteRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testDeleteSpendingSection_Empty_searchType() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer("1", null);
        AjaxRs<List<SpendingSection>> sectionDeleteRs = settingsController.deleteSpendingSection(deleteContainer);

        assertEquals(sectionDeleteRs.getStatus(), Status.ERROR, sectionDeleteRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testDeleteSpendingSection_Empty_all() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer(null, null);
        AjaxRs<List<SpendingSection>> sectionDeleteRs = settingsController.deleteSpendingSection(deleteContainer);

        assertEquals(sectionDeleteRs.getStatus(), Status.ERROR, sectionDeleteRs.getMessage());
    }
}