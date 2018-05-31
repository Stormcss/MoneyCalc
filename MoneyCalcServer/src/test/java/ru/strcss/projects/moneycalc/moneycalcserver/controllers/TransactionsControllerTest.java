package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import com.mongodb.WriteResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.SettingsDBConnection;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.TransactionsDBConnection;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.formatDateToString;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransaction;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransactionList;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.assertTransactionsOrderedByDate;

public class TransactionsControllerTest {

    private TransactionsDBConnection transactionsDBConnection = mock(TransactionsDBConnection.class);
    private SettingsDBConnection settingsDBConnection = mock(SettingsDBConnection.class);
    private TransactionsController transactionsController;
    private List<Integer> requiredSections = Arrays.asList(0, 1);
    private Integer transactionsCount = 50;

    @BeforeClass
    public void setUp() {
        User user = new User("login", "password", Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @BeforeGroups(groups = {"SuccessfulScenario", "incorrectContainers"})
    public void prepare_successfulScenario_incorrectContainers() {
        List<Transaction> transactionList = generateTransactionList(transactionsCount, requiredSections);
        transactionList.get(1).setDate(formatDateToString(LocalDate.now().minus(1, ChronoUnit.DAYS)));

        when(transactionsDBConnection.getTransactions(anyString(), any(TransactionsSearchContainer.class)))
                .thenReturn(transactionList);
        when(transactionsDBConnection.addTransaction(anyString(), any(TransactionAddContainer.class)))
                .thenReturn(new WriteResult(1, false, new Object()));
        when(transactionsDBConnection.deleteTransaction(anyString(), any(TransactionDeleteContainer.class)))
                .thenReturn(new WriteResult(1, false, new Object()));
        when(transactionsDBConnection.updateTransaction(anyString(), any(TransactionUpdateContainer.class)))
                .thenReturn(new WriteResult(1, false, new Object()));
        when(settingsDBConnection.isSpendingSectionIDExists(anyString(), anyInt()))
                .thenReturn(true);

        transactionsController = new TransactionsController(transactionsDBConnection, settingsDBConnection);
    }


    @Test(groups = "SuccessfulScenario")
    public void testGetTransactions() {
        ResponseEntity<MoneyCalcRs<List<Transaction>>> getTransactionsRs = transactionsController.getTransactions(
                new TransactionsSearchContainer("2017-02-10", "2017-02-20", requiredSections));

        assertEquals(getTransactionsRs.getBody().getServerStatus(), Status.SUCCESS, getTransactionsRs.getBody().getMessage());
        assertEquals(getTransactionsRs.getBody().getPayload().size(), (int) transactionsCount, getTransactionsRs.getBody().getMessage());
        assertTrue(assertTransactionsOrderedByDate(getTransactionsRs.getBody().getPayload()), "Transactions are not ordered by date!");
    }

    @Test(groups = "SuccessfulScenario")
    public void testAddTransaction() {
        ResponseEntity<MoneyCalcRs<Transaction>> addTransactionsRs = transactionsController.addTransaction(
                new TransactionAddContainer(generateTransaction()));

        assertEquals(addTransactionsRs.getBody().getServerStatus(), Status.SUCCESS, addTransactionsRs.getBody().getMessage());
    }

    @Test(groups = "SuccessfulScenario")
    public void testUpdateTransaction() {
        ResponseEntity<MoneyCalcRs<Transaction>> updateTransactionsRs = transactionsController.updateTransaction(
                new TransactionUpdateContainer("223e4", generateTransaction()));

        assertEquals(updateTransactionsRs.getBody().getServerStatus(), Status.SUCCESS, updateTransactionsRs.getBody().getMessage());
    }

    @Test(groups = "SuccessfulScenario")
    public void testDeleteTransaction() {
        ResponseEntity<MoneyCalcRs<Void>> deleteTransactionsRs =
                transactionsController.deleteTransaction(new TransactionDeleteContainer("223e4"));

        assertEquals(deleteTransactionsRs.getBody().getServerStatus(), Status.SUCCESS, deleteTransactionsRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testGetTransactions_emptyRangeFrom() {
        ResponseEntity<MoneyCalcRs<List<Transaction>>> getTransactionsRs = transactionsController.getTransactions(
                new TransactionsSearchContainer(null, "2017-02-20", requiredSections));

        assertEquals(getTransactionsRs.getBody().getServerStatus(), Status.ERROR, getTransactionsRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testGetTransactions_emptyRangeTo() {
        ResponseEntity<MoneyCalcRs<List<Transaction>>> getTransactionsRs = transactionsController.getTransactions(
                new TransactionsSearchContainer("2017-02-10", null, requiredSections));

        assertEquals(getTransactionsRs.getBody().getServerStatus(), Status.ERROR, getTransactionsRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testGetTransactions_emptySectionsId() {
        ResponseEntity<MoneyCalcRs<List<Transaction>>> getTransactionsRs = transactionsController.getTransactions(
                new TransactionsSearchContainer("2017-02-10", "2017-02-20", null));

        assertEquals(getTransactionsRs.getBody().getServerStatus(), Status.ERROR, getTransactionsRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testGetTransactions_RangeFrom_after_RangeTo() {
        ResponseEntity<MoneyCalcRs<List<Transaction>>> getTransactionsRs = transactionsController.getTransactions(
                new TransactionsSearchContainer("2017-02-20", "2017-02-10", requiredSections));

        assertEquals(getTransactionsRs.getBody().getServerStatus(), Status.ERROR, getTransactionsRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testAddTransaction_emptyTransaction() {
        ResponseEntity<MoneyCalcRs<Transaction>> addTransactionsRs = transactionsController.addTransaction(
                new TransactionAddContainer(Transaction.builder().build()));

        assertEquals(addTransactionsRs.getBody().getServerStatus(), Status.ERROR, addTransactionsRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testAddTransaction_Transaction_emptyFields() {
        Transaction transaction = Transaction.builder()
                .date("2017-02-20")
                .currency("RUR")
                .sectionID(0)
                .build();

        ResponseEntity<MoneyCalcRs<Transaction>> addTransactionsRs =
                transactionsController.addTransaction(new TransactionAddContainer(transaction));

        assertEquals(addTransactionsRs.getBody().getServerStatus(), Status.ERROR, addTransactionsRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testUpdateTransaction_emptyId() {
        ResponseEntity<MoneyCalcRs<Transaction>> updateTransactionsRs = transactionsController.updateTransaction(
                new TransactionUpdateContainer(null, generateTransaction()));

        assertEquals(updateTransactionsRs.getBody().getServerStatus(), Status.ERROR, updateTransactionsRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testUpdateTransaction_emptyTransaction() {
        ResponseEntity<MoneyCalcRs<Transaction>> updateTransactionsRs = transactionsController.updateTransaction(
                new TransactionUpdateContainer("223e4", null));

        assertEquals(updateTransactionsRs.getBody().getServerStatus(), Status.ERROR, updateTransactionsRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testDeleteTransaction_emptyId() {
        ResponseEntity<MoneyCalcRs<Void>> deleteTransactionsRs = transactionsController.deleteTransaction(
                new TransactionDeleteContainer(null));

        assertEquals(deleteTransactionsRs.getBody().getServerStatus(), Status.ERROR, deleteTransactionsRs.getBody().getMessage());
    }

}