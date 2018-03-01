package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalc.integration.utils.Generator;
import ru.strcss.projects.moneycalc.integration.utils.Utils;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.testng.Assert.*;

@Slf4j
public class TransactionsControllerIT extends AbstractControllerIT {

    /**
     * Checks for correct Transactions returning for specific range
     */

    @Test
    public void getTransaction_RangeChecks() {
        String login = Utils.savePersonGetLogin(service);

        //Adding new Transactions
        AjaxRs<Transaction> responseAddTransaction1 = Utils.sendRequest(service.addTransaction(new TransactionAddContainer(login, Generator.generateTransaction()))).body();
        AjaxRs<Transaction> responseAddTransaction2 = Utils.sendRequest(service.addTransaction(
                new TransactionAddContainer(login, Generator.generateTransaction(GenerationUtils.generateDatePlus(ChronoUnit.DAYS, 1))))).body();
        assertEquals(responseAddTransaction1.getStatus(), Status.SUCCESS, responseAddTransaction1.getMessage());
        assertEquals(responseAddTransaction2.getStatus(), Status.SUCCESS, responseAddTransaction2.getMessage());

        //Requesting Transactions from today to tomorrow
        TransactionsSearchContainer containerToday2Tomorrow = new TransactionsSearchContainer(login, ControllerUtils.formatDateToString(GenerationUtils.currentDate()),
                ControllerUtils.formatDateToString(GenerationUtils.generateDatePlus(ChronoUnit.DAYS, 1)), Collections.emptyList());
        AjaxRs<List<Transaction>> responseToday2Tomorrow = Utils.sendRequest(service.getTransactions(containerToday2Tomorrow)).body();

        assertEquals(responseToday2Tomorrow.getStatus(), Status.SUCCESS, responseToday2Tomorrow.getMessage());
        assertEquals(responseToday2Tomorrow.getPayload().size(), 2, "Incorrect count of transactions is returned");

        //Requesting Transactions from yesterday to tomorrow
        TransactionsSearchContainer containerYesterday2Today = new TransactionsSearchContainer(login, ControllerUtils.formatDateToString(GenerationUtils.generateDateMinus(ChronoUnit.DAYS, 1)),
                ControllerUtils.formatDateToString(GenerationUtils.currentDate()), Collections.emptyList());
        AjaxRs<List<Transaction>> responseYesterday2Today = Utils.sendRequest(service.getTransactions(containerYesterday2Today)).body();

        assertEquals(responseYesterday2Today.getStatus(), Status.SUCCESS, responseYesterday2Today.getMessage());
        assertEquals(responseYesterday2Today.getPayload().size(), 1, "Incorrect count of transactions is returned");

        //Requesting Transactions from yesterday to tomorrow
        TransactionsSearchContainer containerTomorrowAndLater = new TransactionsSearchContainer(login, ControllerUtils.formatDateToString(GenerationUtils.generateDatePlus(ChronoUnit.DAYS, 1)),
                ControllerUtils.formatDateToString(GenerationUtils.generateDatePlus(ChronoUnit.DAYS, 2)), Collections.emptyList());
        AjaxRs<List<Transaction>> responseTomorrowAndLater = Utils.sendRequest(service.getTransactions(containerTomorrowAndLater)).body();

        assertEquals(responseTomorrowAndLater.getStatus(), Status.SUCCESS, responseTomorrowAndLater.getMessage());
        assertEquals(responseTomorrowAndLater.getPayload().size(), 1, "Incorrect count of transactions is returned");
    }

