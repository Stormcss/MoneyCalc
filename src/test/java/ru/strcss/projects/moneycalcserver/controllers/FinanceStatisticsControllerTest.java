package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.strcss.projects.moneycalcserver.controllers.api.MoneyCalcClient;
import ru.strcss.projects.moneycalcserver.controllers.utils.Generator;
import ru.strcss.projects.moneycalcserver.enitities.dto.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.personGenerator;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

@Slf4j
public class FinanceStatisticsControllerTest {
    private MoneyCalcClient service;

    @BeforeClass
    public void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MoneyCalcClient.class);
    }

    @Test
    public void getTransactions() throws IOException {
        Person person = personGenerator();

        //Registering Person
        Response<AjaxRs<Person>> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
        assertEquals(responseCreatePerson.body().getStatus(), Status.SUCCESS, responseCreatePerson.body().getMessage());

        //Requesting Transactions
        Response<AjaxRs<List<Transaction>>> responseGetTransactions = sendRequest(service.getTransactions(person.getAccess().getLogin()));

        List<Transaction> personTransactions = person.getFinance().getFinanceStatistics().getTransactions();
        List<Transaction> requestedTransactions = responseGetTransactions.body().getPayload();

        assertEquals(responseGetTransactions.body().getStatus(), Status.SUCCESS, responseGetTransactions.body().getMessage());
        assertEquals(requestedTransactions.size(), personTransactions.size(),
                "Transactions list size in generated Person is not equal to returned list of Transactions");

        if (personTransactions.size() > 0) {
            for (int i = 0; i < personTransactions.size(); i++) {
                int randomIndex = ThreadLocalRandom.current().nextInt(personTransactions.size() - 1);
                assertEquals(requestedTransactions.get(randomIndex).getSum(), personTransactions.get(randomIndex).getSum(),
                        "Transactions in generated Person and returned are not equal!");
            }
        }
    }

    @Test
    public void addTransaction() throws IOException {
        Person person = personGenerator();

        //Registering Person
        Response<AjaxRs<Person>> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
        assertEquals(responseCreatePerson.body().getStatus(), Status.SUCCESS, responseCreatePerson.body().getMessage());

        Transaction transaction = Generator.generateTransaction();

        //Adding transaction
        Response<AjaxRs<List<Transaction>>> responseAddTransaction = sendRequest(service.addTransaction(transaction));
        assertEquals(responseAddTransaction.body().getStatus(), Status.SUCCESS, responseAddTransaction.body().getMessage());

        assertEquals(responseAddTransaction.body().getPayload().get(responseAddTransaction.body().getPayload().size() - 1).get_id(), transaction.get_id(), "Transactions are not equal!");
    }

    @Test
    public void updateTransaction() throws IOException {
        Person person = personGenerator();

        //Registering Person
        Response<AjaxRs<Person>> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
        assertEquals(responseCreatePerson.body().getStatus(), Status.SUCCESS, responseCreatePerson.body().getMessage());

        Transaction transaction = Generator.generateTransaction();

        // Adding transaction
        Response<AjaxRs<List<Transaction>>> responseAddTransaction = sendRequest(service.addTransaction(transaction));
        assertEquals(responseAddTransaction.body().getStatus(), Status.SUCCESS, responseAddTransaction.body().getMessage());

        Transaction newTransaction = Transaction.builder()
                .currency(transaction.getCurrency())
                .description(transaction.getDescription())
                .sum(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE))
                .build();

        //Updating transaction
        Response<AjaxRs<List<Transaction>>> responseUpdateTransaction = sendRequest(service.updateTransaction(newTransaction));
        assertEquals(responseUpdateTransaction.body().getStatus(), Status.SUCCESS, responseUpdateTransaction.body().getMessage());
        assertNotEquals(responseUpdateTransaction.body().getPayload().get(responseUpdateTransaction.body().getPayload().size() - 1).getSum(), transaction.getSum(), "Transaction wasn't updated!");

        // TODO: 26.01.2018 Add checks for Summary Statistics!!!
    }

        // TODO: 26.01.2018 Add TEST for deleteTransaction

}