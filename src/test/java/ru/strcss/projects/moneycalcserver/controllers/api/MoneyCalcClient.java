package ru.strcss.projects.moneycalcserver.controllers.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.Person;

public interface MoneyCalcClient {

    @POST("/api/registration/registerPerson")
    Call<AjaxRs> registerPerson(@Body Person person);

    @POST("/api/settings/getSettings")
    Call<AjaxRs> getSettings(@Body String login);


}
