package ru.strcss.projects.moneycalc.integration;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.addTransaction;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.getTransactions;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.savePersonGetToken;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.sendRequest;
import static ru.strcss.projects.moneycalc.moneycalcdto.dto.Status.SUCCESS;
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
    public void getTransactionRangeChecks() {
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
    public void getTransactionSectionFilter() {
        int numOfAddedTransactionsPerSection = 5;
        int numOfSections = 2; //currently Person by default has only 2 sections

        String token = savePersonGetToken(service);

        //Adding new Transactions
        List<Transaction> addedTransactions = new ArrayList<>();
        for (int i = 1; i <= numOfSections; i++) {
            int sectionId = i;
            addedTransactions.addAll(IntStream.range(0, numOfAddedTransactionsPerSection)
                    .mapToObj(s -> sendRequest(service.addTransaction(token, generateTransaction(sectionId))).body())
                    .filter(Objects::nonNull)
                    .map(MoneyCalcRs::getPayload)
                    .collect(Collectors.toList()));
        }
        assertEquals(addedTransactions.size(), numOfAddedTransactionsPerSection * numOfSections,
                "Some Transactions were not saved!");

        //Requesting Transactions with Single Section
        for (int sectionId = 1; sectionId <= numOfSections; sectionId++) {
            int finalSectionId = sectionId;

            MoneyCalcRs<List<Transaction>> singleSectionRs = getTransactions(service, token, LocalDate.now(),
                    generateDatePlus(ChronoUnit.DAYS, 1), Collections.singletonList(sectionId));

            assertEquals(singleSectionRs.getPayload().size(), numOfAddedTransactionsPerSection, INCORRECT_TRANSACTIONS_COUNT);
            assertTrue(singleSectionRs.getPayload().stream().allMatch(t -> t.getSectionId() == finalSectionId),
                    "Some of returned Transactions has wrong SectionId");
        }
        //Requesting Transactions with Multiple Sections
        if (numOfSections > 1) {
            MoneyCalcRs<List<Transaction>> multipleSectionsRs = getTransactions(service, token, LocalDate.now(),
                    generateDatePlus(ChronoUnit.DAYS, 1), Arrays.asList(1, 2));
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
    public void saveNewTransactionNonExistentSectionID() {
        String token = savePersonGetToken(service);

        Response<MoneyCalcRs<Transaction>> addTransactionRs = sendRequest(service.addTransaction(token,
                generateTransaction(10)));

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
                .mapToObj(s -> sendRequest(service.addTransaction(token, generateTransaction())).body())
                .filter(Objects::nonNull)
                .map(MoneyCalcRs::getPayload)
                .collect(Collectors.toList());

        assertEquals(addedTransactions.size(), numOfAddedTransactions, "Some Transactions were not created!");

        //Getting random Transactions to delete
        Long idToDelete = addedTransactions.get(ThreadLocalRandom.current().nextInt(addedTransactions.size())).getId();

        sendRequest(service.deleteTransaction(token, idToDelete), SUCCESS).body();

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
                .mapToObj(s -> sendRequest(service.addTransaction(token, generateTransaction())).body())
                .filter(Objects::nonNull)
                .map(MoneyCalcRs::getPayload)
                .collect(Collectors.toList());

        assertEquals(addedTransactions.size(), numOfAddedTransactions, "Some Transactions were not created!");

        //Getting random Transactions to update
        Long idToUpdate = addedTransactions.get(ThreadLocalRandom.current().nextInt(0, addedTransactions.size()))
                .getId();

        //Update Transaction
        LocalDate newDate = LocalDate.now().minus(1, ChronoUnit.DAYS);
        sendRequest(service.updateTransaction(token,
                new TransactionUpdateContainer(idToUpdate, generateTransaction(newDate))), SUCCESS);

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

    /**
     * Checking that updating single transaction won't lead to updating others
     */
    @Test
    public void shouldUpdateOnlyOneTransaction() {
        String token = savePersonGetToken(service);

        String title1 = "title1";
        String newTitle = "newTitle!";

        addTransaction(service, token, generateTransaction(title1, "123"));
        Long updatedTransactionId = addTransaction(service, token, generateTransaction("title2", "desc2"))
                .getPayload().getId();

        Transaction newTransaction = generateTransaction(newTitle, "newDesc");
        sendRequest(service.updateTransaction(token, new TransactionUpdateContainer(updatedTransactionId, newTransaction)), SUCCESS).body();

        List<Transaction> transactionList = getTransactions(service, token).getPayload();

        assertEquals(transactionList.get(0).getTitle(), title1, "Old transaction title has changed!");
        assertEquals(transactionList.get(1).getTitle(), newTitle, "Transaction title has not changed!");
    }

    @Test
    public void getTransactionFilterByTitle() {
        int numOfAddedTransactions = 5;
        String token = savePersonGetToken(service);

        IntStream.range(0, numOfAddedTransactions)
                .forEach(value -> addTransaction(service, token, generateTransaction("title" + value, "desc" + value)));

        TransactionsSearchFilter searchContainer = new TransactionsSearchFilter();
        searchContainer.setDateFrom(generateDateMinus(ChronoUnit.DAYS, 1));
        searchContainer.setDateTo(generateDatePlus(ChronoUnit.DAYS, 1));

        // filtering by title by mask
        searchContainer.setTitle("%itle%");
        List<Transaction> filteredTransactions = getTransactions(service, token, searchContainer).getPayload();
        assertEquals(filteredTransactions.size(), numOfAddedTransactions, INCORRECT_TRANSACTIONS_COUNT);

        // filtering by exact match
        searchContainer.setTitle("title4");
        filteredTransactions = getTransactions(service, token, searchContainer).getPayload();
        assertEquals(filteredTransactions.size(), 1, INCORRECT_TRANSACTIONS_COUNT);
    }

    @Test
    public void getTransactionFilterByDescription() {
        int numOfAddedTransactions = 5;
        String token = savePersonGetToken(service);

        IntStream.range(0, numOfAddedTransactions)
                .forEach(value -> addTransaction(service, token, generateTransaction("title" + value, "desc" + value)));

        TransactionsSearchFilter searchContainer = new TransactionsSearchFilter();
        searchContainer.setDateFrom(generateDateMinus(ChronoUnit.DAYS, 1));
        searchContainer.setDateTo(generateDatePlus(ChronoUnit.DAYS, 1));

        // filtering by title by mask
        searchContainer.setDescription("%esc%");
        List<Transaction> filteredTransactions = getTransactions(service, token, searchContainer).getPayload();
        assertEquals(filteredTransactions.size(), numOfAddedTransactions, INCORRECT_TRANSACTIONS_COUNT);

        // filtering by exact match
        searchContainer.setDescription("desc4");
        filteredTransactions = getTransactions(service, token, searchContainer).getPayload();
        assertEquals(filteredTransactions.size(), 1, INCORRECT_TRANSACTIONS_COUNT);
    }

    @Test
    public void getTransactionFilterByPrice() {
        int numOfAddedTransactions = 5;
        String token = savePersonGetToken(service);

        IntStream.range(0, numOfAddedTransactions)
                .forEach(value -> addTransaction(service, token, generateTransaction(1, value * 100 + 1)));

        TransactionsSearchFilter searchContainer = new TransactionsSearchFilter();
        searchContainer.setDateFrom(generateDateMinus(ChronoUnit.DAYS, 1));
        searchContainer.setDateTo(generateDatePlus(ChronoUnit.DAYS, 1));

        searchContainer.setPriceFrom(BigDecimal.valueOf(100));
        searchContainer.setPriceTo(BigDecimal.valueOf(301));
        List<Transaction> filteredTransactions = getTransactions(service, token, searchContainer).getPayload();
        assertEquals(filteredTransactions.size(), 3, INCORRECT_TRANSACTIONS_COUNT);
    }
}