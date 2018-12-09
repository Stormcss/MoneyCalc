package ru.strcss.projects.moneycalcmigrator.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.entities.Access;
import ru.strcss.projects.moneycalc.entities.Person;
import ru.strcss.projects.moneycalc.entities.SpendingSection;
import ru.strcss.projects.moneycalc.entities.Transaction;

import java.util.List;

public interface MigrationAPI {

    @POST("/login")
    Call<Void> login(@Body Access access);

    @POST("/api/registration/register")
    Call<MoneyCalcRs<Person>> registerPerson(@Body Credentials credentials);

    /**
     * Settings
     */
    @POST("/api/spendingSections")
    Call<MoneyCalcRs<List<SpendingSection>>> addSpendingSection(@Header("Authorization") String token, @Body SpendingSection spendingSection);

    @GET("/api/spendingSections")
    Call<MoneyCalcRs<List<SpendingSection>>> getSpendingSections(@Header("Authorization") String token);

    /**
     * Transactions
     */
    @POST("/api/transactions")
    Call<MoneyCalcRs<Transaction>> addTransaction(@Header("Authorization") String token, @Body Transaction transaction);

    @DELETE("/api/transactions/{transactionId}")
    Call<MoneyCalcRs<Void>> deleteTransaction(@Header("Authorization") String token, @Path(value = "transactionId") Long transactionId);
}
