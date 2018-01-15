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
        //saving Person
        Response<AjaxRs> rsSave = Utils.sendRequest(service.registerPerson(person));
        Assert.assertEquals(rsSave.body().getStatus(), Status.SUCCESS, rsSave.body().getMessage());

        //requesting his Settings
        Response<AjaxRs> rsGetSettings = Utils.sendRequest(service.getSettings(person.getAccess().getLogin()));
        Assert.assertEquals(rsGetSettings.body().getStatus(), Status.SUCCESS, rsGetSettings.body().getMessage());
        log.debug("PersonalSettings: {}", rsGetSettings.body().getPayload());
    }
}