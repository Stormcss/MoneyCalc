package ru.strcss.projects.moneycalcmigrator.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.crudcontainers.LoginGetContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.util.List;

public interface MigrationAPI {
    /**
     *  Settings
     */
    @POST("/api/settings/addSpendingSection")
    Call<AjaxRs<List<SpendingSection>>> addSpendingSection(@Body SpendingSectionAddContainer spendingSectionContainer);

    @POST("/api/settings/getSpendingSections")
    Call<AjaxRs<List<SpendingSection>>> getSpendingSections(@Body LoginGetContainer getContainer);


    @POST("/api/registration/registerPerson")
    Call<AjaxRs<Person>> registerPerson(@Body Credentials credentials);

    @POST("/api/finance/transactions/addTransaction")
    Call<AjaxRs<Transaction>> addTransaction(@Body TransactionAddContainer transactionContainer);

    @POST("/api/finance/transactions/deleteTransaction")
    Call<AjaxRs<Void>> deleteTransaction(@Body TransactionDeleteContainer transactionContainer);
}
