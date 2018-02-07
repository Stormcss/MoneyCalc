package ru.strcss.projects.moneycalcserver.controllers.testapi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.strcss.projects.moneycalc.dto.*;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.Transaction;

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
     *  Transactions
     */
    @POST("/api/finance/financeStats/getTransactions")
    Call<AjaxRs<List<Transaction>>> getTransactions(@Body TransactionsSearchContainer container);

    @POST("/api/finance/financeStats/addTransaction")
    Call<AjaxRs<Transaction>> addTransaction(@Body TransactionContainer transactionContainer);

    @POST("/api/finance/financeStats/deleteTransaction")
    Call<AjaxRs<Void>> deleteTransaction(@Body TransactionDeleteContainer transactionContainer);

    @POST("/api/finance/financeStats/updateTransaction")
    Call<AjaxRs<Transaction>> updateTransaction(@Body TransactionUpdateContainer transactionUpdateContainer);

    @POST("/api/finance/financeStats/dropStatistics")
    Call<AjaxRs<List<Transaction>>> dropTransactions(@Body String login);
}