    /**
     * Checks for correct Transactions returning for requested sections
     */
    @Test
    public void getTransaction_SectionFilter() {
        int numOfAddedTransactionsPerSection = 5;
        int numOfSections = 3;

        String login = Utils.savePersonGetLogin(service);

        //Adding new Transactions
        List<Transaction> addedTransactions = new ArrayList<>();
        for (int i = 0; i < numOfSections; i++) {
            // FIXME: 11.02.2018 I suppose it could be done better
            int sectionID = i;
            addedTransactions.addAll(IntStream.range(0, numOfAddedTransactionsPerSection)
                    .mapToObj(s -> Utils.sendRequest(service.addTransaction(new TransactionAddContainer(login, Generator.generateTransaction(sectionID)))).body())
                    .filter(Objects::nonNull)
                    .map(AjaxRs::getPayload)
                    .collect(Collectors.toList()));
        }
        assertEquals(addedTransactions.size(), numOfAddedTransactionsPerSection * numOfSections, "Some Transactions were not saved!");

        //Requesting Transactions with Single Section
        for (int sectionID = 0; sectionID < numOfSections; sectionID++) {
            int finalSectionID = sectionID;
            // FIXME: 11.02.2018 I suppose it could be done better
            TransactionsSearchContainer containerSection1 = new TransactionsSearchContainer(login, ControllerUtils.formatDateToString(GenerationUtils.currentDate()),
                    ControllerUtils.formatDateToString(GenerationUtils.generateDatePlus(ChronoUnit.DAYS, 1)), Collections.singletonList(sectionID));
            AjaxRs<List<Transaction>> responseSingleSection = Utils.sendRequest(service.getTransactions(containerSection1)).body();

            assertEquals(responseSingleSection.getStatus(), Status.SUCCESS, responseSingleSection.getMessage());
            assertEquals(responseSingleSection.getPayload().size(), numOfAddedTransactionsPerSection, "Incorrect count of transactions has returned");
            assertTrue(responseSingleSection.getPayload().stream().allMatch(t -> t.getSectionID() == finalSectionID), "Some of returned Transactions have wrong SectionID");
        }

        //Requesting Transactions with Multiple Sections
        if (numOfSections > 1) {
            TransactionsSearchContainer containerSection1 = new TransactionsSearchContainer(login, ControllerUtils.formatDateToString(GenerationUtils.currentDate()),
                    ControllerUtils.formatDateToString(GenerationUtils.generateDatePlus(ChronoUnit.DAYS, 1)), Arrays.asList(0, 1));
            AjaxRs<List<Transaction>> responseMultipleSections = Utils.sendRequest(service.getTransactions(containerSection1)).body();

            assertEquals(responseMultipleSections.getStatus(), Status.SUCCESS, responseMultipleSections.getMessage());
            assertEquals(responseMultipleSections.getPayload().size(), numOfAddedTransactionsPerSection * 2, "Incorrect count of transactions has returned");
        }
    }

    /**
     * Saving new Transaction to empty list
     */

    @Test
    public void saveNewTransaction() {
        String login = Utils.savePersonGetLogin(service);

//        Adding new Transaction
        AjaxRs<Transaction> responseAddTransaction = Utils.sendRequest(service.addTransaction(new TransactionAddContainer(login, Generator.generateTransaction()))).body();
        assertEquals(responseAddTransaction.getStatus(), Status.SUCCESS, responseAddTransaction.getMessage());

        //Checking that it is added
        TransactionsSearchContainer container = new TransactionsSearchContainer(login, ControllerUtils.formatDateToString(GenerationUtils.generateDateMinus(ChronoUnit.DAYS, 1)),
                ControllerUtils.formatDateToString(GenerationUtils.generateDatePlus(ChronoUnit.DAYS, 1)), Collections.emptyList());
        AjaxRs<List<Transaction>> responseGetTransactions = Utils.sendRequest(service.getTransactions(container)).body();

        assertEquals(responseGetTransactions.getStatus(), Status.SUCCESS, responseGetTransactions.getMessage());
        assertEquals(responseGetTransactions.getPayload().size(), 1, "Size of returned Transactions list is not 1!");
    }

