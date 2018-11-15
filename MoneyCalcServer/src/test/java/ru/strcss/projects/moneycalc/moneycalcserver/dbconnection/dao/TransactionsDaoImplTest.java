//package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;
//
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.query.NativeQuery;
//import org.hibernate.query.Query;
//import org.testng.annotations.BeforeGroups;
//import org.testng.annotations.Test;
//import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
//import ru.strcss.projects.moneycalc.entities.Transaction;
//import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.PersonDao;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import static org.testng.Assert.*;
//import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransaction;
//import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransactionList;
//
//public class TransactionsDaoImplTest {
//    private TransactionsDaoImpl transactionsDao;
//
//    private SessionFactory sessionFactory = mock(SessionFactory.class);
//    private Session mockedSession = mock(Session.class);
//    private org.hibernate.Transaction mockedTransaction = mock(org.hibernate.Transaction.class);
//    private Query mockedQuery = mock(Query.class);
//    private NativeQuery mockedNativeQuery = mock(NativeQuery.class);
//    private EntityManagerFactory mockedEntityManagerFactory = mock(EntityManagerFactory.class);
//    private EntityManager mockedEntityManager = mock(EntityManager.class);
//
//    private PersonDao personDao = mock(PersonDao.class);
//
//    private LocalDate now = LocalDate.now();
//    private LocalDate tomorrow = LocalDate.now().plus(1, ChronoUnit.DAYS);
//    private List<Integer> sectionIds = Arrays.asList(0, 1);
//
//    @BeforeGroups(groups = "TransactionsDaoSuccessfulScenario")
//    public void setUp() throws Exception {
//        when(sessionFactory.openSession())
//                .thenReturn(mockedSession);
//        when(mockedSession.createQuery(anyString(), any()))
//                .thenReturn(mockedQuery);
//        when(mockedSession.createNativeQuery(anyString(), any(Class.class)))
//                .thenReturn(mockedNativeQuery);
//        when(mockedSession.save(any()))
//                .thenReturn(1);
//        when(mockedSession.getEntityManagerFactory())
//                .thenReturn(mockedEntityManagerFactory);
//
//        when(mockedEntityManagerFactory.createEntityManager())
//                .thenReturn(mockedEntityManager);
//
//        when(mockedSession.getTransaction())
//                .thenReturn(mockedTransaction);
//        when(mockedEntityManager.getTransaction())
//                .thenReturn(mockedTransaction);
//
//        when(mockedQuery.setParameter(anyString(), any()))
//                .thenReturn(mockedQuery);
//        when(mockedNativeQuery.setParameter(anyString(), any()))
//                .thenReturn(mockedNativeQuery);
//        when(mockedQuery.list())
//                .thenReturn(generateTransactionList(5, Arrays.asList(0, 1)));
//        when(mockedNativeQuery.list())
//                .thenReturn(generateTransactionList(5, Arrays.asList(0, 1)));
//        when(mockedQuery.getSingleResult())
//                .thenReturn(generateTransaction(null, 1, null, 1, null, null));
//
//        transactionsDao = new TransactionsDaoImpl(sessionFactory, personDao);
//    }
//
//    @Test(groups = "TransactionsDaoSuccessfulScenario")
//    public void testGetTransactionById() throws Exception {
//        Transaction transaction = transactionsDao.getTransactionById(1);
//
//        assertNotNull(transaction);
//        assertEquals((int) transaction.getId(), 1);
//    }
//
//    @Test(groups = "TransactionsDaoSuccessfulScenario")
//    public void testGetTransactionsByPersonId() throws Exception {
//        List<Transaction> transactionList = transactionsDao.getTransactionsByPersonId(1, now, tomorrow, sectionIds);
//        assertFalse(transactionList.isEmpty());
//    }
//
//    @Test(groups = "TransactionsDaoSuccessfulScenario")
//    public void testGetTransactions() throws Exception {
//        List<Transaction> transactionList = transactionsDao.getTransactions("login", new TransactionsSearchContainer());
//        assertFalse(transactionList.isEmpty());
//    }
//
//    @Test(groups = "TransactionsDaoSuccessfulScenario")
//    public void testAddTransaction() throws Exception {
//        Integer transactionId = transactionsDao.addTransaction(1, generateTransaction());
//        assertEquals((int) transactionId, 1);
//    }
//
//    @Test(groups = "TransactionsDaoSuccessfulScenario")
//    public void testUpdateTransaction() throws Exception {
//        boolean isUpdateSuccessful = transactionsDao.updateTransaction(generateTransaction());
//        assertTrue(isUpdateSuccessful);
//    }
//
//    @Test(groups = "TransactionsDaoSuccessfulScenario")
//    public void testDeleteTransaction() throws Exception {
//        boolean isDeleteSuccessful = transactionsDao.deleteTransaction(generateTransaction());
//        assertTrue(isDeleteSuccessful);
//    }
//
//    @Test(groups = "TransactionsDaoSuccessfulScenario")
//    public void testSetSessionFactory() throws Exception {
//        transactionsDao.setSessionFactory(sessionFactory);
//    }
//
//}