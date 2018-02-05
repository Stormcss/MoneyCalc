package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.Person;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.UUID;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.personGenerator;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

@Slf4j
public class RegisterControllerTest extends AbstractControllerTest {
    private Person savedPerson = personGenerator();

    @Test(priority = -2)
    public void registerCorrectPerson() throws IOException {
        Response<AjaxRs<Person>> response = sendRequest(service.registerPerson(new Credentials(savedPerson.getAccess(), savedPerson.getIdentifications())));

        assertEquals(response.body().getStatus(), Status.SUCCESS, response.body().getMessage());
    }

    @Test(priority = -1)
    public void registerExistingLoginPerson() throws IOException {
        Response<AjaxRs<Person>> response = sendRequest(service.registerPerson(new Credentials(savedPerson.getAccess(), savedPerson.getIdentifications())));

        assertEquals(response.body().getStatus(), Status.ERROR, response.body().getMessage());
    }

    @Test(priority = 0)
    public void registerExistingEmailPerson() throws IOException {
        savedPerson.getAccess().setLogin(UUID());

        // TODO: 23.01.2018 is everything right here?

        Response<AjaxRs<Person>> response = sendRequest(service.registerPerson(new Credentials(savedPerson.getAccess(), savedPerson.getIdentifications())));

        assertEquals(response.body().getStatus(), Status.ERROR, response.body().getMessage());
    }

    @Test
    public void registerIncorrectPassword() throws IOException {
        Person person = personGenerator();
        person.getAccess().setPassword("");
        Response<AjaxRs<Person>> response = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));

        assertEquals(response.body().getStatus(), Status.ERROR, response.body().getMessage());
    }

    @Test
    public void registerIncorrectEmail() throws IOException {
        Person person = personGenerator();
        person.getAccess().setEmail("");
        Response<AjaxRs<Person>> response = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));

        assertEquals(response.body().getStatus(), Status.ERROR, response.body().getMessage());
    }

    @Test
    public void registerIncorrectLogin() throws IOException {

        Person person = personGenerator();
        person.getAccess().setLogin("");
        Response<AjaxRs<Person>> response = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));

        assertEquals(response.body().getStatus(), Status.ERROR);
    }

    @Test
    public void registerIncorrectAll() throws IOException {
        Person person = personGenerator();
        person.getAccess().setPassword("");
        person.getAccess().setLogin("");
        person.getAccess().setEmail("");
        person.getIdentifications().setName("");
        Response<AjaxRs<Person>> response = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));

        assertEquals(response.body().getStatus(), Status.ERROR);
    }
}