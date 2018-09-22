package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

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
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SettingsUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.PersonService;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.SettingsService;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.SpendingSectionService;
import ru.strcss.projects.moneycalc.moneycalcserver.dto.ResultContainer;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static ru.strcss.projects.moneycalc.testutils.Generator.*;

public class SettingsControllerTest {

    private SettingsService settingsService = mock(SettingsService.class);
    private PersonService personService = mock(PersonService.class);
    private SpendingSectionService sectionService = mock(SpendingSectionService.class);

    private SettingsController settingsController;

    private final String duplicatingSectionName = "duplicate";

    private List<SpendingSection> sectionList = generateSpendingSectionList(5, false);

    @BeforeClass
    public void setUp() {
        User user = new User("login", "password", Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(personService.getPersonIdByLogin(anyString())).thenReturn(1);
        when(personService.getSettingsIdByPersonId(anyInt())).thenReturn(1);
        sectionList.get(sectionList.size() - 1).setIsRemoved(true);
    }

    @BeforeGroups(groups = {"SettingsSuccessfulScenario", "SettingsIncorrectContainers"})
    public void prepare_successfulScenario_incorrectContainers() {
//        List<SpendingSection> sectionList = generateSpendingSectionList(5, true);
//        sectionList.get(sectionList.size() - 1).setIsAdded(false);

        when(settingsService.updateSettings(any(Settings.class))).thenReturn(generateSettings());
        when(settingsService.getSettingsById(anyInt())).thenReturn(generateSettings());

        when(sectionService.getSpendingSectionsByLogin(anyString(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(sectionList);
        when(sectionService.getSpendingSectionById(anyInt())).thenReturn(generateSpendingSection());
        when(sectionService.addSpendingSection(anyInt(), any(SpendingSection.class))).thenReturn(1);
        when(sectionService.updateSpendingSection(any(SpendingSection.class))).thenReturn(true);
        when(sectionService.deleteSpendingSection(anyString(), any())).thenReturn(new ResultContainer(true));

        when(sectionService.isSpendingSectionNameNew(anyInt(), anyString())).thenReturn(true);
        when(sectionService.isSpendingSectionIdExists(anyInt(), anyInt())).thenReturn(true);
        when(sectionService.getSpendingSectionsByPersonId(anyInt())).thenReturn(generateSpendingSectionList(3, true));

        settingsController = new SettingsController(settingsService, personService, sectionService);
    }

    @BeforeGroups(groups = "SettingsfailedScenario")
    public void prepare_failedScenario() {
        when(settingsService.getSettingsById(anyInt())).thenReturn(null);
        when(settingsService.updateSettings(any(Settings.class))).thenReturn(null);
        when(sectionService.getSpendingSectionsByLogin(anyString(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(null);
        when(sectionService.addSpendingSection(anyInt(), any(SpendingSection.class))).thenReturn(null);
        when(sectionService.updateSpendingSection(any(SpendingSection.class))).thenReturn(false);
        when(sectionService.deleteSpendingSection(anyString(), any())).thenReturn(new ResultContainer(false));
    }

    @BeforeGroups(groups = "SettingsDuplicatingSectionNames")
    public void prepare_duplicatingSectionNames() {
        List<SpendingSection> sectionList = generateSpendingSectionList(5, false);
        sectionList.get(1).setName(duplicatingSectionName);
        when(sectionService.getSpendingSectionsByLogin(anyString(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(sectionList);
        when(sectionService.getSpendingSectionsByLogin(anyString(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(sectionList);
    }

    @Test(groups = "SettingsSuccessfulScenario")
    public void testSaveSettings() {
        ResponseEntity<MoneyCalcRs<Settings>> settingsSaveRs = settingsController.updateSettings(
                new SettingsUpdateContainer(generateSettings()));
        assertEquals(settingsSaveRs.getBody().getServerStatus(), Status.SUCCESS, "Settings were not saved!");
    }

    @Test(groups = "SettingsSuccessfulScenario")
    public void testGetSettings() {
        ResponseEntity<MoneyCalcRs<Settings>> settingsGetRs = settingsController.getSettings();

        assertEquals(settingsGetRs.getBody().getServerStatus(), Status.SUCCESS, settingsGetRs.getBody().getMessage());
    }

    @Test(groups = "SettingsSuccessfulScenario")
    public void testAddSpendingSection() {
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionAddRs = settingsController.addSpendingSection(
                new SpendingSectionAddContainer(generateSpendingSection()));

        assertEquals(sectionAddRs.getBody().getServerStatus(), Status.SUCCESS, sectionAddRs.getBody().getMessage());
    }

    @Test(groups = "SettingsSuccessfulScenario")
    public void testAddSpendingSection_withIsRemovedTrue() {
        SpendingSection spendingSection = generateSpendingSection();
        spendingSection.setIsRemoved(true);

        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionAddRs = settingsController.addSpendingSection(
                new SpendingSectionAddContainer(spendingSection));

        assertEquals(sectionAddRs.getBody().getServerStatus(), Status.ERROR, sectionAddRs.getBody().getMessage());
    }

    @Test(groups = "SettingsSuccessfulScenario")
    public void testUpdateSpendingSection_ExistingId() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer(1, generateSpendingSection(5000, 1));
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getBody().getServerStatus(), Status.SUCCESS, sectionUpdateRs.getBody().getMessage());
    }

    @Test(groups = "SettingsSuccessfulScenario")
    public void testUpdateSpendingSection_withIsRemovedTrue() {
        SpendingSection spendingSection = generateSpendingSection(5000, 1);
        spendingSection.setIsRemoved(true);
        SpendingSectionUpdateContainer nameUpdateContainer = new SpendingSectionUpdateContainer(1, spendingSection);

        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getBody().getServerStatus(), Status.SUCCESS, sectionUpdateRs.getBody().getMessage());
    }

    @Test(groups = "SettingsSuccessfulScenario")
    public void testDeleteSpendingSection_byId() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer(1);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionDeleteRs = settingsController.deleteSpendingSection(deleteContainer);

        assertEquals(sectionDeleteRs.getBody().getServerStatus(), Status.SUCCESS, sectionDeleteRs.getBody().getMessage());
        assertTrue(sectionDeleteRs.getBody().getPayload().size() > 0, "Size of returned sections is 0!");
    }

    @Test(groups = "SettingsIncorrectContainers")
    public void testSaveSettings_Empty_Settings() {
        ResponseEntity<MoneyCalcRs<Settings>> settingsSaveRs = settingsController.updateSettings(new SettingsUpdateContainer(null));
        assertEquals(settingsSaveRs.getBody().getServerStatus(), Status.ERROR, settingsSaveRs.getBody().getMessage());
    }

    @Test(groups = "SettingsIncorrectContainers")
    public void testSaveSettings_Settings_EmptyAll() {
        ResponseEntity<MoneyCalcRs<Settings>> settingsSaveRs = settingsController.updateSettings(new SettingsUpdateContainer(Settings.builder().build()));
        assertEquals(settingsSaveRs.getBody().getServerStatus(), Status.ERROR, settingsSaveRs.getBody().getMessage());
    }

    @Test(groups = "SettingsIncorrectContainers")
    public void testAddSpendingSection_Empty_SpendingSection() {
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionAddRs = settingsController.addSpendingSection(new SpendingSectionAddContainer(null));
        assertEquals(sectionAddRs.getBody().getServerStatus(), Status.ERROR, sectionAddRs.getBody().getMessage());
    }

    @Test(groups = "SettingsIncorrectContainers")
    public void testAddSpendingSection_SpendingSection_Empty_All() {
        SpendingSection spendingSection = SpendingSection.builder().build();
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionAddRs = settingsController.addSpendingSection(new SpendingSectionAddContainer(spendingSection));

        assertEquals(sectionAddRs.getBody().getServerStatus(), Status.ERROR, sectionAddRs.getBody().getMessage());
    }


    @Test(groups = "SettingsIncorrectContainers")
    public void testUpdateSpendingSection_Empty_idOrName() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer(null, generateSpendingSection(5000, 1));
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getBody().getServerStatus(), Status.ERROR, sectionUpdateRs.getBody().getMessage());
    }

    @Test(groups = "SettingsIncorrectContainers")
    public void testUpdateSpendingSection_SpendingSection_emptyAll() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer(1, SpendingSection.builder().build());
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getBody().getServerStatus(), Status.ERROR, sectionUpdateRs.getBody().getMessage());
    }

    @Test(groups = "SettingsIncorrectContainers")
    public void testUpdateSpendingSection_empty_spendingSection() {
        SpendingSectionUpdateContainer nameUpdateContainer = new SpendingSectionUpdateContainer(1, null);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getBody().getServerStatus(), Status.ERROR, sectionUpdateRs.getBody().getMessage());
    }

    @Test(groups = "SettingsIncorrectContainers")
    public void testDeleteSpendingSection_idEmpty() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer(null);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionDeleteRs = settingsController.deleteSpendingSection(deleteContainer);

        assertEquals(sectionDeleteRs.getBody().getServerStatus(), Status.ERROR, sectionDeleteRs.getBody().getMessage());
    }

    @Test(groups = "SettingsfailedScenario", dependsOnGroups = {"SettingsSuccessfulScenario", "SettingsIncorrectContainers"})
    public void testGetSettings_failedScenario() {
        ResponseEntity<MoneyCalcRs<Settings>> settingsGetRs = settingsController.getSettings();

        assertEquals(settingsGetRs.getBody().getServerStatus(), Status.ERROR, "Response is not failed!");
    }

    @Test(groups = "SettingsfailedScenario", dependsOnGroups = {"SettingsSuccessfulScenario", "SettingsIncorrectContainers"})
    public void testSaveSettings_failedScenario() {
        ResponseEntity<MoneyCalcRs<Settings>> settingsSaveRs = settingsController.updateSettings(
                new SettingsUpdateContainer(generateSettings()));

        assertEquals(settingsSaveRs.getBody().getServerStatus(), Status.ERROR, "Response is not failed!");
    }


    @Test(groups = "SettingsfailedScenario", dependsOnGroups = {"SettingsSuccessfulScenario", "SettingsIncorrectContainers"})
    public void testAddSpendingSection_failedScenario() {
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionAddRs = settingsController.addSpendingSection(
                new SpendingSectionAddContainer(generateSpendingSection()));

        assertEquals(sectionAddRs.getBody().getServerStatus(), Status.ERROR, sectionAddRs.getBody().getMessage());
    }

    @Test(groups = "SettingsfailedScenario", dependsOnGroups = {"SettingsSuccessfulScenario", "SettingsIncorrectContainers"})
    public void testAddSpendingSection_existingSpendingSectionName() {
        when(sectionService.isSpendingSectionNameNew(anyInt(), anyString())).thenReturn(false);

        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionAddRs = settingsController.addSpendingSection(
                new SpendingSectionAddContainer(generateSpendingSection()));

        assertEquals(sectionAddRs.getBody().getServerStatus(), Status.ERROR, sectionAddRs.getBody().getMessage());
        when(sectionService.isSpendingSectionNameNew(anyInt(), anyString())).thenReturn(true);
    }

    @Test(groups = "SettingsfailedScenario", dependsOnGroups = {"SettingsSuccessfulScenario", "SettingsIncorrectContainers"})
    public void testUpdateSpendingSection_failedScenario() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer(0, generateSpendingSection(duplicatingSectionName));
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getBody().getServerStatus(), Status.ERROR, sectionUpdateRs.getBody().getMessage());
    }

    @Test(groups = "SettingsfailedScenario", dependsOnGroups = {"SettingsSuccessfulScenario", "SettingsIncorrectContainers"})
    public void testDeleteSpendingSection_failedScenario() {
        SpendingSectionDeleteContainer deleteContainer = new SpendingSectionDeleteContainer(1);
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionDeleteRs = settingsController.deleteSpendingSection(deleteContainer);

        assertEquals(sectionDeleteRs.getBody().getServerStatus(), Status.ERROR, sectionDeleteRs.getBody().getMessage());
    }


    @Test(groups = "SettingsDuplicatingSectionNames", dependsOnGroups = {"SettingsSuccessfulScenario", "SettingsIncorrectContainers", "SettingsfailedScenario"})
    public void testUpdateSpendingSection_updatingToExistingSpendingSectionName() {
        SpendingSectionUpdateContainer nameUpdateContainer =
                new SpendingSectionUpdateContainer(0, generateSpendingSection(duplicatingSectionName));
        ResponseEntity<MoneyCalcRs<List<SpendingSection>>> sectionUpdateRs = settingsController.updateSpendingSection(nameUpdateContainer);

        assertEquals(sectionUpdateRs.getBody().getServerStatus(), Status.ERROR, sectionUpdateRs.getBody().getMessage());
    }

//    /**
//     * Asserting that incoming list has no removed sections.
//     */
//    private void assertNoRemovedSections(List<SpendingSection> spendingSections) {
//        assertTrue(spendingSections.stream().noneMatch(SpendingSection::getIsRemoved), "Some removed sections are returned!");
//    }


}