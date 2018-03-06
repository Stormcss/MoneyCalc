package ru.strcss.projects.moneycalcmigrator.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.util.List;

public interface MigrationAPI {

    @POST("/login")
    Call<Void> login(@Body Access access);

    @POST("/api/registration/register")
    Call<AjaxRs<Person>> registerPerson(@Body Credentials credentials);

    /**
     *  Settings
     */
    @POST("/api/settings/addSpendingSection")
    Call<AjaxRs<List<SpendingSection>>> addSpendingSection(@Header("Authorization") String token, @Body SpendingSectionAddContainer spendingSectionContainer);

    @GET("/api/settings/getSpendingSections")
    Call<AjaxRs<List<SpendingSection>>> getSpendingSections(@Header("Authorization") String token);

    /**
     *  Transactions
     */
    @POST("/api/finance/transactions/addTransaction")
    Call<AjaxRs<Transaction>> addTransaction(@Header("Authorization") String token, @Body TransactionAddContainer transactionContainer);

    @POST("/api/finance/transactions/deleteTransaction")
    Call<AjaxRs<Void>> deleteTransaction(@Header("Authorization") String token, @Body TransactionDeleteContainer transactionContainer);
}
