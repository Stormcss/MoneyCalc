package ru.strcss.projects.moneycalcmigrator.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.Transaction;

public interface MigrationAPI {
    /**
     *  Settings
     */
    @POST("/api/settings/saveSettings")
    Call<AjaxRs<Settings>> saveSettings(@Body Settings settings);

    @POST("/api/settings/getSettings")
    Call<AjaxRs<Settings>> getSettings(@Body String login);


    @POST("/api/registration/registerPerson")
    Call<AjaxRs<Person>> registerPerson(@Body Credentials credentials);

    @POST("/api/finance/transactions/addTransaction")
    Call<AjaxRs<Transaction>> addTransaction(@Body TransactionAddContainer transactionContainer);

    @POST("/api/finance/transactions/deleteTransaction")
    Call<AjaxRs<Void>> deleteTransaction(@Body TransactionDeleteContainer transactionContainer);
}
