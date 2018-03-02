package ru.strcss.projects.moneycalc.integration.testapi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.crudcontainers.LoginGetContainer;
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


    /**
     *  Settings
     */
    @POST("/api/settings/saveSettings")
    Call<AjaxRs<Settings>> saveSettings(@Body SettingsUpdateContainer updateContainer);

    @POST("/api/settings/getSettings")
    Call<AjaxRs<Settings>> getSettings(@Body LoginGetContainer getContainer);

    @POST("/api/settings/addSpendingSection")
    Call<AjaxRs<List<SpendingSection>>> addSpendingSection(@Body SpendingSectionAddContainer spendingSectionContainer);

    @POST("/api/settings/updateSpendingSection")
    Call<AjaxRs<List<SpendingSection>>> updateSpendingSection(@Body SpendingSectionUpdateContainer updateContainer);

    @POST("/api/settings/deleteSpendingSection")
    Call<AjaxRs<List<SpendingSection>>> deleteSpendingSection(@Body SpendingSectionDeleteContainer deleteContainer);

    @POST("/api/settings/getSpendingSections")
    Call<AjaxRs<List<SpendingSection>>> getSpendingSections(@Body LoginGetContainer getContainer);

    /**
     *  Identifications
     */
    @POST("/api/identifications/saveIdentifications")
    Call<AjaxRs<Identifications>> saveIdentifications(@Body IdentificationsUpdateContainer updateContainer);

    @POST("/api/identifications/getIdentifications")
    Call<AjaxRs<Identifications>> getIdentifications(@Body LoginGetContainer getContainer);

    /**
     *  Transactions
     */
    @POST("/api/finance/transactions/getTransactions")
    Call<AjaxRs<List<Transaction>>> getTransactions(@Body TransactionsSearchContainer container);

    @POST("/api/finance/transactions/addTransaction")
    Call<AjaxRs<Transaction>> addTransaction(@Body TransactionAddContainer transactionContainer);

    @POST("/api/finance/transactions/deleteTransaction")
    Call<AjaxRs<Void>> deleteTransaction(@Body TransactionDeleteContainer transactionContainer);

    @POST("/api/finance/transactions/updateTransaction")
    Call<AjaxRs<Transaction>> updateTransaction(@Body TransactionUpdateContainer transactionUpdateContainer);

    @POST("/api/finance/transactions/dropStatistics")
    Call<AjaxRs<List<Transaction>>> dropTransactions(@Body String login);

    /**
     *  Statistics
     */
    @POST("/api/statistics/financeSummary/getFinanceSummaryBySection")
    Call<AjaxRs<List<FinanceSummaryBySection>>> getFinanceSummaryBySection(@Body FinanceSummaryGetContainer getContainer);
}
