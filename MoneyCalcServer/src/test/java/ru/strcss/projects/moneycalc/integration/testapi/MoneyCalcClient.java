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
    @POST("/api/settings/saveSettings")
    Call<MoneyCalcRs<Settings>> saveSettings(@Header("Authorization") String token,
                                             @Body SettingsUpdateContainer updateContainer);

    @GET("/api/settings/getSettings")
    Call<MoneyCalcRs<Settings>> getSettings(@Header("Authorization") String token);

    @POST("/api/settings/addSpendingSection")
    Call<MoneyCalcRs<List<SpendingSection>>> addSpendingSection(@Header("Authorization") String token,
                                                                @Body SpendingSectionAddContainer spendingSectionContainer);

    @POST("/api/settings/updateSpendingSection")
    Call<MoneyCalcRs<List<SpendingSection>>> updateSpendingSection(@Header("Authorization") String token,
                                                                   @Body SpendingSectionUpdateContainer updateContainer);

    @POST("/api/settings/deleteSpendingSection")
    Call<MoneyCalcRs<List<SpendingSection>>> deleteSpendingSection(@Header("Authorization") String token,
                                                                   @Body SpendingSectionDeleteContainer deleteContainer);

    @GET("/api/settings/getSpendingSections")
    Call<MoneyCalcRs<List<SpendingSection>>> getSpendingSections(@Header("Authorization") String token);

    /**
     * Identifications
     */
    @POST("/api/identifications/saveIdentifications")
    Call<MoneyCalcRs<Identifications>> saveIdentifications(@Header("Authorization") String token,
                                                           @Body IdentificationsUpdateContainer updateContainer);

    @GET("/api/identifications/getIdentifications")
    Call<MoneyCalcRs<Identifications>> getIdentifications(@Header("Authorization") String token);

    /**
     * Transactions
     */
    @POST("/api/finance/transactions/getTransactions")
    Call<MoneyCalcRs<List<Transaction>>> getTransactions(@Header("Authorization") String token,
                                                         @Body TransactionsSearchContainer container);

    @POST("/api/finance/transactions/addTransaction")
    Call<MoneyCalcRs<Transaction>> addTransaction(@Header("Authorization") String token,
                                                  @Body TransactionAddContainer transactionContainer);

    @POST("/api/finance/transactions/deleteTransaction")
    Call<MoneyCalcRs<Void>> deleteTransaction(@Header("Authorization") String token,
                                              @Body TransactionDeleteContainer transactionContainer);

    @POST("/api/finance/transactions/updateTransaction")
    Call<MoneyCalcRs<Transaction>> updateTransaction(@Header("Authorization") String token,
                                                     @Body TransactionUpdateContainer transactionUpdateContainer);

    @POST("/api/finance/transactions/dropStatistics")
    Call<MoneyCalcRs<List<Transaction>>> dropTransactions(@Header("Authorization") String token);

    /**
     * Statistics
     */
    @POST("/api/statistics/financeSummary/getFinanceSummaryBySection")
    Call<MoneyCalcRs<List<FinanceSummaryBySection>>> getFinanceSummaryBySection(@Header("Authorization") String token,
                                                                                @Body FinanceSummaryGetContainer getContainer);



    @GET("/api/access/getAccess")
    Call<MoneyCalcRs<Access>> getAccess(@Header("Authorization") String token);
}
