//package ru.strcss.projects.moneycalc.moneycalcserver.controllers;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.BeforeGroups;
//import org.testng.annotations.Test;
//import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
//import ru.strcss.projects.moneycalc.dto.Status;
//import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
//import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
//import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
//import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
//import ru.strcss.projects.moneycalc.entities.Transaction;
//import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.PersonService;
//import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.SpendingSectionService;
//import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.TransactionsService;
//
//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.mockito.Matchers.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import static org.testng.Assert.assertEquals;
//import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransaction;
//import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransactionList;
//
//public class TransactionsControllerTest {
//
//    private TransactionsService transactionsService = mock(TransactionsService.class);
//    private PersonService personService = mock(PersonService.class);
//    private SpendingSectionService sectionService = mock(SpendingSectionService.class);
//
//    private TransactionsController transactionsController;
//    private List<Integer> requiredSections = Arrays.asList(0, 1);
//    private Integer transactionsCount = 50;
//
//    private LocalDate dateFrom = LocalDate.of(2017, 2, 10);
//    private LocalDate dateTo = LocalDate.of(2017, 2, 20);
//
//    @BeforeClass
//    public void setUp() {
//        User user = new User("login", "password", Collections.emptyList());
//        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
//        SecurityContextHolder.getContext().setAuthentication(auth);
//    }
//
//    @BeforeGroups(groups = {"successfulScenario", "incorrectContainers"})
//    public void prepare_successfulScenario_incorrectContainers() {
//        List<Transaction> transactionList = generateTransactionList(transactionsCount, requiredSections);
//        transactionList.get(1).setDate(LocalDate.now().minus(1, ChronoUnit.DAYS));
//
//        when(transactionsService.getTransactions(anyString(), any(TransactionsSearchContainer.class))).thenReturn(transactionList);
//        when(transactionsService.getTransactionById(anyInt()))
//                .thenReturn(generateTransaction());
//        when(transactionsService.addTransaction(anyInt(), any(Transaction.class)))
//                .thenReturn(1);
//        when(transactionsService.deleteTransaction(any(Transaction.class)))
//                .thenReturn(true);
//        when(transactionsService.updateTransaction(any(Transaction.class)))
//                .thenReturn(true);
//        when(sectionService.isSpendingSectionIdExists(anyInt(), anyInt()))
//                .thenReturn(true);
//
//        transactionsController = new TransactionsController(transactionsService, sectionService, personService);
//    }
//
//    @BeforeGroups(groups = "failedScenario")
//    public void prepare_failedScenario() {
//        transactionsController = new TransactionsController(transactionsService, sectionService, personService);
//    }
//
//    @Test(groups = "successfulScenario")
//    public void testGetTransactions() {
//        ResponseEntity<MoneyCalcRs<List<Transaction>>> getTransactionsRs = transactionsController.getTransactions(
//                new TransactionsSearchContainer(dateFrom, dateTo, requiredSections, null, null, null, null));
//
//        assertEquals(getTransactionsRs.getBody().getServerStatus(), Status.SUCCESS, getTransactionsRs.getBody().getMessage());
//        assertEquals(getTransactionsRs.getBody().getPayload().size(), (int) transactionsCount, getTransactionsRs.getBody().getMessage());
////        assertTrue(assertTransactionsOrderedByDate(getTransactionsRs.getBody().getPayload()), "Transactions are not ordered by date!");
//    }
//
//    @Test(groups = "successfulScenario")
//    public void testAddTransaction() {
//        ResponseEntity<MoneyCalcRs<Transaction>> addTransactionsRs = transactionsController.addTransaction(
//                new TransactionAddContainer(generateTransaction()));
//
//        assertEquals(addTransactionsRs.getBody().getServerStatus(), Status.SUCCESS, addTransactionsRs.getBody().getMessage());
//    }
//
//    @Test(groups = "successfulScenario")
//    public void testUpdateTransaction() {
//        ResponseEntity<MoneyCalcRs<Transaction>> updateTransactionsRs = transactionsController.updateTransaction(
//                new TransactionUpdateContainer(33, generateTransaction()));
//
//        assertEquals(updateTransactionsRs.getBody().getServerStatus(), Status.SUCCESS, updateTransactionsRs.getBody().getMessage());
//    }
//
//    @Test(groups = "successfulScenario")
//    public void testDeleteTransaction() {
//        ResponseEntity<MoneyCalcRs<Void>> deleteTransactionsRs =
//                transactionsController.deleteTransaction(new TransactionDeleteContainer(2));
//
//        assertEquals(deleteTransactionsRs.getBody().getServerStatus(), Status.SUCCESS, deleteTransactionsRs.getBody().getMessage());
//    }
//
//    @Test(groups = "incorrectContainers")
//    public void testGetTransactions_emptyRangeFrom() {
//        ResponseEntity<MoneyCalcRs<List<Transaction>>> getTransactionsRs = transactionsController.getTransactions(
//                new TransactionsSearchContainer(null, dateTo, requiredSections, null, null, null, null));
//
//        assertEquals(getTransactionsRs.getBody().getServerStatus(), Status.ERROR, getTransactionsRs.getBody().getMessage());
//    }
//
//    @Test(groups = "incorrectContainers")
//    public void testGetTransactions_emptyRangeTo() {
//        ResponseEntity<MoneyCalcRs<List<Transaction>>> getTransactionsRs = transactionsController.getTransactions(
//                new TransactionsSearchContainer(dateFrom, null, requiredSections, null, null, null, null));
//
//        assertEquals(getTransactionsRs.getBody().getServerStatus(), Status.ERROR, getTransactionsRs.getBody().getMessage());
//    }
//
//    @Test(groups = "incorrectContainers", enabled = false)
//    public void testGetTransactions_emptySectionsId() {
//        ResponseEntity<MoneyCalcRs<List<Transaction>>> getTransactionsRs = transactionsController.getTransactions(
//                new TransactionsSearchContainer(dateFrom, dateTo, null, null, null, null, null));
//
//        assertEquals(getTransactionsRs.getBody().getServerStatus(), Status.ERROR, getTransactionsRs.getBody().getMessage());
//    }
//
//    @Test(groups = "incorrectContainers")
//    public void testGetTransactions_RangeFrom_after_RangeTo() {
//        ResponseEntity<MoneyCalcRs<List<Transaction>>> getTransactionsRs = transactionsController.getTransactions(
//                new TransactionsSearchContainer(dateTo, dateFrom, requiredSections, null, null, null, null));
//
//        assertEquals(getTransactionsRs.getBody().getServerStatus(), Status.ERROR, getTransactionsRs.getBody().getMessage());
//    }
//
//    @Test(groups = "incorrectContainers")
//    public void testAddTransaction_emptyTransaction() {
//        ResponseEntity<MoneyCalcRs<Transaction>> addTransactionsRs = transactionsController.addTransaction(
//                new TransactionAddContainer(Transaction.builder().build()));
//
//        assertEquals(addTransactionsRs.getBody().getServerStatus(), Status.ERROR, addTransactionsRs.getBody().getMessage());
//    }
//
//    @Test(groups = "incorrectContainers")
//    public void testAddTransaction_Transaction_emptyFields() {
//        Transaction transaction = Transaction.builder()
//                .date(dateTo)
//                .currency("RUR")
//                .sectionId(0)
//                .build();
//
//        ResponseEntity<MoneyCalcRs<Transaction>> addTransactionsRs =
//                transactionsController.addTransaction(new TransactionAddContainer(transaction));
//
//        assertEquals(addTransactionsRs.getBody().getServerStatus(), Status.ERROR, addTransactionsRs.getBody().getMessage());
//    }
//
//    @Test(groups = "incorrectContainers")
//    public void testUpdateTransaction_emptyId() {
//        ResponseEntity<MoneyCalcRs<Transaction>> updateTransactionsRs = transactionsController.updateTransaction(
//                new TransactionUpdateContainer(null, generateTransaction()));
//
//        assertEquals(updateTransactionsRs.getBody().getServerStatus(), Status.ERROR, updateTransactionsRs.getBody().getMessage());
//    }
//
//    @Test(groups = "incorrectContainers")
//    public void testUpdateTransaction_emptyTransaction() {
//        ResponseEntity<MoneyCalcRs<Transaction>> updateTransactionsRs = transactionsController.updateTransaction(
//                new TransactionUpdateContainer(2, null));
//
//        assertEquals(updateTransactionsRs.getBody().getServerStatus(), Status.ERROR, updateTransactionsRs.getBody().getMessage());
//    }
//
//    @Test(groups = "incorrectContainers")
//    public void testDeleteTransaction_emptyId() {
//        ResponseEntity<MoneyCalcRs<Void>> deleteTransactionsRs = transactionsController.deleteTransaction(
//                new TransactionDeleteContainer(null));
//
//        assertEquals(deleteTransactionsRs.getBody().getServerStatus(), Status.ERROR, deleteTransactionsRs.getBody().getMessage());
//    }
//
//    @Test(groups = "failedScenario")
//    public void testAddTransaction_missingSectionId() {
//        when(sectionService.isSpendingSectionIdExists(anyInt(), anyInt()))
//                .thenReturn(false);
//        transactionsController = new TransactionsController(transactionsService, sectionService, personService);
//
//        ResponseEntity<MoneyCalcRs<Transaction>> addTransactionsRs = transactionsController.addTransaction(
//                new TransactionAddContainer(generateTransaction()));
//
//        assertEquals(addTransactionsRs.getBody().getServerStatus(), Status.ERROR, addTransactionsRs.getBody().getMessage());
//    }
//
//    @Test(groups = "failedScenario")
//    public void testUpdateTransaction_incorrectTransaction() {
//        Transaction transaction = generateTransaction();
//        transaction.setSectionId(null);
//        ResponseEntity<MoneyCalcRs<Transaction>> addTransactionsRs = transactionsController.addTransaction(
//                new TransactionAddContainer(transaction));
//
//        assertEquals(addTransactionsRs.getBody().getServerStatus(), Status.ERROR, addTransactionsRs.getBody().getMessage());
//    }
//
//
//}