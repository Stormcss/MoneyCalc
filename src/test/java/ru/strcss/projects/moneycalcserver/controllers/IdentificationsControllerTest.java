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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.UUID;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.personGenerator;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

@Slf4j
public class IdentificationsControllerTest {
    private MoneyCalcClient service;

//    private String login = Generator.UUID();
//    private Identifications identifications = Generator.generateIdentifications(login);
//     newIdentifications used to update existing identifications
//    private Identifications newIdentifications = Generator.generateIdentifications(login);

    @BeforeClass
    public void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MoneyCalcClient.class);
    }

    @Test
    public void saveIdentifications() throws IOException {
        String login = Generator.UUID();
        Person person = personGenerator(login);

        //Registering Person
        Response<AjaxRs<Person>> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
        assertEquals(responseCreatePerson.body().getStatus(), Status.SUCCESS, responseCreatePerson.body().getMessage());

        //Updating default Identifications
        Response<AjaxRs<Identifications>> responseSaveIdentifications = sendRequest(service.saveIdentifications(person.getIdentifications()));
        assertEquals(responseSaveIdentifications.body().getStatus(), Status.SUCCESS, responseSaveIdentifications.body().getMessage());
        assertNotNull(responseSaveIdentifications.body().getPayload(), "Payload is null!");
        assertNotNull(responseSaveIdentifications.body().getPayload().getName(), "Identifications object is empty!");

        //Requesting updated Identifications
        Response<AjaxRs<Identifications>> responseGetUpdated = sendRequest(service.getIdentifications(login));
        assertEquals(responseGetUpdated.body().getStatus(), Status.SUCCESS, responseGetUpdated.body().getMessage());
        assertEquals(responseGetUpdated.body().getPayload().get_id(), login, "returned Identifications object has wrong login!");
        assertEquals(responseGetUpdated.body().getPayload().getName(), person.getIdentifications().getName(), "returned Identifications object has wrong name!");
    }

    @Test
    public void saveIdentificationsIncorrectLogin() throws IOException {
        Identifications identificationsIncorrect = Generator.generateIdentifications(UUID());

        identificationsIncorrect.set_id("");
        Response<AjaxRs<Identifications>> response = sendRequest(service.saveIdentifications(identificationsIncorrect));

        assertEquals(response.body().getStatus(), Status.ERROR, "Identifications object with incorrect Login is saved!");
    }

    @Test
    public void saveIdentificationsIncorrectName() throws IOException {
        Identifications identificationsIncorrect = Generator.generateIdentifications(UUID());

        identificationsIncorrect.setName("");
        Response<AjaxRs<Identifications>> response = sendRequest(service.saveIdentifications(identificationsIncorrect));

        assertEquals(response.body().getStatus(), Status.ERROR, "Identifications object with incorrect Name is saved!");
    }

    @Test
    public void getIdentifications() throws IOException {

        String login = Generator.UUID();
        Person person = personGenerator(login);

        //Registering Person
        Response<AjaxRs<Person>> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
        assertEquals(responseCreatePerson.body().getStatus(), Status.SUCCESS, responseCreatePerson.body().getMessage());

        Response<AjaxRs<Identifications>> response = sendRequest(service.getIdentifications(login));

        assertEquals(response.body().getStatus(), Status.SUCCESS, response.body().getMessage());
        assertEquals(response.body().getPayload().get_id(), login, "returned Identifications object has wrong login!");
        assertEquals(response.body().getPayload().getName(), responseCreatePerson.body().getPayload().getIdentifications().getName(), "returned Identifications object has wrong name!");
    }

}