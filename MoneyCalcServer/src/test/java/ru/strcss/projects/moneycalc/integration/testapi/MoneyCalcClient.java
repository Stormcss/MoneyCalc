package ru.strcss.projects.moneycalc.integration.testapi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Credentials;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.ItemsContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.StatisticsFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Person;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumByDate;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumByDateSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumBySection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SummaryBySection;

public interface MoneyCalcClient {

    @POST("/api/registration/register")
    Call<Person> registerPerson(@Body Credentials credentials);

    @POST("/login")
    Call<Void> login(@Body Access access);

    /**
     * Settings
     */
    @PUT("/api/settings")
    Call<Settings> updateSettings(@Header("Authorization") String token,
                                  @Body Settings settings);

    @GET("/api/settings")
    Call<Settings> getSettings(@Header("Authorization") String token);

    /**
     * Spending Section
     */
    @POST("/api/spendingSections")
    Call<SpendingSectionsSearchRs> addSpendingSection(@Header("Authorization") String token,
                                                      @Body SpendingSection spendingSection);

    @PUT("/api/spendingSections")
    Call<SpendingSectionsSearchRs> updateSpendingSection(@Header("Authorization") String token,
                                                         @Body SpendingSectionUpdateContainer updateContainer);

    @DELETE("/api/spendingSections/{sectionId}")
    Call<SpendingSectionsSearchRs> deleteSpendingSection(@Header("Authorization") String token,
                                                         @Path("sectionId") Integer sectionId);

    @GET("/api/spendingSections")
    Call<SpendingSectionsSearchRs> getSpendingSections(@Header("Authorization") String token);

    @GET("/api/spendingSections?withNonAdded=true")
    Call<SpendingSectionsSearchRs> getSpendingSectionsWithNonAdded(@Header("Authorization") String token);

    @GET("/api/spendingSections?withRemoved=true")
    Call<SpendingSectionsSearchRs> getSpendingSectionsWithRemoved(@Header("Authorization") String token);

    @GET("/api/spendingSections?withRemovedOnly=true")
    Call<SpendingSectionsSearchRs> getSpendingSectionsWithRemovedOnly(@Header("Authorization") String token);

    /**
     * Identifications
     */
    @PUT("/api/identifications")
    Call<Identifications> saveIdentifications(@Header("Authorization") String token,
                                              @Body Identifications identifications);

    @GET("/api/identifications")
    Call<Identifications> getIdentifications(@Header("Authorization") String token);

    /**
     * Transactions
     */
    @GET("/api/transactions")
    Call<TransactionsSearchRs> getTransactions(@Header("Authorization") String token);

    @POST("/api/transactions/getFiltered")
    Call<TransactionsSearchRs> getTransactions(@Header("Authorization") String token,
                                               @Body TransactionsSearchFilter container);

    @POST("/api/transactions")
    Call<Transaction> addTransaction(@Header("Authorization") String token,
                                     @Body Transaction transaction);

    @DELETE("/api/transactions/{transactionId}")
    Call<Void> deleteTransaction(@Header("Authorization") String token,
                                 @Path("transactionId") Long transactionId);

    @PUT("/api/transactions")
    Call<Transaction> updateTransaction(@Header("Authorization") String token,
                                        @Body TransactionUpdateContainer transactionUpdateContainer);

    /**
     * Statistics
     */
    @GET("/api/stats/bySection/summary")
    Call<ItemsContainer<SummaryBySection>> getSummaryBySection(@Header("Authorization") String token);

    @POST("/api/stats/bySection/sum")
    Call<ItemsContainer<SumBySection>> getSumBySection(@Header("Authorization") String token,
                                                       @Body StatisticsFilter statisticsFilter);

    @POST("/api/stats/byDate/sum")
    Call<ItemsContainer<SumByDate>> getSumByDate(@Header("Authorization") String token,
                                                 @Body StatisticsFilter getContainer);

    @POST("/api/stats/byDate/sumBySection")
    Call<ItemsContainer<SumByDateSection>> getSumByDateSection(@Header("Authorization") String token,
                                                               @Body StatisticsFilter getContainer);

    /**
     * Access
     */
    @GET("/api/access")
    Call<Access> getAccess(@Header("Authorization") String token);
}
