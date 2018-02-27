package ru.strcss.projects.moneycalcserver.integration;

import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.strcss.projects.moneycalcserver.integration.testapi.MoneyCalcClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractControllerIT extends AbstractTestNGSpringContextTests {

    @LocalServerPort
    private int SpringBootPort;

    MoneyCalcClient service;

    @BeforeClass
    public void init() {
        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:" + SpringBootPort + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MoneyCalcClient.class);
    }

}
