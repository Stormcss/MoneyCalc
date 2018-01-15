package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.strcss.projects.moneycalcserver.controllers.api.MoneyCalcClient;
import ru.strcss.projects.moneycalcserver.controllers.utils.Generator;
import ru.strcss.projects.moneycalcserver.controllers.utils.Utils;
import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.Person;
import ru.strcss.projects.moneycalcserver.enitities.dto.Status;

import java.io.IOException;

@Slf4j
public class ApiControllerRegisterTest {
    private MoneyCalcClient service;
    private Person savedPerson = Generator.personGenerator();

    @BeforeClass
    public void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MoneyCalcClient.class);
    }

    @Test(priority = -2)
    public void registerCorrectPerson() throws IOException {
        Response<AjaxRs> response = Utils.sendRequest(service.registerPerson(savedPerson));

        Assert.assertEquals(response.body().getStatus(), Status.SUCCESS, response.body().getMessage());
    }

    @Test(priority = -1)
    public void registerExistingLoginPerson() throws IOException {
        Response<AjaxRs> response = Utils.sendRequest(service.registerPerson(savedPerson));

        Assert.assertEquals(response.body().getStatus(), Status.ERROR, response.body().getMessage());
    }

    @Test(priority = 0)
    public void registerExistingEmailPerson() throws IOException {
        savedPerson.getAccess().setLogin(Generator.UUID());

        Response<AjaxRs> response = Utils.sendRequest(service.registerPerson(savedPerson));

        Assert.assertEquals(response.body().getStatus(), Status.ERROR, response.body().getMessage());
    }

    @Test
    public void registerIncorrectPassword() throws IOException {
        Person person = Generator.personGenerator();
        person.getAccess().setPassword("");
        Response<AjaxRs> response = Utils.sendRequest(service.registerPerson(person));

        Assert.assertEquals(response.body().getStatus(), Status.ERROR, response.body().getMessage());
    }

    @Test
    public void registerIncorrectEmail() throws IOException {
        Person person = Generator.personGenerator();
        person.getAccess().setEmail("");
        Response<AjaxRs> response = Utils.sendRequest(service.registerPerson(person));

        Assert.assertEquals(response.body().getStatus(), Status.ERROR, response.body().getMessage());
    }

    @Test
    public void registerIncorrectLogin() throws IOException {

        Person person = Generator.personGenerator();
        person.getAccess().setLogin("");
        Response<AjaxRs> response = Utils.sendRequest(service.registerPerson(person));

        Assert.assertEquals(response.body().getStatus(), Status.ERROR);
    }

    @Test
    public void registerIncorrectAll() throws IOException {
        Person person = Generator.personGenerator();
        person.getAccess().setPassword("");
        person.getAccess().setLogin("");
        person.getAccess().setEmail("");
        person.getPersonalIdentifications().setName("");
        Response<AjaxRs> response = Utils.sendRequest(service.registerPerson(person));

        Assert.assertEquals(response.body().getStatus(), Status.ERROR);
    }
}