package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.testng.Assert.*;
import static ru.strcss.projects.moneycalc.integration.utils.Utils.savePersonGetToken;
import static ru.strcss.projects.moneycalc.integration.utils.Utils.sendRequest;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.formatDateToString;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.*;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransaction;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.assertTransactionsOrderedByDate;

@Slf4j
public class TransactionsControllerIT extends AbstractIT {

    /**
     * Checks for correct Transactions returning for specific range
     */

    @Test
    public void getTransaction_RangeChecks() {
        String token = savePersonGetToken(service);

        //Adding new Transactions
        MoneyCalcRs<Transaction> addTransaction1Rs = sendRequest(service.addTransaction(token, new TransactionAddContainer(generateTransaction()))).body();
        MoneyCalcRs<Transaction> addTransaction2Rs = sendRequest(service.addTransaction(token,
                new TransactionAddContainer(generateTransaction(generateDatePlus(ChronoUnit.DAYS, 1))))).body();
        assertEquals(addTransaction1Rs.getServerStatus(), Status.SUCCESS, addTransaction1Rs.getMessage());
        assertEquals(addTransaction2Rs.getServerStatus(), Status.SUCCESS, addTransaction2Rs.getMessage());

        //Requesting Transactions from today to tomorrow
        TransactionsSearchContainer containerToday2Tomorrow = new TransactionsSearchContainer(formatDateToString(currentDate()),
                formatDateToString(generateDatePlus(ChronoUnit.DAYS, 1)), Collections.emptyList());
        MoneyCalcRs<List<Transaction>> today2TomorrowRs = sendRequest(service.getTransactions(token, containerToday2Tomorrow)).body();

        assertEquals(today2TomorrowRs.getServerStatus(), Status.SUCCESS, today2TomorrowRs.getMessage());
        assertEquals(today2TomorrowRs.getPayload().size(), 2, "Incorrect count of transactions is returned");

        //Requesting Transactions from yesterday to tomorrow
        TransactionsSearchContainer containerYesterday2Today = new TransactionsSearchContainer(formatDateToString(generateDateMinus(ChronoUnit.DAYS, 1)),
                formatDateToString(currentDate()), Collections.emptyList());
        MoneyCalcRs<List<Transaction>> yesterday2TodayRs = sendRequest(service.getTransactions(token, containerYesterday2Today)).body();

        assertEquals(yesterday2TodayRs.getServerStatus(), Status.SUCCESS, yesterday2TodayRs.getMessage());
        assertEquals(yesterday2TodayRs.getPayload().size(), 1, "Incorrect count of transactions is returned");

        //Requesting Transactions from yesterday to tomorrow
        TransactionsSearchContainer containerTomorrowAndLater = new TransactionsSearchContainer(formatDateToString(generateDatePlus(ChronoUnit.DAYS, 1)),
                formatDateToString(generateDatePlus(ChronoUnit.DAYS, 2)), Collections.emptyList());
        MoneyCalcRs<List<Transaction>> tomorrowAndLaterRs = sendRequest(service.getTransactions(token, containerTomorrowAndLater)).body();

        assertEquals(tomorrowAndLaterRs.getServerStatus(), Status.SUCCESS, tomorrowAndLaterRs.getMessage());
        assertEquals(tomorrowAndLaterRs.getPayload().size(), 1, "Incorrect count of transactions is returned");
    }

    /**
     * Checks for correct Transactions returning for requested sections
     */
    @Test
    public void getTransaction_SectionFilter() {
        int numOfAddedTransactionsPerSection = 5;
        int numOfSections = 2; //currently Person by default has only 2 sections

        String token = savePersonGetToken(service);

        //Adding new Transactions
        List<Transaction> addedTransactions = new ArrayList<>();
        for (int i = 0; i < numOfSections; i++) {
            // FIXME: 11.02.2018 I suppose it could be done better
            int sectionID = i;
            addedTransactions.addAll(IntStream.range(0, numOfAddedTransactionsPerSection)
                    .mapToObj(s -> sendRequest(service.addTransaction(token, new TransactionAddContainer(generateTransaction(sectionID)))).body())
                    .filter(Objects::nonNull)
                    .map(MoneyCalcRs::getPayload)
                    .collect(Collectors.toList()));
        }
        assertEquals(addedTransactions.size(), numOfAddedTransactionsPerSection * numOfSections, "Some Transactions were not saved!");

        //Requesting Transactions with Single Section
        for (int sectionID = 0; sectionID < numOfSections; sectionID++) {
            int finalSectionID = sectionID;
            // FIXME: 11.02.2018 I suppose it could be done better
            TransactionsSearchContainer containerSection1 = new TransactionsSearchContainer(formatDateToString(currentDate()),
                    formatDateToString(generateDatePlus(ChronoUnit.DAYS, 1)), Collections.singletonList(sectionID));
            MoneyCalcRs<List<Transaction>> singleSectionRs = sendRequest(service.getTransactions(token, containerSection1)).body();

            assertEquals(singleSectionRs.getServerStatus(), Status.SUCCESS, singleSectionRs.getMessage());
            assertEquals(singleSectionRs.getPayload().size(), numOfAddedTransactionsPerSection, "Incorrect count of transactions has returned");
            assertTrue(singleSectionRs.getPayload().stream().allMatch(t -> t.getSectionID() == finalSectionID), "Some of returned Transactions have wrong SectionID");
        }

        //Requesting Transactions with Multiple Sections
        if (numOfSections > 1) {
            TransactionsSearchContainer containerSection1 = new TransactionsSearchContainer(formatDateToString(currentDate()),
                    formatDateToString(generateDatePlus(ChronoUnit.DAYS, 1)), Arrays.asList(0, 1));
            MoneyCalcRs<List<Transaction>> multipleSectionsRs = sendRequest(service.getTransactions(token, containerSection1)).body();

            assertEquals(multipleSectionsRs.getServerStatus(), Status.SUCCESS, multipleSectionsRs.getMessage());
            assertEquals(multipleSectionsRs.getPayload().size(), numOfAddedTransactionsPerSection * 2, "Incorrect count of transactions has returned");
        }
    }

