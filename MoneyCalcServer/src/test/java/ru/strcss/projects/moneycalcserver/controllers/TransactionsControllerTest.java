package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.testng.Assert.*;
import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.*;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.generateTransaction;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.savePersonGetLogin;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

@Slf4j
public class TransactionsControllerTest extends AbstractControllerTest {

    /**
     * Checks for correct Transactions returning for specific range
     */

    @Test
    public void getTransaction_RangeChecks() {
        String login = savePersonGetLogin(service);

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
    public void saveNewTransaction() {
        String login = savePersonGetLogin(service);

//        Adding new Transaction
        AjaxRs<Transaction> responseAddTransaction = sendRequest(service.addTransaction(new TransactionContainer(generateTransaction(), login))).body();
        assertEquals(responseAddTransaction.getStatus(), Status.SUCCESS, responseAddTransaction.getMessage());

        //Checking that it is added
        TransactionsSearchContainer container = new TransactionsSearchContainer(login, formatDateToString(generateDateMinus(ChronoUnit.DAYS, 1)),
                formatDateToString(generateDatePlus(ChronoUnit.DAYS, 1)));
        AjaxRs<List<Transaction>> responseGetTransactions = sendRequest(service.getTransactions(container)).body();

        assertEquals(responseGetTransactions.getStatus(), Status.SUCCESS, responseGetTransactions.getMessage());
        assertEquals(responseGetTransactions.getPayload().size(), 1, "Size of returned Transactions list is not 1!");

        //TODO: 26.01.2018 Add checks for Summary Statistics!!!
    }

    /**
     * Deleting Transaction
     */
    @Test
    public void deleteTransaction() {
        int numOfAddedTransactions = 5;
        String login = savePersonGetLogin(service);

        //Adding new Transactions
        List<Transaction> addedTransactions = IntStream.range(0, numOfAddedTransactions)
                .mapToObj(s -> sendRequest(service.addTransaction(new TransactionContainer(generateTransaction(), login))).body())
                .filter(Objects::nonNull)
                .map(AjaxRs::getPayload)
                .collect(Collectors.toList());

        assertEquals(addedTransactions.size(), numOfAddedTransactions, "Some Transactions were not created!");

        //Getting random Transactions to delete
        String idToDelete = addedTransactions.get(ThreadLocalRandom.current().nextInt(addedTransactions.size())).get_id();

        //Deleting Transaction
//        TransactionDeleteContainer container = ;

        AjaxRs responseDeletedTransaction
                = sendRequest(service.deleteTransaction(new TransactionDeleteContainer(login, idToDelete))).body();
        assertEquals(responseDeletedTransaction.getStatus(), Status.SUCCESS, responseDeletedTransaction.getMessage());

        //Getting Transactions list
        AjaxRs<List<Transaction>> responseGetTransactions = sendRequest(service.getTransactions(new TransactionsSearchContainer(login, formatDateToString(currentDate()),
                formatDateToString(currentDate())))).body();
        assertEquals(responseGetTransactions.getStatus(), Status.SUCCESS, responseGetTransactions.getMessage());

        assertEquals(responseGetTransactions.getPayload().size(), numOfAddedTransactions - 1, "List size after delete has not decreased!");
        assertFalse(responseGetTransactions.getPayload().stream().anyMatch(transaction -> transaction.get_id().equals(idToDelete)), "Transaction was not deleted!");
    }

    @Test
    public void updateTransaction() {
        int numOfAddedTransactions = 5;
        String login = savePersonGetLogin(service);

        //Adding new Transactions
        List<Transaction> addedTransactions = IntStream.range(0, numOfAddedTransactions)
                .mapToObj(s -> sendRequest(service.addTransaction(new TransactionContainer(generateTransaction(), login))).body())
                .filter(Objects::nonNull)
                .map(AjaxRs::getPayload)
                .collect(Collectors.toList());

        assertEquals(addedTransactions.size(), numOfAddedTransactions, "Some Transactions were not created!");

        //Getting random Transactions to update
        String idToUpdate = addedTransactions.get(ThreadLocalRandom.current().nextInt(addedTransactions.size())).get_id();

        //Update Transaction
        AjaxRs<Transaction> responseUpdatedTransaction
                = sendRequest(service.updateTransaction(new TransactionUpdateContainer(login, idToUpdate, generateTransaction()))).body();
        assertEquals(responseUpdatedTransaction.getStatus(), Status.SUCCESS, responseUpdatedTransaction.getMessage());

        //Getting Transactions list
        AjaxRs<List<Transaction>> responseGetTransactions = sendRequest(service.getTransactions(new TransactionsSearchContainer(login, formatDateToString(currentDate()),
                formatDateToString(currentDate())))).body();
        assertEquals(responseGetTransactions.getStatus(), Status.SUCCESS, responseGetTransactions.getMessage());

        List<Transaction> transactionsList = responseGetTransactions.getPayload();

        assertTrue(transactionsList.stream()
                .map(Transaction::get_id)
                .anyMatch(id -> id.equals(idToUpdate)), "Id of updated Transaction has changed!");
        assertEquals(transactionsList.size(), numOfAddedTransactions, "Size of Transactions list has changed!");


        Transaction beforeUpdatedTransaction = addedTransactions.stream()
                .filter(transaction -> transaction.get_id().equals(idToUpdate))
                .findFirst()
                .get();
        Transaction updatedTransaction = transactionsList.stream()
                .filter(transaction -> transaction.get_id().equals(idToUpdate))
                .findFirst()
                .get();

        assertNotEquals(updatedTransaction.getSum(), beforeUpdatedTransaction.getSum(), "Sum in Transaction after update has not changed!");
        assertEquals(updatedTransaction.get_id(), beforeUpdatedTransaction.get_id(), "Sum in Transaction after update has not changed!");
    }
}