package ru.strcss.projects.moneycalcserver.controllers.utils;

import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalcserver.controllers.testapi.MoneyCalcClient;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.UUID;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.personGenerator;

@Slf4j
public class Utils {
    public static <T> Response<AjaxRs<T>> sendRequest(Call<AjaxRs<T>> call) {

        Response<AjaxRs<T>> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(response != null, "Response is null!");
        assertTrue(response.body() != null, "Response body is null!");
//        assertEquals(response.body().getStatus(), Status.SUCCESS, response.body().getMessage());

        log.debug("{} - {}", response.body().getMessage(), response.body().getStatus().name());

        return response;
    }

    public static String savePersonGetLogin(MoneyCalcClient service) {
        String login = UUID();
        Person person = personGenerator(login);

        //Registering Person
        AjaxRs<Person> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications()))).body();
        assertEquals(responseCreatePerson.getStatus(), Status.SUCCESS, responseCreatePerson.getMessage());

        return login;
    }

//    public static Person savePersonGetPerson(MoneyCalcClient service) throws IOException {
//        String login = UUID();
//        Person person = personGenerator(login);
//
//        //Registering Person
//        Response<AjaxRs<Person>> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
//        assertEquals(responseCreatePerson.body().getStatus(), Status.SUCCESS, responseCreatePerson.body().getMessage());
//
//        return person;
//    }

}
