package ru.strcss.projects.moneycalcserver.controllers.testapi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
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

    @POST("/api/registration/registerPerson")
    Call<AjaxRs<Person>> registerPerson(@Body Credentials credentials);


    /**
     *  Settings
     */
    @POST("/api/settings/saveSettings")
    Call<AjaxRs<Settings>> saveSettings(@Body Settings settings);

    @POST("/api/settings/getSettings")
    Call<AjaxRs<Settings>> getSettings(@Body String login);

    @POST("/api/settings/addSpendingSection")
    Call<AjaxRs<List<SpendingSection>>> addSpendingSection(@Body SpendingSectionAddContainer spendingSectionContainer);

    @POST("/api/settings/updateSpendingSection")
    Call<AjaxRs<List<SpendingSection>>> updateSpendingSection(@Body SpendingSectionUpdateContainer updateContainer);

    @POST("/api/settings/deleteSpendingSection")
    Call<AjaxRs<List<SpendingSection>>> deleteSpendingSection(@Body SpendingSectionDeleteContainer deleteContainer);

    /**
     *  Identifications
     */
    @POST("/api/identifications/saveIdentifications")
    Call<AjaxRs<Identifications>> saveIdentifications(@Body Identifications settings);

    @POST("/api/identifications/getIdentifications")
    Call<AjaxRs<Identifications>> getIdentifications(@Body String login);

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
