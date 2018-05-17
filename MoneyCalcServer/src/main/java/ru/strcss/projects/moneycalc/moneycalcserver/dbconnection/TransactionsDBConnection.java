package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.PersonTransactions;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
public class TransactionsDBConnection {

    private MongoTemplate mongoTemplate;

    @Autowired
    public TransactionsDBConnection(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Get Transactions List from DB layer
     *
     * @param container with search parameters
     * @return List of Transactions
     */
    public List<Transaction> getTransactions(String login, TransactionsSearchContainer container) {
        Aggregation aggregation = newAggregation(match(Criteria.where("login").is(login)),
                project("login").and(aggregationOperationContext -> {

                    List<Object> filterCriterias = new ArrayList<>();

                    filterCriterias.add(new BasicDBObject("$gte", Arrays.<Object>asList("$$result.date", container.getRangeFrom())));
                    filterCriterias.add(new BasicDBObject("$lte", Arrays.<Object>asList("$$result.date", container.getRangeTo())));

                    if (container.getRequiredSections().size() != 0) {
                        filterCriterias.add(new BasicDBObject("$and", new BasicDBObject("$in", Arrays.asList("$$result.sectionID", container.getRequiredSections()))));
                    }

                    BasicDBObject filter = new BasicDBObject("input", "$transactions").append("as", "result")
                            .append("cond", new BasicDBObject("$and", filterCriterias));


                    return new BasicDBObject("$filter", filter);
                }).as("transactions"));
        List<BasicDBObject> dbObjectTransactionsList = mongoTemplate.aggregate(aggregation, "Transactions", BasicDBObject.class).getMappedResults();

        Object rawTransactionsObject = dbObjectTransactionsList.get(0).get("transactions");

        List<Transaction> transactions = new ArrayList<>();

        for (DBObject transactionDBObject : (List<DBObject>) rawTransactionsObject) {
            transactions.add(mongoTemplate.getConverter().read(Transaction.class, transactionDBObject));
        }

        return transactions;
    }


    /**
     * Add transaction to DB
     *
     * @param transactionAddContainer
     * @return
     */
    public WriteResult addTransaction(String login, TransactionAddContainer transactionAddContainer) {
        Update update = new Update();
        Query addTransactionQuery = new Query(Criteria.where("login").is(login));

        update.push("transactions", transactionAddContainer.getTransaction());

        return mongoTemplate.updateFirst(addTransactionQuery, update, PersonTransactions.class);
    }

    /**
     * Update Transaction in DB
     *
     * @param transactionUpdateContainer
     * @return
     */
    public WriteResult updateTransaction(String login, TransactionUpdateContainer transactionUpdateContainer) {
        Query findUpdatedTransactionQuery = Query.query(
                Criteria.where("login").is(login)
                        .and("transactions._id").is(transactionUpdateContainer.getId()));

        return mongoTemplate.updateMulti(findUpdatedTransactionQuery,
                new Update().set("transactions.$", transactionUpdateContainer.getTransaction()), PersonTransactions.class);
    }

    /**
     * Delete Transaction from DB
     *
     * @param transactionDeleteContainer
     * @return
     */
    public WriteResult deleteTransaction(String login, TransactionDeleteContainer transactionDeleteContainer) {
        Query getPersonTransactionsQuery = Query.query(Criteria.where("login").is(login));
        Query getTransactionQuery = Query.query(Criteria.where("_id").is(transactionDeleteContainer.getId()));

        return mongoTemplate.updateFirst(getPersonTransactionsQuery,
                new Update().pull("transactions", getTransactionQuery), "Transactions");
    }
}
