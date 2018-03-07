package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.integration.utils.Generator;

import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.integration.utils.Generator.generateCredentials;
import static ru.strcss.projects.moneycalc.integration.utils.Utils.sendRequest;

@Slf4j
public class RegisterControllerIT extends AbstractIT {

    @Test
    public void registerCorrectPerson() {
        AjaxRs<Person> response = sendRequest(service.registerPerson(generateCredentials())).body();

        assertEquals(response.getStatus(), Status.SUCCESS, response.getMessage());
    }

    @Test
    public void registerExistingLoginPerson() {
        String login = Generator.UUID();

        AjaxRs<Person> saveNewResponse = sendRequest(service.registerPerson(generateCredentials(login))).body();
        assertEquals(saveNewResponse.getStatus(), Status.SUCCESS, "New Person was not saved!");

        AjaxRs<Person> saveExistingResponse = sendRequest(service.registerPerson(generateCredentials(login))).body();
        assertEquals(saveExistingResponse.getStatus(), Status.ERROR, "Existing Person was saved!");
    }

    @Test
    public void registerExistingEmailPerson() {
        Credentials credentials = generateCredentials();

        AjaxRs<Person> saveNewResponse = sendRequest(service.registerPerson(credentials)).body();
        assertEquals(saveNewResponse.getStatus(), Status.SUCCESS, "New Person was not saved!");

        //Creating new Credentials with the same Email
        credentials.getAccess().setLogin(Generator.UUID());

        AjaxRs<Person> saveExistingResponse = sendRequest(service.registerPerson(credentials)).body();
        assertEquals(saveExistingResponse.getStatus(), Status.ERROR, "Person with existing email was saved!");
    }

    @Test
    public void registerEmptyPassword() {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setPassword(null);

        AjaxRs<Person> response = sendRequest(service.registerPerson(credentials)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Person with empty password was saved!");
    }

    @Test
    public void registerEmptyEmail() {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setEmail(null);

        AjaxRs<Person> response = sendRequest(service.registerPerson(credentials)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Person with empty Email was saved!");
    }

    @Test
    public void registerIncorrectEmail() {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setEmail("123");

        AjaxRs<Person> response = sendRequest(service.registerPerson(credentials)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Person with incorrect Email was saved!");
    }

    @Test
    public void registerEmptyLogin() {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setLogin(null);

        AjaxRs<Person> response = sendRequest(service.registerPerson(credentials)).body();

        assertEquals(response.getStatus(), Status.ERROR, "Person with empty login was saved!");
    }

    @Test
    public void registerEmptyAll() {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setPassword(null);
        credentials.getAccess().setLogin(null);
        credentials.getAccess().setEmail(null);
        credentials.getIdentifications().setName(null);

        AjaxRs<Person> response = sendRequest(service.registerPerson(credentials)).body();

        assertEquals(response.getStatus(), Status.ERROR, response.getMessage());
    }
}