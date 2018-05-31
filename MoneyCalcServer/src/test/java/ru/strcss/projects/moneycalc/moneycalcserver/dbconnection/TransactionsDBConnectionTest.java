package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.PersonTransactions;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ru.strcss.projects.moneycalc.testutils.Generator.*;

public class TransactionsDBConnectionTest {

    private MongoTemplate mongoTemplate = mock(MongoTemplate.class);
    private TransactionsDBConnection transactionsDBConnection = new TransactionsDBConnection(mongoTemplate);

    private final List<Integer> sectionIds = Arrays.asList(0, 1, 2);
    private final int transactionsCount = 10;

    @BeforeClass
    public void setUp() throws Exception {
        when(mongoTemplate.updateMulti(any(Query.class), any(Update.class), anyString()))
                .thenReturn(new WriteResult(1, false, new Object()));
        when(mongoTemplate.aggregate(any(Aggregation.class), eq(Person.class), eq(SpendingSection.class)))
                .thenReturn(new AggregationResults<>(Arrays.asList(generateSpendingSection()), new BasicDBObject() {
                }));
        when(mongoTemplate.getConverter())
                .thenReturn(new MappingMongoConverter(new DefaultDbRefResolver(new SimpleMongoDbFactory(
                        new MongoClient("host", 123), "DB")), new MongoMappingContext()));

        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq(PersonTransactions.class)))
                .thenReturn(new WriteResult(1, true, null));

        when(mongoTemplate.updateMulti(any(Query.class), any(Update.class), eq(PersonTransactions.class)))
                .thenReturn(new WriteResult(1, true, null));

        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), anyString()))
                .thenReturn(new WriteResult(1, true, null));

        List<BasicDBObject> mappedResults = Collections.singletonList(new BasicDBObject("transactions",
                generateBasicDBListWithTransactions(transactionsCount, sectionIds)));

        when(mongoTemplate.aggregate(any(Aggregation.class), eq("Transactions"), eq(BasicDBObject.class)))
                .thenReturn(new AggregationResults<>(mappedResults, new BasicDBObject()));
    }

    @Test
    public void testGetTransactions() throws Exception {
        TransactionsSearchContainer searchContainer = new TransactionsSearchContainer("2018-01-01", "2018-02-01", sectionIds);

        List<Transaction> transactions = transactionsDBConnection.getTransactions("login", searchContainer);

        assertNotNull(transactions, "transactions is null!");
        assertEquals(transactions.size(), transactionsCount, "transactions list is empty!");
    }

    @Test
    public void testAddTransaction() throws Exception {
        TransactionAddContainer addContainer = new TransactionAddContainer(generateTransaction());

        WriteResult writeResult = transactionsDBConnection.addTransaction("login", addContainer);

        assertNotNull(writeResult, "WriteResult is null!");
        assertEquals(writeResult.getN(), 1, "WriteResult n is not 1!");
    }

    @Test
    public void testUpdateTransaction() throws Exception {
        TransactionUpdateContainer updateContainer = new TransactionUpdateContainer("id", generateTransaction());

        WriteResult writeResult = transactionsDBConnection.updateTransaction("login", updateContainer);

        assertNotNull(writeResult, "WriteResult is null!");
        assertEquals(writeResult.getN(), 1, "WriteResult n is not 1!");
    }

    @Test
    public void testDeleteTransaction() throws Exception {
        TransactionDeleteContainer deleteContainer = new TransactionDeleteContainer("id");

        WriteResult writeResult = transactionsDBConnection.deleteTransaction("login", deleteContainer);

        assertNotNull(writeResult, "WriteResult is null!");
        assertEquals(writeResult.getN(), 1, "WriteResult n is not 1!");
    }

}