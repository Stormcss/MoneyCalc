package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.strcss.projects.moneycalcserver.controllers.api.MoneyCalcClient;
import ru.strcss.projects.moneycalcserver.controllers.utils.Generator;
import ru.strcss.projects.moneycalcserver.enitities.dto.*;

import java.io.IOException;

import static org.testng.Assert.*;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.UUID;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.personGenerator;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

@Slf4j
public class SettingsControllerTest {
    private MoneyCalcClient service;

    //    private String login = Generator.UUID();
//    private Settings settings = Generator.generateSettings(login);
//
    @BeforeClass
    public void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MoneyCalcClient.class);
    }

    @Test
    public void saveSettingsIncorrectLogin() throws IOException {
//        Person person = personGenerator();
//
//        Response<AjaxRs<Person>> registerPersonResponse = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
//        assertEquals(registerPersonResponse.body().getStatus(), Status.SUCCESS, registerPersonResponse.body().getMessage());
//
        Settings settingsIncorrect = Generator.generateSettings(UUID());
        settingsIncorrect.set_id("");

        Response<AjaxRs<Settings>> response = sendRequest(service.saveSettings(settingsIncorrect));

        assertEquals(response.body().getStatus(), Status.ERROR, "Incorrect Settings are saved!");
    }

    @Test
    public void getSettings() throws IOException {
        Person person = personGenerator();

        //Registering Person
        Response<AjaxRs<Person>> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
        assertEquals(responseCreatePerson.body().getStatus(), Status.SUCCESS, responseCreatePerson.body().getMessage());

        //Saving Settings
        Response<AjaxRs<Settings>> responseSaveSettings = sendRequest(service.saveSettings(person.getSettings()));
        assertEquals(responseSaveSettings.body().getStatus(), Status.SUCCESS, responseSaveSettings.body().getMessage());

        //Getting Settings
        Response<AjaxRs<Settings>> responseGetSettings = sendRequest(service.getSettings(person.getID()));

        log.debug("Settings: {}", responseGetSettings.body().getPayload());

        assertEquals(responseGetSettings.body().getStatus(), Status.SUCCESS, responseGetSettings.body().getMessage());
        assertEquals(responseGetSettings.body().getPayload().get_id(), person.getID(), "returned Settings object has wrong login!");
    }

    @Test
    public void saveSettingsUpdate() throws IOException {

        String login = UUID();
        Person person = personGenerator(login);
        Settings newSettings = Generator.generateSettings(login);

        //Registering Person
        Response<AjaxRs<Person>> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
        assertEquals(responseCreatePerson.body().getStatus(), Status.SUCCESS, responseCreatePerson.body().getMessage());

        //Updating default Settings
        Response<AjaxRs<Settings>> responseSaveSettings = sendRequest(service.saveSettings(person.getSettings()));
        assertEquals(responseSaveSettings.body().getStatus(), Status.SUCCESS, responseSaveSettings.body().getMessage());
        assertNotNull(responseSaveSettings.body().getPayload(), "Payload is null!");
        assertNotNull(responseSaveSettings.body().getPayload().getSections(), "Settings object is empty!");

        //Updating Settings once again
        Response<AjaxRs<Settings>> responseUpdate = sendRequest(service.saveSettings(newSettings));
        assertEquals(responseUpdate.body().getStatus(), Status.SUCCESS, responseUpdate.body().getMessage());
        assertNotNull(responseUpdate.body().getPayload(), "Payload is null!");
        assertNotNull(responseUpdate.body().getPayload().getSections(), "Settings object is empty!");
        assertEquals(responseUpdate.body().getPayload().get_id(), login, "returned Settings object has wrong login!");
        assertNotEquals(responseUpdate.body().getPayload().getSections().get(0).getName(), responseSaveSettings.body().getPayload().getSections().get(0).getName(), "Settings were not updated!");

        //Requesting updated settings
        Response<AjaxRs<Settings>> responseGetUpdated = sendRequest(service.getSettings(login));

        assertEquals(responseGetUpdated.body().getStatus(), Status.SUCCESS, responseGetUpdated.body().getMessage());
        assertNotNull(responseGetUpdated.body().getPayload(), "Payload is null!");
        assertNotNull(responseGetUpdated.body().getPayload().getSections(), "Settings object is empty!");
        assertEquals(responseGetUpdated.body().getPayload().getSections().get(0).getName(), newSettings.getSections().get(0).getName(), "Settings were not updated!");

        log.debug("Settings before update: {}", person.getSettings());
        log.debug("Settings after update: {}", responseGetUpdated.body().getPayload());
    }

}