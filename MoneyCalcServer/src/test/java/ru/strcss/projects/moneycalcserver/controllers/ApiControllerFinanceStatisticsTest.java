//package ru.strcss.projects.moneycalcserver.controllers;
//
//import lombok.extern.slf4j.Slf4j;
//import org.testng.Assert;
//import org.testng.annotations.BeforeClass;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//import ru.strcss.projects.moneycalcserver.controllers.api.MoneyCalcClient;
//import ru.strcss.projects.moneycalcserver.controllers.utils.Generator;
//import ru.strcss.projects.moneycalcserver.controllers.utils.Utils;
//import ru.strcss.projects.moneycalcserver.enitities.dto.*;
//
//import java.io.IOException;
//
//@Slf4j
//public class ApiControllerFinanceStatisticsTest {
//    private MoneyCalcClient service;
//
//    @BeforeClass
//    public void init() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://localhost:8080/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        service = retrofit.create(MoneyCalcClient.class);
//    }
//
////    @Test
//    public void getFinanceStats() throws IOException {
//
//        Person person = Generator.personGenerator();
//
//        //saving Person
//        Response<AjaxRs<Person>> rsSave = Utils.sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
//        Assert.assertEquals(rsSave.body().getStatus(), Status.SUCCESS, rsSave.body().getMessage());
//
//        //requesting his FinanceStatistics
//        Response<AjaxRs<FinanceStatistics>> rsGetStats = Utils.sendRequest(service.getFinanceStats(person.getAccess().getLogin()));
//        Assert.assertEquals(rsGetStats.body().getStatus(), Status.SUCCESS, rsGetStats.body().getMessage());
//        log.debug("FinanceStatistics: {}", rsGetStats.body().getPayload());
//    }
//}