    /**
     * Deleting Transaction
     */
    @Test
    public void deleteTransaction() {
        int numOfAddedTransactions = 5;
        String login = Utils.savePersonGetLogin(service);

        //Adding new Transactions
        List<Transaction> addedTransactions = IntStream.range(0, numOfAddedTransactions)
                .mapToObj(s -> Utils.sendRequest(service.addTransaction(new TransactionAddContainer(login, Generator.generateTransaction()))).body())
                .filter(Objects::nonNull)
                .map(AjaxRs::getPayload)
                .collect(Collectors.toList());

        assertEquals(addedTransactions.size(), numOfAddedTransactions, "Some Transactions were not created!");

        //Getting random Transactions to delete
        String idToDelete = addedTransactions.get(ThreadLocalRandom.current().nextInt(addedTransactions.size())).get_id();

        AjaxRs responseDeletedTransaction = Utils.sendRequest(service.deleteTransaction(new TransactionDeleteContainer(login, idToDelete))).body();
        assertEquals(responseDeletedTransaction.getStatus(), Status.SUCCESS, responseDeletedTransaction.getMessage());

        //Getting Transactions list
        AjaxRs<List<Transaction>> responseGetTransactions = Utils.sendRequest(service.getTransactions(new TransactionsSearchContainer(login, ControllerUtils.formatDateToString(GenerationUtils.currentDate()),
                ControllerUtils.formatDateToString(GenerationUtils.currentDate()), Collections.emptyList()))).body();
        assertEquals(responseGetTransactions.getStatus(), Status.SUCCESS, responseGetTransactions.getMessage());

        assertEquals(responseGetTransactions.getPayload().size(), numOfAddedTransactions - 1, "List size after delete has not decreased!");
        assertFalse(responseGetTransactions.getPayload().stream().anyMatch(transaction -> transaction.get_id().equals(idToDelete)), "Transaction was not deleted!");
    }

    /**
     * Updating Transaction
     */
    @Test
    public void updateTransaction() {
        int numOfAddedTransactions = 5;
        String login = Utils.savePersonGetLogin(service);

        //Adding new Transactions
        List<Transaction> addedTransactions = IntStream.range(0, numOfAddedTransactions)
                .mapToObj(s -> Utils.sendRequest(service.addTransaction(new TransactionAddContainer(login, Generator.generateTransaction()))).body())
                .filter(Objects::nonNull)
                .map(AjaxRs::getPayload)
                .collect(Collectors.toList());

        assertEquals(addedTransactions.size(), numOfAddedTransactions, "Some Transactions were not created!");

        //Getting random Transactions to update
        String idToUpdate = addedTransactions.get(ThreadLocalRandom.current().nextInt(addedTransactions.size())).get_id();

        //Update Transaction
        AjaxRs<Transaction> responseUpdatedTransaction
                = Utils.sendRequest(service.updateTransaction(new TransactionUpdateContainer(login, idToUpdate, Generator.generateTransaction()))).body();
        assertEquals(responseUpdatedTransaction.getStatus(), Status.SUCCESS, responseUpdatedTransaction.getMessage());

        //Getting Transactions list
        AjaxRs<List<Transaction>> responseGetTransactions = Utils.sendRequest(service.getTransactions(new TransactionsSearchContainer(login, ControllerUtils.formatDateToString(GenerationUtils.currentDate()),
                ControllerUtils.formatDateToString(GenerationUtils.currentDate()), Collections.emptyList()))).body();
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

    @Test
    public void saveNewTransaction_NonexistentPerson() {
        AjaxRs<Transaction> responseAddTransaction = Utils.sendRequest(service.addTransaction(new TransactionAddContainer(Generator.UUID(), Generator.generateTransaction()))).body();
        assertEquals(responseAddTransaction.getStatus(), Status.ERROR, responseAddTransaction.getMessage());
    }

    @Test
    public void getTransactions_NonexistentPerson() {
        TransactionsSearchContainer container = new TransactionsSearchContainer(Generator.UUID(), GenerationUtils.currentDateString(), GenerationUtils.currentDateString(), Collections.singletonList(0));
        AjaxRs<List<Transaction>> responseGetTransaction = Utils.sendRequest(service.getTransactions(container)).body();
        assertEquals(responseGetTransaction.getStatus(), Status.ERROR, responseGetTransaction.getMessage());
    }

    @Test
    public void updateTransaction_NonexistentPerson() {
        AjaxRs<Transaction> responseAddTransaction = Utils.sendRequest(service.updateTransaction(new TransactionUpdateContainer(Generator.UUID(), Generator.UUID(), Generator.generateTransaction()))).body();
        assertEquals(responseAddTransaction.getStatus(), Status.ERROR, responseAddTransaction.getMessage());
    }

    @Test
    public void deleteTransaction_NonexistentPerson() {
        AjaxRs<Void> responseAddTransaction = Utils.sendRequest(service.deleteTransaction(new TransactionDeleteContainer(Generator.UUID(), Generator.UUID()))).body();
        assertEquals(responseAddTransaction.getStatus(), Status.ERROR, responseAddTransaction.getMessage());
    }
}