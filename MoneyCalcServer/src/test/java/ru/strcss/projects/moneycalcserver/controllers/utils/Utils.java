package ru.strcss.projects.moneycalcserver.controllers.utils;

import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalcserver.controllers.testapi.MoneyCalcClient;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.UUID;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.generateCredentials;

@Slf4j
public class Utils {

    public static <T> Response<AjaxRs<T>> sendRequest(Call<AjaxRs<T>> call) {
        Response<AjaxRs<T>> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertNotNull(response, "Response is null!");
        assertNotNull(response.body(), "Response body is null!");

        log.debug("{} - {}", response.body().getMessage(), response.body().getStatus().name());
        return response;
    }

    /**
     * Save Person with random login and return it
     *
     * @param service - Retrofit configured service
     * @return login of generated Person
     */
    public static String savePersonGetLogin(MoneyCalcClient service) {
        String login = UUID();

        //Registering Person
        AjaxRs<Person> responseCreatePerson = sendRequest(service.registerPerson(generateCredentials(login))).body();
        assertEquals(responseCreatePerson.getStatus(), Status.SUCCESS, responseCreatePerson.getMessage());

        return login;
    }
}