    /**
     * Saving new Transaction to empty list
     */
    @Test
    public void saveNewTransaction() {
        String token = savePersonGetToken(service);

//        Adding new Transaction
        MoneyCalcRs<Transaction> addTransactionRs = sendRequest(service.addTransaction(token, new TransactionAddContainer(generateTransaction()))).body();
        assertEquals(addTransactionRs.getServerStatus(), Status.SUCCESS, addTransactionRs.getMessage());

        //Checking that it is added
        TransactionsSearchContainer container = new TransactionsSearchContainer(formatDateToString(generateDateMinus(ChronoUnit.DAYS, 1)),
                formatDateToString(generateDatePlus(ChronoUnit.DAYS, 1)), Collections.emptyList());
        MoneyCalcRs<List<Transaction>> getTransactionsRs = sendRequest(service.getTransactions(token, container)).body();

        assertEquals(getTransactionsRs.getServerStatus(), Status.SUCCESS, getTransactionsRs.getMessage());
        assertEquals(getTransactionsRs.getPayload().size(), 1, "Size of returned Transactions list is not 1!");
    }

    /**
     * Saving new Transaction with nonexistent sectionID
     */
    @Test
    public void saveNewTransaction_nonExistentSectionID() {
        String token = savePersonGetToken(service);

        Response<MoneyCalcRs<Transaction>> addTransactionRs = sendRequest(service.addTransaction(token, new TransactionAddContainer(generateTransaction(10))));

        assertFalse(addTransactionRs.isSuccessful(), "Response is not failed!");
    }

    /**
     * Deleting Transaction
     */
    @Test
    public void deleteTransaction() {
        int numOfAddedTransactions = 5;
        String token = savePersonGetToken(service);

        //Adding new Transactions
        List<Transaction> addedTransactions = IntStream.range(0, numOfAddedTransactions)
                .mapToObj(s -> sendRequest(service.addTransaction(token, new TransactionAddContainer(generateTransaction()))).body())
                .filter(Objects::nonNull)
                .map(MoneyCalcRs::getPayload)
                .collect(Collectors.toList());

        assertEquals(addedTransactions.size(), numOfAddedTransactions, "Some Transactions were not created!");

        //Getting random Transactions to delete
        String idToDelete = addedTransactions.get(ThreadLocalRandom.current().nextInt(addedTransactions.size())).get_id();

        MoneyCalcRs deleteTransactionRs = sendRequest(service.deleteTransaction(token, new TransactionDeleteContainer(idToDelete))).body();
        assertEquals(deleteTransactionRs.getServerStatus(), Status.SUCCESS, deleteTransactionRs.getMessage());

        //Getting Transactions list
        MoneyCalcRs<List<Transaction>> getTransactionsRs = sendRequest(service.getTransactions(token, new TransactionsSearchContainer(formatDateToString(currentDate()),
                formatDateToString(currentDate()), Collections.emptyList()))).body();
        assertEquals(getTransactionsRs.getServerStatus(), Status.SUCCESS, getTransactionsRs.getMessage());

        assertEquals(getTransactionsRs.getPayload().size(), numOfAddedTransactions - 1, "List size after delete has not decreased!");
        assertFalse(getTransactionsRs.getPayload().stream().anyMatch(transaction -> transaction.get_id().equals(idToDelete)), "Transaction was not deleted!");
    }

    /**
     * Updating Transaction
     */
    @Test
    public void updateTransaction() {
        int numOfAddedTransactions = 5;
        String token = savePersonGetToken(service);

        //Adding new Transactions
        List<Transaction> addedTransactions = IntStream.range(0, numOfAddedTransactions)
                .mapToObj(s -> sendRequest(service.addTransaction(token, new TransactionAddContainer(generateTransaction()))).body())
                .filter(Objects::nonNull)
                .map(MoneyCalcRs::getPayload)
                .collect(Collectors.toList());

        assertEquals(addedTransactions.size(), numOfAddedTransactions, "Some Transactions were not created!");

        //Getting random Transactions to update
        String idToUpdate = addedTransactions.get(ThreadLocalRandom.current().nextInt(1, addedTransactions.size())).get_id();

        //Update Transaction
        LocalDate newDate = LocalDate.now().minus(1, ChronoUnit.DAYS);
        MoneyCalcRs<Transaction> updateTransactionRs
                = sendRequest(service.updateTransaction(token, new TransactionUpdateContainer(idToUpdate, generateTransaction(newDate)))).body();
        assertEquals(updateTransactionRs.getServerStatus(), Status.SUCCESS, updateTransactionRs.getMessage());

        //Getting Transactions list
        MoneyCalcRs<List<Transaction>> getTransactionsRs = sendRequest(service.getTransactions(token, new TransactionsSearchContainer(formatDateToString(newDate),
                formatDateToString(currentDate()), Collections.emptyList()))).body();
        assertEquals(getTransactionsRs.getServerStatus(), Status.SUCCESS, getTransactionsRs.getMessage());

        List<Transaction> transactionsList = getTransactionsRs.getPayload();

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
        assertEquals(updatedTransaction.get_id(), beforeUpdatedTransaction.get_id(), "Id of updated Transaction has changed!");
        assertTrue(assertTransactionsOrderedByDate(transactionsList), "Transaction list is not ordered by date!");
    }
}