package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.Person;

import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.UUID;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.personGenerator;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

@Slf4j
public class RegisterControllerTest extends AbstractControllerTest {
    private Person savedPerson = personGenerator();

    @Test(priority = -2)
    public void registerCorrectPerson(){
        AjaxRs<Person> response = sendRequest(service.registerPerson(new Credentials(savedPerson.getAccess(), savedPerson.getIdentifications()))).body();

        assertEquals(response.getStatus(), Status.SUCCESS, response.getMessage());
    }

    @Test(priority = -1)
    public void registerExistingLoginPerson(){
        AjaxRs<Person> response = sendRequest(service.registerPerson(new Credentials(savedPerson.getAccess(), savedPerson.getIdentifications()))).body();

        assertEquals(response.getStatus(), Status.ERROR, response.getMessage());
    }

    @Test
    public void registerExistingEmailPerson(){
        savedPerson.getAccess().setLogin(UUID());

        // TODO: 23.01.2018 is everything right here?

        AjaxRs<Person> response = sendRequest(service.registerPerson(new Credentials(savedPerson.getAccess(), savedPerson.getIdentifications()))).body();

        assertEquals(response.getStatus(), Status.ERROR, response.getMessage());
    }

    @Test
    public void registerIncorrectPassword(){
        Person person = personGenerator();
        person.getAccess().setPassword("");
        AjaxRs<Person> response = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications()))).body();

        assertEquals(response.getStatus(), Status.ERROR, response.getMessage());
    }

    @Test
    public void registerIncorrectEmail(){
        Person person = personGenerator();
        person.getAccess().setEmail("");
        AjaxRs<Person> response = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications()))).body();

        assertEquals(response.getStatus(), Status.ERROR, response.getMessage());
    }

    @Test
    public void registerIncorrectLogin(){

        Person person = personGenerator();
        person.getAccess().setLogin("");
        AjaxRs<Person> response = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications()))).body();

        assertEquals(response.getStatus(), Status.ERROR);
    }

    @Test
    public void registerIncorrectAll(){
        Person person = personGenerator();
        person.getAccess().setPassword("");
        person.getAccess().setLogin("");
        person.getAccess().setEmail("");
        person.getIdentifications().setName("");
        AjaxRs<Person> response = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications()))).body();

        assertEquals(response.getStatus(), Status.ERROR);
    }
}