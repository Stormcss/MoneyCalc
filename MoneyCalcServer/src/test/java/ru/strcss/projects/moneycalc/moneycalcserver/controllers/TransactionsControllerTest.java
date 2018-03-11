package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import com.mongodb.WriteResult;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.SettingsDBConnection;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.TransactionsDBConnection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransaction;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransactionList;

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
        when(transactionsDBConnection.getTransactions(anyString(), any(TransactionsSearchContainer.class)))
                .thenReturn(generateTransactionList(transactionsCount, requiredSections));
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
        AjaxRs<List<Transaction>> getTransactionsRs = transactionsController.getTransactions(new TransactionsSearchContainer("2017-02-10", "2017-02-20", requiredSections));

        assertEquals(getTransactionsRs.getStatus(), Status.SUCCESS, getTransactionsRs.getMessage());
        assertEquals(getTransactionsRs.getPayload().size(), (int) transactionsCount, getTransactionsRs.getMessage());
    }

    @Test(groups = "SuccessfulScenario")
    public void testAddTransaction() {
        AjaxRs<Transaction> addTransactionsRs = transactionsController.addTransaction(new TransactionAddContainer(generateTransaction()));

        assertEquals(addTransactionsRs.getStatus(), Status.SUCCESS, addTransactionsRs.getMessage());
    }

    @Test(groups = "SuccessfulScenario")
    public void testUpdateTransaction() {
        AjaxRs<Transaction> updateTransactionsRs = transactionsController.updateTransaction(new TransactionUpdateContainer("223e4", generateTransaction()));

        assertEquals(updateTransactionsRs.getStatus(), Status.SUCCESS, updateTransactionsRs.getMessage());
    }

    @Test(groups = "SuccessfulScenario")
    public void testDeleteTransaction() {
        AjaxRs<Void> deleteTransactionsRs = transactionsController.deleteTransaction(new TransactionDeleteContainer("223e4"));

        assertEquals(deleteTransactionsRs.getStatus(), Status.SUCCESS, deleteTransactionsRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testGetTransactions_emptyRangeFrom() {
        AjaxRs<List<Transaction>> getTransactionsRs = transactionsController.getTransactions(new TransactionsSearchContainer(null, "2017-02-20", requiredSections));

        assertEquals(getTransactionsRs.getStatus(), Status.ERROR, getTransactionsRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testGetTransactions_emptyRangeTo() {
        AjaxRs<List<Transaction>> getTransactionsRs = transactionsController.getTransactions(new TransactionsSearchContainer("2017-02-10", null, requiredSections));

        assertEquals(getTransactionsRs.getStatus(), Status.ERROR, getTransactionsRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testGetTransactions_emptySectionsId() {
        AjaxRs<List<Transaction>> getTransactionsRs = transactionsController.getTransactions(new TransactionsSearchContainer("2017-02-10", "2017-02-20", null));

        assertEquals(getTransactionsRs.getStatus(), Status.ERROR, getTransactionsRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testGetTransactions_RangeFrom_after_RangeTo() {
        AjaxRs<List<Transaction>> getTransactionsRs = transactionsController.getTransactions(new TransactionsSearchContainer("2017-02-20", "2017-02-10", requiredSections));

        assertEquals(getTransactionsRs.getStatus(), Status.ERROR, getTransactionsRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testAddTransaction_emptyTransaction() {
        AjaxRs<Transaction> addTransactionsRs = transactionsController.addTransaction(new TransactionAddContainer(Transaction.builder().build()));

        assertEquals(addTransactionsRs.getStatus(), Status.ERROR, addTransactionsRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testAddTransaction_Transaction_emptyFields() {
        Transaction transaction = Transaction.builder()
                .date("2017-02-20")
                .currency("RUR")
                .sectionID(0)
                .build();

        AjaxRs<Transaction> addTransactionsRs = transactionsController.addTransaction(new TransactionAddContainer(transaction));

        assertEquals(addTransactionsRs.getStatus(), Status.ERROR, addTransactionsRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testUpdateTransaction_emptyId() {
        AjaxRs<Transaction> updateTransactionsRs = transactionsController.updateTransaction(new TransactionUpdateContainer(null, generateTransaction()));

        assertEquals(updateTransactionsRs.getStatus(), Status.ERROR, updateTransactionsRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testUpdateTransaction_emptyTransaction() {
        AjaxRs<Transaction> updateTransactionsRs = transactionsController.updateTransaction(new TransactionUpdateContainer("223e4", null));

        assertEquals(updateTransactionsRs.getStatus(), Status.ERROR, updateTransactionsRs.getMessage());
    }

    @Test(groups = "incorrectContainers")
    public void testDeleteTransaction_emptyId() {
        AjaxRs<Void> deleteTransactionsRs = transactionsController.deleteTransaction(new TransactionDeleteContainer(null));

        assertEquals(deleteTransactionsRs.getStatus(), Status.ERROR, deleteTransactionsRs.getMessage());
    }
}