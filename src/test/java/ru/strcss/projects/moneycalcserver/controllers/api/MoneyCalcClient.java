package ru.strcss.projects.moneycalcserver.controllers.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.strcss.projects.moneycalcserver.controllers.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.controllers.dto.Credentials;
import ru.strcss.projects.moneycalcserver.controllers.dto.TransactionContainer;
import ru.strcss.projects.moneycalcserver.controllers.dto.TransactionsSearchContainer;
import ru.strcss.projects.moneycalcserver.enitities.Identifications;
import ru.strcss.projects.moneycalcserver.enitities.Person;
import ru.strcss.projects.moneycalcserver.enitities.Settings;
import ru.strcss.projects.moneycalcserver.enitities.Transaction;

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

    /**
     *  Identifications
     */
    @POST("/api/identifications/saveIdentifications")
    Call<AjaxRs<Identifications>> saveIdentifications(@Body Identifications settings);

    @POST("/api/identifications/getIdentifications")
    Call<AjaxRs<Identifications>> getIdentifications(@Body String login);

    /**
     *  FinanceStatistics
     */
    @POST("/api/finance/financeStats/getTransactions")
    Call<AjaxRs<List<Transaction>>> getTransactions(@Body TransactionsSearchContainer container);

    @POST("/api/finance/financeStats/addTransaction")
    Call<AjaxRs<Transaction>> addTransaction(@Body TransactionContainer transactionContainer);

    @POST("/api/finance/financeStats/deleteTransaction")
    Call<AjaxRs<List<Transaction>>> deleteTransaction(@Body Transaction transaction);

    @POST("/api/finance/financeStats/updateTransaction")
    Call<AjaxRs<List<Transaction>>> updateTransaction(@Body Transaction transaction);

    @POST("/api/finance/financeStats/dropStatistics")
    Call<AjaxRs<List<Transaction>>> dropTransactions(@Body String login);
}
