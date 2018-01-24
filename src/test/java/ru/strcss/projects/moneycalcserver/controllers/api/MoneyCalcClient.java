package ru.strcss.projects.moneycalcserver.controllers.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.strcss.projects.moneycalcserver.enitities.dto.*;

public interface MoneyCalcClient {

    /**
     *  Settings
     */

    @POST("/api/registration/registerPerson")
    Call<AjaxRs<Person>> registerPerson(@Body Credentials credentials);

    @POST("/api/financeStatistics/getFinanceStats")
    Call<AjaxRs<FinanceStatistics>> getFinanceStats(@Body String login);


    /**
     *  Settings
     */
    @POST("/api/settings/saveSettings")
    Call<AjaxRs<Settings>> saveSettings(@Body Settings settings);

    @POST("/api/settings/getSettings")
    Call<AjaxRs<Settings>> getSettings(@Body String login);

    /**
     *  Identifications
     */
    @POST("/api/identifications/saveIdentifications")
    Call<AjaxRs<Identifications>> saveIdentifications(@Body Identifications settings);

    @POST("/api/identifications/getIdentifications")
    Call<AjaxRs<Identifications>> getIdentifications(@Body String login);

}
