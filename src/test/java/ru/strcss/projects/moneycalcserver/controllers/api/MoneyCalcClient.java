package ru.strcss.projects.moneycalcserver.controllers.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.FinanceStatistics;
import ru.strcss.projects.moneycalcserver.enitities.dto.Person;
import ru.strcss.projects.moneycalcserver.enitities.dto.PersonalSettings;

public interface MoneyCalcClient {

    @POST("/api/registration/registerPerson")
    Call<AjaxRs<Person>> registerPerson(@Body Person person);

    @POST("/api/settings/getSettings")
    Call<AjaxRs<PersonalSettings>> getSettings(@Body String login);

    @POST("/api/financeStatistics/getFinanceStats")
    Call<AjaxRs<FinanceStatistics>> getFinanceStats(@Body String login);

}
