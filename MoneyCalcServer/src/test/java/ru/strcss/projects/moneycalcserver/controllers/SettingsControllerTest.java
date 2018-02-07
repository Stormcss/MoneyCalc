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
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.personGenerator;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.savePersonGetLogin;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

@Slf4j
public class SettingsControllerTest extends AbstractControllerTest {

    @Test
    public void saveSettingsIncorrectLogin(){
        String login = savePersonGetLogin(service);

        Settings settingsIncorrect = Generator.generateSettings(login);
        settingsIncorrect.set_id("");

        AjaxRs<Settings> response = sendRequest(service.saveSettings(settingsIncorrect)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Incorrect Settings are saved!");
    }

    @Test
    public void getSettings(){
        String login = savePersonGetLogin(service);

        //Saving Settings
//        Response<AjaxRs<Settings>> responseSaveSettings = sendRequest(service.saveSettings(person.getSettings()));
//        assertEquals(responseSaveSettings.getStatus(), Status.SUCCESS, responseSaveSettings.getMessage());

        //Getting Settings
        AjaxRs<Settings> responseGetSettings = sendRequest(service.getSettings(login)).body();

        log.debug("Settings: {}", responseGetSettings.getPayload());

        assertEquals(responseGetSettings.getStatus(), Status.SUCCESS, responseGetSettings.getMessage());
        assertEquals(responseGetSettings.getPayload().get_id(), login, "returned Settings object has wrong login!");
    }

    @Test
    public void saveSettingsUpdate(){

        String login = UUID();
        Person person = personGenerator(login);
        Settings newSettings = Generator.generateSettings(login);

        //Registering Person
        AjaxRs<Person> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications()))).body();
        assertEquals(responseCreatePerson.getStatus(), Status.SUCCESS, responseCreatePerson.getMessage());

        //Updating default Settings
        AjaxRs<Settings> responseSaveSettings = sendRequest(service.saveSettings(person.getSettings())).body();
        assertEquals(responseSaveSettings.getStatus(), Status.SUCCESS, responseSaveSettings.getMessage());
        assertNotNull(responseSaveSettings.getPayload(), "Payload is null!");
        assertNotNull(responseSaveSettings.getPayload().getSections(), "Settings object is empty!");

        //Requesting settings
        AjaxRs<Settings> responseGetSettings = sendRequest(service.getSettings(login)).body();
        assertEquals(responseGetSettings.getStatus(), Status.SUCCESS, responseGetSettings.getMessage());


        //Updating Settings again
        AjaxRs<Settings> responseUpdate = sendRequest(service.saveSettings(newSettings)).body();
        assertEquals(responseUpdate.getStatus(), Status.SUCCESS, responseUpdate.getMessage());
        assertNotNull(responseUpdate.getPayload(), "Payload is null!");
        assertNotNull(responseUpdate.getPayload().getSections(), "Settings object is empty!");
        assertEquals(responseUpdate.getPayload().get_id(), login, "returned Settings object has wrong login!");
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

        log.debug("Settings before update: {}", person.getSettings());
        log.debug("Settings after update: {}", responseGetUpdated.getPayload());
    }

}