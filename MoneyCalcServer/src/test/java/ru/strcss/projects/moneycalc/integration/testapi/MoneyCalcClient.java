package ru.strcss.projects.moneycalc.integration.testapi;

import retrofit2.Call;
import retrofit2.http.*;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SettingsUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.statistics.FinanceSummaryFilter;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.entities.*;

import java.util.List;

public interface MoneyCalcClient {

    @POST("/api/registration/register")
    Call<MoneyCalcRs<Person>> registerPerson(@Body Credentials credentials);

    @POST("/login")
    Call<Void> login(@Body Access access);

    /**
     * Settings
     */
    @PUT("/api/settings")
    Call<MoneyCalcRs<Settings>> updateSettings(@Header("Authorization") String token,
                                               @Body SettingsUpdateContainer updateContainer);

    @GET("/api/settings")
    Call<MoneyCalcRs<Settings>> getSettings(@Header("Authorization") String token);

    /**
     * Spending Section
     */
    @POST("/api/spendingSections")
    Call<MoneyCalcRs<List<SpendingSection>>> addSpendingSection(@Header("Authorization") String token,
                                                                @Body SpendingSection spendingSection);

    @PUT("/api/spendingSections")
    Call<MoneyCalcRs<List<SpendingSection>>> updateSpendingSection(@Header("Authorization") String token,
                                                                   @Body SpendingSectionUpdateContainer updateContainer);

    @DELETE("/api/spendingSections/{sectionId}")
    Call<MoneyCalcRs<List<SpendingSection>>> deleteSpendingSection(@Header("Authorization") String token,
                                                                   @Path("sectionId") Integer sectionId);

    @GET("/api/spendingSections")
    Call<MoneyCalcRs<List<SpendingSection>>> getSpendingSections(@Header("Authorization") String token);

    @GET("/api/spendingSections?withNonAdded=true")
    Call<MoneyCalcRs<List<SpendingSection>>> getSpendingSectionsWithNonAdded(@Header("Authorization") String token);

    @GET("/api/spendingSections?withRemoved=true")
    Call<MoneyCalcRs<List<SpendingSection>>> getSpendingSectionsWithRemoved(@Header("Authorization") String token);

    @GET("/api/spendingSections?withRemovedOnly=true")
    Call<MoneyCalcRs<List<SpendingSection>>> getSpendingSectionsWithRemovedOnly(@Header("Authorization") String token);

    /**
     * Identifications
     */
    @PUT("/api/identifications")
    Call<MoneyCalcRs<Identifications>> saveIdentifications(@Header("Authorization") String token,
                                                           @Body Identifications identifications);

    @GET("/api/identifications")
    Call<MoneyCalcRs<Identifications>> getIdentifications(@Header("Authorization") String token);

    /**
     * Transactions
     */
    @POST("/api/transactions/getFiltered")
    Call<MoneyCalcRs<List<Transaction>>> getTransactions(@Header("Authorization") String token,
                                                         @Body TransactionsSearchFilter container);

    @POST("/api/transactions")
    Call<MoneyCalcRs<Transaction>> addTransaction(@Header("Authorization") String token,
                                                  @Body Transaction transaction);

    @DELETE("/api/transactions/{transactionId}")
    Call<MoneyCalcRs<Void>> deleteTransaction(@Header("Authorization") String token,
                                              @Path("transactionId") Long transactionId);

    @PUT("/api/transactions")
    Call<MoneyCalcRs<Transaction>> updateTransaction(@Header("Authorization") String token,
                                                     @Body TransactionUpdateContainer transactionUpdateContainer);

    /**
     * Statistics
     */
    @POST("/api/stats/summaryBySection")
    Call<MoneyCalcRs<List<FinanceSummaryBySection>>> getFinanceSummaryBySection(@Header("Authorization") String token);

    @POST("/api/stats/summaryBySection/getFiltered")
    Call<MoneyCalcRs<List<FinanceSummaryBySection>>> getFinanceSummaryBySection(@Header("Authorization") String token,
                                                                                @Body FinanceSummaryFilter getContainer);

    /**
     * Access
     */
    @GET("/api/access/get")
    Call<MoneyCalcRs<Access>> getAccess(@Header("Authorization") String token);
}
