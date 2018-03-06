package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.integration.utils.Generator;

import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.integration.utils.Utils.sendRequest;

@Slf4j
public class RegisterControllerIT extends AbstractIT {

    @Test
    public void registerCorrectPerson() {
        AjaxRs<Person> response = sendRequest(service.registerPerson(Generator.generateCredentials())).body();

        assertEquals(response.getStatus(), Status.SUCCESS, response.getMessage());
    }

    @Test
    public void registerExistingLoginPerson() {
        String login = Generator.UUID();

        AjaxRs<Person> saveNewResponse = sendRequest(service.registerPerson(Generator.generateCredentials(login))).body();
        assertEquals(saveNewResponse.getStatus(), Status.SUCCESS, "New Person was not saved!");

        AjaxRs<Person> saveExistingResponse = sendRequest(service.registerPerson(Generator.generateCredentials(login))).body();
        assertEquals(saveExistingResponse.getStatus(), Status.ERROR, "Existing Person was saved!");
    }

    @Test
    public void registerExistingEmailPerson() {
        Credentials credentials = Generator.generateCredentials();

        AjaxRs<Person> saveNewResponse = sendRequest(service.registerPerson(credentials)).body();
        assertEquals(saveNewResponse.getStatus(), Status.SUCCESS, "New Person was not saved!");

        //Creating new Credentials with the same Email
        credentials.getAccess().setLogin(Generator.UUID());

        AjaxRs<Person> saveExistingResponse = sendRequest(service.registerPerson(credentials)).body();
        assertEquals(saveExistingResponse.getStatus(), Status.ERROR, "Person with existing email was saved!");
    }

    @Test
    public void registerIncorrectPassword() {
        Credentials credentials = Generator.generateCredentials();
        credentials.getAccess().setPassword(null);

        AjaxRs<Person> response = sendRequest(service.registerPerson(credentials)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Person with incorrect password was saved!");
    }

    @Test
    public void registerIncorrectEmail() {
        Credentials credentials = Generator.generateCredentials();
        credentials.getAccess().setEmail(null);

        AjaxRs<Person> response = sendRequest(service.registerPerson(credentials)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Person with empty Email was saved!");
    }

    @Test
    public void registerIncorrectLogin() {
        Credentials credentials = Generator.generateCredentials();
        credentials.getAccess().setLogin("");

        AjaxRs<Person> response = sendRequest(service.registerPerson(credentials)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Person with empty login was saved!");
    }

    @Test
    public void registerIncorrectAll() {
        Credentials credentials = Generator.generateCredentials();
        credentials.getAccess().setPassword("");
        credentials.getAccess().setLogin("");
        credentials.getAccess().setEmail("");
        credentials.getIdentifications().setName("");

        AjaxRs<Person> response = sendRequest(service.registerPerson(credentials)).body();

        assertEquals(response.getStatus(), Status.ERROR, response.getMessage());
    }
}