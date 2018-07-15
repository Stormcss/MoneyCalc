package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.testng.Assert.*;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.*;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDateMinus;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransaction;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.assertTransactionsOrderedByDate;

@Slf4j
public class TransactionsControllerIT extends AbstractIT {

    private final String INCORRECT_TRANSACTIONS_COUNT = "Incorrect count of transactions has returned";

    /**
     * Checks for correct Transactions returning for specific range
     */
    @Test
    public void getTransaction_RangeChecks() {
        String token = savePersonGetToken(service);

        //Adding new Transactions
        addTransaction(service, token, generateTransaction());
        addTransaction(service, token, generateTransaction(generateDatePlus(ChronoUnit.DAYS, 1)));

        //Requesting Transactions from today to tomorrow
        MoneyCalcRs<List<Transaction>> today2TomorrowRs = getTransactions(service, token, LocalDate.now(),
                generateDatePlus(ChronoUnit.DAYS, 1), Collections.emptyList());
        assertEquals(today2TomorrowRs.getPayload().size(), 2, INCORRECT_TRANSACTIONS_COUNT);

        //Requesting Transactions from yesterday to today
        MoneyCalcRs<List<Transaction>> yesterday2TodayRs = getTransactions(service, token,
                generateDateMinus(ChronoUnit.DAYS, 1), LocalDate.now(), Collections.emptyList());
        assertEquals(yesterday2TodayRs.getPayload().size(), 1, INCORRECT_TRANSACTIONS_COUNT);

        //Requesting Transactions from yesterday to tomorrow
        MoneyCalcRs<List<Transaction>> tomorrowAndLaterRs = getTransactions(service, token,
                generateDatePlus(ChronoUnit.DAYS, 1), generateDatePlus(ChronoUnit.DAYS, 2), Collections.emptyList());
        assertEquals(tomorrowAndLaterRs.getPayload().size(), 1, INCORRECT_TRANSACTIONS_COUNT);
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
        assertEquals(addedTransactions.size(), numOfAddedTransactionsPerSection * numOfSections,
                "Some Transactions were not saved!");

        //Requesting Transactions with Single Section
        for (int sectionID = 0; sectionID < numOfSections; sectionID++) {
            int finalSectionID = sectionID;
            // FIXME: 11.02.2018 I suppose it could be done better

            MoneyCalcRs<List<Transaction>> singleSectionRs = getTransactions(service, token, LocalDate.now(),
                    generateDatePlus(ChronoUnit.DAYS, 1), sectionID);

            assertEquals(singleSectionRs.getPayload().size(), numOfAddedTransactionsPerSection, INCORRECT_TRANSACTIONS_COUNT);
            assertTrue(singleSectionRs.getPayload().stream().allMatch(t -> t.getSectionId() == finalSectionID),
                    "Some of returned Transactions have wrong SectionID");
        }
        //Requesting Transactions with Multiple Sections
        if (numOfSections > 1) {
            MoneyCalcRs<List<Transaction>> multipleSectionsRs = getTransactions(service, token, LocalDate.now(),
                    generateDatePlus(ChronoUnit.DAYS, 1), Arrays.asList(0, 1));
            assertEquals(multipleSectionsRs.getPayload().size(), numOfAddedTransactionsPerSection * 2,
                    INCORRECT_TRANSACTIONS_COUNT);
        }
    }

    /**
     * Saving new Transaction to empty list
     */
    @Test
    public void saveNewTransaction() {
        String token = savePersonGetToken(service);

        addTransaction(service, token, generateTransaction());

        MoneyCalcRs<List<Transaction>> getTransactionsRs = getTransactions(service, token,
                generateDateMinus(ChronoUnit.DAYS, 1), generateDatePlus(ChronoUnit.DAYS, 1), Collections.emptyList());
        assertEquals(getTransactionsRs.getPayload().size(), 1, "Size of returned Transactions list is not 1!");
    }

    /**
     * Saving new Transaction with nonexistent sectionID
     */
    @Test
    public void saveNewTransaction_nonExistentSectionID() {
        String token = savePersonGetToken(service);

        Response<MoneyCalcRs<Transaction>> addTransactionRs = sendRequest(service.addTransaction(token,
                new TransactionAddContainer(generateTransaction(10))));

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
        Integer idToDelete = addedTransactions.get(ThreadLocalRandom.current().nextInt(addedTransactions.size())).getId();

        sendRequest(service.deleteTransaction(token, new TransactionDeleteContainer(idToDelete)), Status.SUCCESS).body();

        //Getting Transactions list
        MoneyCalcRs<List<Transaction>> getTransactionsRs = getTransactions(service, token, LocalDate.now(), LocalDate.now(),
                Collections.emptyList());
        assertEquals(getTransactionsRs.getPayload().size(), numOfAddedTransactions - 1,
                "List size after delete has not decreased!");
        assertFalse(getTransactionsRs.getPayload().stream().anyMatch(transaction -> transaction.getId().equals(idToDelete)),
                "Transaction was not deleted!");
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
        Integer idToUpdate = addedTransactions.get(ThreadLocalRandom.current().nextInt(1, addedTransactions.size())).getId();

        //Update Transaction
        LocalDate newDate = LocalDate.now().minus(1, ChronoUnit.DAYS);
        sendRequest(service.updateTransaction(token,
                new TransactionUpdateContainer(idToUpdate, generateTransaction(newDate))), Status.SUCCESS);

        //Getting Transactions list
        List<Transaction> transactionsList = getTransactions(service, token, newDate, LocalDate.now(),
                Collections.emptyList()).getPayload();

        assertTrue(transactionsList.stream()
                .map(Transaction::getId)
                .anyMatch(id -> id.equals(idToUpdate)), "Id of updated Transaction has changed!");
        assertEquals(transactionsList.size(), numOfAddedTransactions, "Size of Transactions list has changed!");

        Transaction beforeUpdatedTransaction = addedTransactions.stream()
                .filter(transaction -> transaction.getId().equals(idToUpdate))
                .findFirst()
                .get();
        Transaction updatedTransaction = transactionsList.stream()
                .filter(transaction -> transaction.getId().equals(idToUpdate))
                .findFirst()
                .get();

        assertNotEquals(updatedTransaction.getSum(), beforeUpdatedTransaction.getSum(), "Sum in Transaction after update has not changed!");
        assertEquals(updatedTransaction.getId(), beforeUpdatedTransaction.getId(), "id of updated Transaction has changed!");
        assertEquals(updatedTransaction.getSectionId(), beforeUpdatedTransaction.getSectionId(), "inner id of updated Transaction has changed!");
        assertTrue(assertTransactionsOrderedByDate(transactionsList), "Transaction list is not ordered by date!");
    }
}