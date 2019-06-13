package ru.strcss.projects.moneycalc.moneycalcmigrator.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Credentials;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Person;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;

public interface MigrationAPI {

    @POST("/login")
    Call<Void> login(@Body Access access);

    @POST("/api/registration/register")
    Call<Person> registerPerson(@Body Credentials credentials);

    /**
     * Settings
     */
    @POST("/api/spendingSections")
    Call<SpendingSectionsSearchRs> addSpendingSection(@Header("Authorization") String token, @Body SpendingSection spendingSection);

    @GET("/api/spendingSections")
    Call<SpendingSectionsSearchRs> getSpendingSections(@Header("Authorization") String token);

    /**
     * Transactions
     */
    @POST("/api/transactions")
    Call<Transaction> addTransaction(@Header("Authorization") String token, @Body Transaction transaction);

    @DELETE("/api/transactions/{transactionId}")
    Call<Void> deleteTransaction(@Header("Authorization") String token, @Path(value = "transactionId") Long transactionId);
}
