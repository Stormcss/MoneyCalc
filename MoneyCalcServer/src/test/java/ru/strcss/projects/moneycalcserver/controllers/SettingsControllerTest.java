package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalcserver.controllers.utils.Generator;

import static org.testng.Assert.*;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.UUID;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.generateCredentials;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.savePersonGetLogin;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

@Slf4j
public class SettingsControllerTest extends AbstractControllerTest {

    @Test
    public void saveSettingsIncorrectLogin(){
        String login = savePersonGetLogin(service);

        Settings settingsIncorrect = Generator.generateSettings(login, 2);
        settingsIncorrect.setLogin("");

        AjaxRs<Settings> response = sendRequest(service.saveSettings(settingsIncorrect)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Incorrect Settings are saved!");
    }

    @Test
    public void saveSettingsDuplicateSections(){
        String login = savePersonGetLogin(service);

        Settings incorrectSettings = Generator.generateSettings(login, 5);

        incorrectSettings.getSections().get(0).setId(incorrectSettings.getSections().get(1).getId());

        AjaxRs<Settings> response = sendRequest(service.saveSettings(incorrectSettings)).body();

        assertEquals(response.getStatus(), Status.ERROR, response.getMessage());
    }

    @Test
    public void getSettings(){
//        String login = "2649AA313EF74CBABEC0B0E0AEF3E6A7";
        String login = savePersonGetLogin(service);

        //Getting Settings
        AjaxRs<Settings> responseGetSettings = sendRequest(service.getSettings(login)).body();

        log.debug("Settings: {}", responseGetSettings.getPayload());

        assertEquals(responseGetSettings.getStatus(), Status.SUCCESS, responseGetSettings.getMessage());
        assertEquals(responseGetSettings.getPayload().getLogin(), login, "returned Settings object has wrong login!");
        assertTrue(responseGetSettings.getPayload().getSections().stream().allMatch(section -> section.getId() != null), "Some IDs in Spending Sections are null!");
    }

    @Test
    public void saveSettingsUpdate(){

        String login = UUID();
        Settings newSettings = Generator.generateSettings(login,2);
        Credentials credentials = generateCredentials(login);


        //Registering Person
        AjaxRs<Person> responseCreatePerson = sendRequest(service.registerPerson(credentials)).body();
        assertEquals(responseCreatePerson.getStatus(), Status.SUCCESS, responseCreatePerson.getMessage());

        //Requesting settings
        AjaxRs<Settings> responseGetSettings = sendRequest(service.getSettings(login)).body();
        assertEquals(responseGetSettings.getStatus(), Status.SUCCESS, responseGetSettings.getMessage());


        //Updating Settings again
        AjaxRs<Settings> responseUpdate = sendRequest(service.saveSettings(newSettings)).body();
        assertEquals(responseUpdate.getStatus(), Status.SUCCESS, responseUpdate.getMessage());
        assertNotNull(responseUpdate.getPayload(), "Payload is null!");
        assertNotNull(responseUpdate.getPayload().getSections(), "Settings object is empty!");
        assertEquals(responseUpdate.getPayload().getLogin(), login, "returned Settings object has wrong login!");
        assertNotEquals(responseUpdate.getPayload().getSections().get(0).getName(), responseGetSettings.getPayload().getSections().get(0).getName(), "Settings were not updated!");


        //Checking that Person is ok after updating Settings
        AjaxRs<Identifications> responseIdentifications = sendRequest(service.getIdentifications(login)).body();
        assertNotNull(responseIdentifications.getPayload(), "Identifications object was overwritten!");

        //Requesting updated settings
        AjaxRs<Settings> responseGetUpdated = sendRequest(service.getSettings(login)).body();

        assertEquals(responseGetUpdated.getStatus(), Status.SUCCESS, responseGetUpdated.getMessage());
        assertNotNull(responseGetUpdated.getPayload(), "Payload is null!");
        assertNotNull(responseGetUpdated.getPayload().getSections(), "Settings object is empty!");
        assertEquals(responseGetUpdated.getPayload().getSections().get(0).getName(), newSettings.getSections().get(0).getName(), "Settings were not updated!");

        log.debug("Settings before update: {}", responseGetSettings.getPayload());
        log.debug("Settings after update: {}", responseGetUpdated.getPayload());
    }

}