package ru.strcss.projects.moneycalc.integration.utils;

import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.integration.testapi.MoneyCalcClient;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Slf4j
public class Utils {

    public static <T> Response<AjaxRs<T>> sendRequest(Call<AjaxRs<T>> call) {
        return sendRequest(call, null);
    }

    public static <T> Response<AjaxRs<T>> sendRequest(Call<AjaxRs<T>> call, Status expectedStatus) {
        Response<AjaxRs<T>> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertNotNull(response, "Response is null!");
        assertNotNull(response.body(), "Response body is null!");
        if (expectedStatus != null) assertEquals(response.body().getStatus(), expectedStatus, response.body().getMessage());

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
        String login = Generator.UUID();
        sendRequest(service.registerPerson(Generator.generateCredentials(login)), Status.SUCCESS).body();
        return login;
    }
}
