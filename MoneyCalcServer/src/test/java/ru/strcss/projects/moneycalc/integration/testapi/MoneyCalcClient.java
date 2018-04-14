package ru.strcss.projects.moneycalc.integration.testapi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
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
    Call<AjaxRs<Person>> registerPerson(@Body Credentials credentials);

    @POST("/login")
    Call<Void> login(@Body Access access);

    /**
     * Settings
     */
    @POST("/api/settings/saveSettings")
    Call<AjaxRs<Settings>> saveSettings(@Header("Authorization") String token,
                                        @Body SettingsUpdateContainer updateContainer);

    @GET("/api/settings/getSettings")
    Call<AjaxRs<Settings>> getSettings(@Header("Authorization") String token);

    @POST("/api/settings/addSpendingSection")
    Call<AjaxRs<List<SpendingSection>>> addSpendingSection(@Header("Authorization") String token,
                                                           @Body SpendingSectionAddContainer spendingSectionContainer);

    @POST("/api/settings/updateSpendingSection")
    Call<AjaxRs<List<SpendingSection>>> updateSpendingSection(@Header("Authorization") String token,
                                                              @Body SpendingSectionUpdateContainer updateContainer);

    @POST("/api/settings/deleteSpendingSection")
    Call<AjaxRs<List<SpendingSection>>> deleteSpendingSection(@Header("Authorization") String token,
                                                              @Body SpendingSectionDeleteContainer deleteContainer);

    @GET("/api/settings/getSpendingSections")
    Call<AjaxRs<List<SpendingSection>>> getSpendingSections(@Header("Authorization") String token);

    /**
     * Identifications
     */
    @POST("/api/identifications/saveIdentifications")
    Call<AjaxRs<Identifications>> saveIdentifications(@Header("Authorization") String token,
                                                      @Body IdentificationsUpdateContainer updateContainer);

    @GET("/api/identifications/getIdentifications")
    Call<AjaxRs<Identifications>> getIdentifications(@Header("Authorization") String token);

    /**
     * Transactions
     */
    @POST("/api/finance/transactions/getTransactions")
    Call<AjaxRs<List<Transaction>>> getTransactions(@Header("Authorization") String token,
                                                    @Body TransactionsSearchContainer container);

    @POST("/api/finance/transactions/addTransaction")
    Call<AjaxRs<Transaction>> addTransaction(@Header("Authorization") String token,
                                             @Body TransactionAddContainer transactionContainer);

    @POST("/api/finance/transactions/deleteTransaction")
    Call<AjaxRs<Void>> deleteTransaction(@Header("Authorization") String token,
                                         @Body TransactionDeleteContainer transactionContainer);

    @POST("/api/finance/transactions/updateTransaction")
    Call<AjaxRs<Transaction>> updateTransaction(@Header("Authorization") String token,
                                                @Body TransactionUpdateContainer transactionUpdateContainer);

    @POST("/api/finance/transactions/dropStatistics")
    Call<AjaxRs<List<Transaction>>> dropTransactions(@Header("Authorization") String token);

    /**
     * Statistics
     */
    @POST("/api/statistics/financeSummary/getFinanceSummaryBySection")
    Call<AjaxRs<List<FinanceSummaryBySection>>> getFinanceSummaryBySection(@Header("Authorization") String token,
                                                                           @Body FinanceSummaryGetContainer getContainer);



    @GET("/api/access/getAccess")
    Call<AjaxRs<Access>> getAccess(@Header("Authorization") String token);
}
