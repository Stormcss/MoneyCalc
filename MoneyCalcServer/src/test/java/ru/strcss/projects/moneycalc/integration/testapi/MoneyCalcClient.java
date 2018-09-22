package ru.strcss.projects.moneycalc.integration.testapi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.identifications.IdentificationsUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SettingsUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.statistics.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.*;

import java.util.List;

public interface MoneyCalcClient {

    @POST("/api/registration/register")
    Call<MoneyCalcRs<Person>> registerPerson(@Body Credentials credentials);

    @POST("/login")
    Call<Void> login(@Body Access access);

    /**
     * Settings
     */
    @POST("/api/settings/update")
    Call<MoneyCalcRs<Settings>> updateSettings(@Header("Authorization") String token,
                                               @Body SettingsUpdateContainer updateContainer);

    @GET("/api/settings/get")
    Call<MoneyCalcRs<Settings>> getSettings(@Header("Authorization") String token);

    @POST("/api/settings/spendingSection/add")
    Call<MoneyCalcRs<List<SpendingSection>>> addSpendingSection(@Header("Authorization") String token,
                                                                @Body SpendingSectionAddContainer spendingSectionContainer);

    @POST("/api/settings/spendingSection/update")
    Call<MoneyCalcRs<List<SpendingSection>>> updateSpendingSection(@Header("Authorization") String token,
                                                                   @Body SpendingSectionUpdateContainer updateContainer);

    @POST("/api/settings/spendingSection/delete")
    Call<MoneyCalcRs<List<SpendingSection>>> deleteSpendingSection(@Header("Authorization") String token,
                                                                   @Body SpendingSectionDeleteContainer deleteContainer);

    @GET("/api/settings/spendingSection/get")
    Call<MoneyCalcRs<List<SpendingSection>>> getSpendingSections(@Header("Authorization") String token);

    @GET("/api/settings/spendingSection/get?withNonAdded=true")
    Call<MoneyCalcRs<List<SpendingSection>>> getSpendingSectionsWithNonAdded(@Header("Authorization") String token);

    @GET("/api/settings/spendingSection/get?withRemoved=true")
    Call<MoneyCalcRs<List<SpendingSection>>> getSpendingSectionsWithRemoved(@Header("Authorization") String token);

    @GET("/api/settings/spendingSection/get?withRemovedOnly=true")
    Call<MoneyCalcRs<List<SpendingSection>>> getSpendingSectionsWithRemovedOnly(@Header("Authorization") String token);

    /**
     * Identifications
     */
    @POST("/api/identifications/update")
    Call<MoneyCalcRs<Identifications>> saveIdentifications(@Header("Authorization") String token,
                                                           @Body IdentificationsUpdateContainer updateContainer);

    @GET("/api/identifications/get")
    Call<MoneyCalcRs<Identifications>> getIdentifications(@Header("Authorization") String token);

    /**
     * Transactions
     */
    @POST("/api/transactions/getFiltered")
    Call<MoneyCalcRs<List<Transaction>>> getTransactions(@Header("Authorization") String token,
                                                         @Body TransactionsSearchContainer container);

    @POST("/api/transactions/add")
    Call<MoneyCalcRs<Transaction>> addTransaction(@Header("Authorization") String token,
                                                  @Body TransactionAddContainer transactionContainer);

    @POST("/api/transactions/delete")
    Call<MoneyCalcRs<Void>> deleteTransaction(@Header("Authorization") String token,
                                              @Body TransactionDeleteContainer transactionContainer);

    @POST("/api/transactions/update")
    Call<MoneyCalcRs<Transaction>> updateTransaction(@Header("Authorization") String token,
                                                     @Body TransactionUpdateContainer transactionUpdateContainer);

    /**
     * Statistics
     */
    @POST("/api/stats/summaryBySection/get")
    Call<MoneyCalcRs<List<FinanceSummaryBySection>>> getFinanceSummaryBySection(@Header("Authorization") String token);

    @POST("/api/stats/summaryBySection/getFiltered")
    Call<MoneyCalcRs<List<FinanceSummaryBySection>>> getFinanceSummaryBySection(@Header("Authorization") String token,
                                                                                @Body FinanceSummaryGetContainer getContainer);

    /**
     * Access
     */
    @GET("/api/access/get")
    Call<MoneyCalcRs<Access>> getAccess(@Header("Authorization") String token);
}
