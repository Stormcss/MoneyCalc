package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.dto.*;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.*;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.*;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

@Slf4j
public class TransactionsControllerTest extends AbstractControllerTest {

    /**
     * Checks for correct Transactions returning for specific range
     *
     * @throws IOException
     */

    @Test
    public void getTransaction_RangeChecks() throws IOException {
        String login = UUID();
        Person person = personGenerator(login);

        //Registering Person
        Response<AjaxRs<Person>> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
        assertEquals(responseCreatePerson.body().getStatus(), Status.SUCCESS, responseCreatePerson.body().getMessage());

        //Adding new Transactions
        Response<AjaxRs<Transaction>> responseAddTransaction1 = sendRequest(service.addTransaction(new TransactionContainer(generateTransaction(), login)));
        Response<AjaxRs<Transaction>> responseAddTransaction2 = sendRequest(service.addTransaction(
                new TransactionContainer(generateTransaction(generateDatePlus(ChronoUnit.DAYS, 1)), login)));
        assertEquals(responseAddTransaction1.body().getStatus(), Status.SUCCESS, responseAddTransaction1.body().getMessage());
        assertEquals(responseAddTransaction2.body().getStatus(), Status.SUCCESS, responseAddTransaction2.body().getMessage());

        //Requesting Transactions from today to tomorrow
        TransactionsSearchContainer containerToday2Tomorrow = new TransactionsSearchContainer(login, formatDateToString(currentDate()),
                formatDateToString(generateDatePlus(ChronoUnit.DAYS, 1)));
        Response<AjaxRs<List<Transaction>>> responseToday2Tomorrow = sendRequest(service.getTransactions(containerToday2Tomorrow));

        assertEquals(responseToday2Tomorrow.body().getStatus(), Status.SUCCESS, responseToday2Tomorrow.body().getMessage());
        assertEquals(responseToday2Tomorrow.body().getPayload().size(), 2, "Incorrect count of transactions is returned");

        //Requesting Transactions from yesterday to tomorrow
        TransactionsSearchContainer containerYesterday2Today = new TransactionsSearchContainer(login, formatDateToString(generateDateMinus(ChronoUnit.DAYS, 1)),
                formatDateToString(currentDate()));
        Response<AjaxRs<List<Transaction>>> responseYesterday2Today = sendRequest(service.getTransactions(containerYesterday2Today));

        assertEquals(responseYesterday2Today.body().getStatus(), Status.SUCCESS, responseYesterday2Today.body().getMessage());
        assertEquals(responseYesterday2Today.body().getPayload().size(), 1, "Incorrect count of transactions is returned");

        //Requesting Transactions from yesterday to tomorrow
        TransactionsSearchContainer containerTomorrowAndLater = new TransactionsSearchContainer(login, formatDateToString(generateDatePlus(ChronoUnit.DAYS, 1)),
                formatDateToString(generateDatePlus(ChronoUnit.DAYS, 2)));
        Response<AjaxRs<List<Transaction>>> responseTomorrowAndLater = sendRequest(service.getTransactions(containerTomorrowAndLater));

        assertEquals(responseTomorrowAndLater.body().getStatus(), Status.SUCCESS, responseTomorrowAndLater.body().getMessage());
        assertEquals(responseTomorrowAndLater.body().getPayload().size(), 1, "Incorrect count of transactions is returned");
    }

    /**
     * Saving new Transaction to empty list
     *
     * @throws IOException
     */

    @Test
    public void saveNewTransaction() throws IOException {
        String login = UUID();

        Person person = personGenerator(login);

        //Registering Person
        Response<AjaxRs<Person>> responseCreatePerson = sendRequest(service.registerPerson(new Credentials(person.getAccess(), person.getIdentifications())));
        assertEquals(responseCreatePerson.body().getStatus(), Status.SUCCESS, responseCreatePerson.body().getMessage());


        Transaction transaction = generateTransaction();

//        Adding new Transaction
        Response<AjaxRs<Transaction>> responseAddTransaction = sendRequest(service.addTransaction(new TransactionContainer(transaction, login)));
        assertEquals(responseAddTransaction.body().getStatus(), Status.SUCCESS, responseAddTransaction.body().getMessage());

        //Checking that it is added
        TransactionsSearchContainer container = new TransactionsSearchContainer(login, formatDateToString(generateDateMinus(ChronoUnit.DAYS, 1)),
                formatDateToString(generateDatePlus(ChronoUnit.DAYS, 1)));
        Response<AjaxRs<List<Transaction>>> responseGetTransactions = sendRequest(service.getTransactions(container));

        assertEquals(responseGetTransactions.body().getStatus(), Status.SUCCESS, responseGetTransactions.body().getMessage());
        assertEquals(responseGetTransactions.body().getPayload().size(), 1, "Size of returned Transactions list is not 1!");

        //TODO: 26.01.2018 Add checks for Summary Statistics!!!
    }

    /**
     * Saving new Transaction to empty list
     *
     * @throws IOException
     */
    @Test
    public void updateTransaction() throws IOException {

    }
}