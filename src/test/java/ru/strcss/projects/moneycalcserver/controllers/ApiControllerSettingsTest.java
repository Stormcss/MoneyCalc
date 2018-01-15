package ru.strcss.projects.moneycalcserver.controllers;

import org.junit.Test;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
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

public class ApiControllerSettingsTest {

    private MoneyCalcClient service;

    @BeforeClass
    public void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MoneyCalcClient.class);
    }


    @Test
    public void getSettings() throws IOException {

        Person person = Generator.personGenerator();

        person.getAccess().getLogin();

        Response<AjaxRs> response =
                Utils.sendRequest(
                        service.getSettings(
                                person.getAccess().getLogin()));

        Assert.assertTrue(response.body().getStatus().equals(Status.SUCCESS));
    }
}