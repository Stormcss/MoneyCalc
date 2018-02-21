package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalcserver.controllers.utils.Generator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.UUID;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.generateCredentials;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

@Slf4j
public class IdentificationsControllerTest  extends AbstractControllerTest {

    @Test
    public void saveIdentifications() {
        String login = Generator.UUID();
        Credentials credentials = generateCredentials(login);

        //Registering Person
        AjaxRs<Person> responseCreatePerson = sendRequest(service.registerPerson(credentials)).body();
        assertEquals(responseCreatePerson.getStatus(), Status.SUCCESS, responseCreatePerson.getMessage());

        //Updating default Identifications
        AjaxRs<Identifications> responseSaveIdentifications = sendRequest(service.saveIdentifications(credentials.getIdentifications())).body();
        assertEquals(responseSaveIdentifications.getStatus(), Status.SUCCESS, responseSaveIdentifications.getMessage());
        assertNotNull(responseSaveIdentifications.getPayload(), "Payload is null!");
        assertNotNull(responseSaveIdentifications.getPayload().getName(), "Identifications object is empty!");

        //Requesting updated Identifications
        AjaxRs<Identifications> responseGetUpdated = sendRequest(service.getIdentifications(login)).body();
        assertEquals(responseGetUpdated.getStatus(), Status.SUCCESS, responseGetUpdated.getMessage());
        assertEquals(responseGetUpdated.getPayload().getLogin(), login, "returned Identifications object has wrong login!");
        assertEquals(responseGetUpdated.getPayload().getName(), credentials.getIdentifications().getName(), "returned Identifications object has wrong name!");
    }

    @Test
    public void saveIdentificationsIncorrectLogin() {
        Identifications identificationsIncorrect = Generator.generateIdentifications(UUID());

        identificationsIncorrect.setLogin("");
        AjaxRs<Identifications> response = sendRequest(service.saveIdentifications(identificationsIncorrect)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Identifications object with incorrect Login is saved!");
    }

    @Test
    public void saveIdentificationsIncorrectName() {
        Identifications identificationsIncorrect = Generator.generateIdentifications(UUID());

        identificationsIncorrect.setName("");
        AjaxRs<Identifications> response = sendRequest(service.saveIdentifications(identificationsIncorrect)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Identifications object with incorrect Name is saved!");
    }

    @Test
    public void getIdentifications() {

        String login = Generator.UUID();
        Credentials credentials = generateCredentials(login);

        //Registering Person
        AjaxRs<Person> responseCreatePerson = sendRequest(service.registerPerson(credentials)).body();
        assertEquals(responseCreatePerson.getStatus(), Status.SUCCESS, responseCreatePerson.getMessage());

        AjaxRs<Identifications> response = sendRequest(service.getIdentifications(login)).body();

        assertEquals(response.getStatus(), Status.SUCCESS, response.getMessage());
        assertEquals(response.getPayload().getLogin(), login, "returned Identifications object has wrong login!");
        assertEquals(response.getPayload().getName(), credentials.getIdentifications().getName(), "returned Identifications object has wrong name!");
    }

}