package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.LoginGetContainer;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.integration.utils.Generator;
import ru.strcss.projects.moneycalc.integration.utils.Utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Slf4j
public class IdentificationsControllerIT extends AbstractControllerIT {

    @Test
    public void saveIdentifications() {
        String login = Generator.UUID();
        Credentials credentials = Generator.generateCredentials(login);

        //Registering Person
        AjaxRs<Person> responseCreatePerson = Utils.sendRequest(service.registerPerson(credentials), Status.SUCCESS).body();
//        assertEquals(responseCreatePerson.getStatus(), Status.SUCCESS, responseCreatePerson.getMessage());

        //Updating default Identifications
        AjaxRs<Identifications> responseSaveIdentifications = Utils.sendRequest(service.saveIdentifications(credentials.getIdentifications()), Status.SUCCESS).body();
//        assertEquals(responseSaveIdentifications.getStatus(), Status.SUCCESS, responseSaveIdentifications.getMessage());
        assertNotNull(responseSaveIdentifications.getPayload(), "Payload is null!");
        assertNotNull(responseSaveIdentifications.getPayload().getName(), "Identifications object is empty!");

        //Requesting updated Identifications
        AjaxRs<Identifications> responseGetUpdated = Utils.sendRequest(service.getIdentifications(new LoginGetContainer(login)), Status.SUCCESS).body();
//        assertEquals(responseGetUpdated.getStatus(), Status.SUCCESS, responseGetUpdated.getMessage());
        assertEquals(responseGetUpdated.getPayload().getLogin(), login, "returned Identifications object has wrong login!");
        assertEquals(responseGetUpdated.getPayload().getName(), credentials.getIdentifications().getName(), "returned Identifications object has wrong name!");
    }

    @Test
    public void saveIdentificationsIncorrectLogin() {
        Identifications identificationsIncorrect = Generator.generateIdentifications(Generator.UUID());

        identificationsIncorrect.setLogin("");
        AjaxRs<Identifications> response = Utils.sendRequest(service.saveIdentifications(identificationsIncorrect)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Identifications object with incorrect Login is saved!");
    }

    @Test
    public void saveIdentificationsIncorrectName() {
        Identifications identificationsIncorrect = Generator.generateIdentifications(Generator.UUID());

        identificationsIncorrect.setName("");
        AjaxRs<Identifications> response = Utils.sendRequest(service.saveIdentifications(identificationsIncorrect)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Identifications object with incorrect Name is saved!");
    }

    @Test
    public void getIdentifications() {

        String login = Generator.UUID();
        Credentials credentials = Generator.generateCredentials(login);

        // FIXME: 25.02.2018 Update this code

        //Registering Person
        AjaxRs<Person> responseCreatePerson = Utils.sendRequest(service.registerPerson(credentials), Status.SUCCESS).body();
//        assertEquals(responseCreatePerson.getStatus(), Status.SUCCESS, responseCreatePerson.getMessage());

        AjaxRs<Identifications> response = Utils.sendRequest(service.getIdentifications(new LoginGetContainer(login)), Status.SUCCESS).body();

//        assertEquals(response.getStatus(), Status.SUCCESS, response.getMessage());
        assertEquals(response.getPayload().getLogin(), login, "returned Identifications object has wrong login!");
        assertEquals(response.getPayload().getName(), credentials.getIdentifications().getName(), "returned Identifications object has wrong name!");
    }

}