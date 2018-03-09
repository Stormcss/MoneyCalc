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
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SettingsUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.SettingsDBConnection;
import ru.strcss.projects.moneycalc.moneycalcserver.mongo.PersonRepository;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.testutils.Generator.*;

public class SettingsControllerTest {

    private SettingsDBConnection settingsDBConnection = mock(SettingsDBConnection.class);
    ;
    private PersonRepository repository = mock(PersonRepository.class);

    @BeforeClass
    public void setUp() {
        User user = new User("login", "password", Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @BeforeGroups(groups = "SuccessGetAndSave")
    public void prepare_successGetAndSaveSettings() {
        when(settingsDBConnection.updateSettings(any(Settings.class))).thenReturn(new WriteResult(1, true, new Object()));
        when(settingsDBConnection.getSettings(anyString())).thenReturn(generateSettings(UUID(), true));

        when(settingsDBConnection.addSpendingSection(anyString(), any(SpendingSectionAddContainer.class))).thenReturn(new WriteResult(1, false, null));
        when(settingsDBConnection.isSpendingSectionNameNew(anyString(), anyString())).thenReturn(true);

        when(repository.existsByAccess_Login(anyString())).thenReturn(true);
    }

    @Test(groups = "SuccessGetAndSave")
    public void testSaveSettings() {
        SettingsController settingsController = new SettingsController(settingsDBConnection, repository);

        AjaxRs<Settings> settingsSaveRs = settingsController.saveSettings(new SettingsUpdateContainer(generateSettings(UUID(), false)));

        assertEquals(settingsSaveRs.getStatus(), Status.SUCCESS, "Settings were not saved!");
    }

    @Test(groups = "SuccessGetAndSave")
    public void testGetSettings() {
        SettingsController settingsController = new SettingsController(settingsDBConnection, repository);

        AjaxRs<Settings> settingsGetRs = settingsController.getSettings();

        assertEquals(settingsGetRs.getStatus(), Status.SUCCESS, "Settings were not returned!");
    }

    @Test(groups = "SuccessGetAndSave")
    public void testAddSpendingSection() {
        SettingsController settingsController = new SettingsController(settingsDBConnection, repository);

        AjaxRs<List<SpendingSection>> sectionAddRs = settingsController.addSpendingSection(new SpendingSectionAddContainer(generateSpendingSection()));

        assertEquals(sectionAddRs.getStatus(), Status.SUCCESS, "Settings were not returned!");
    }

    @Test
    public void testUpdateSpendingSection() {
    }

    @Test
    public void testDeleteSpendingSection() {
    }

    @Test(groups = "SuccessGetAndSave")
    public void testGetSpendingSections() {
    }
}