package ru.strcss.projects.moneycalcserver.dbconnection;

import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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
import ru.strcss.projects.moneycalcserver.mongo.PersonTransactionsRepository;

import java.time.LocalDate;
import java.util.List;

import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.formatDateFromString;

@Component
public class TransactionsDBConnection {

    private PersonTransactionsRepository personTransactionsRepository;
    private MongoTemplate mongoTemplate;

    @Autowired
    public TransactionsDBConnection(PersonTransactionsRepository personTransactionsRepository, MongoTemplate mongoTemplate) {
        this.personTransactionsRepository = personTransactionsRepository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Get Transactions List from DB layer
     *
     * @param container with search parameters
     * @return List of Transactions
     */
    public List<Transaction> getTransactions(TransactionsSearchContainer container) {

        String login = container.getLogin().replace("\"", "");

        LocalDate rangeFrom = formatDateFromString(container.getRangeFrom());
        LocalDate rangeTo = formatDateFromString(container.getRangeTo());

        return container.getRequiredSections().size() == 0 ?
                personTransactionsRepository.findTransactionsBetween(login, rangeFrom, rangeTo)
                : personTransactionsRepository.findTransactionsBetweenFilteredWithSection(login, rangeFrom, rangeTo, container.getRequiredSections());
    }


    /**
     * Add transaction to DB
     *
     * @param transactionAddContainer
     * @return
     */
    public WriteResult addTransaction(TransactionAddContainer transactionAddContainer) {
        Update update = new Update();
        Query addTransactionQuery = new Query(Criteria.where("login").is(transactionAddContainer.getLogin()));

        update.push("transactions", transactionAddContainer.getTransaction());

        return mongoTemplate.updateFirst(addTransactionQuery, update, PersonTransactions.class);
    }

    /**
     * Update Transaction in DB
     *
     * @param transactionUpdateContainer
     * @return
     */
    public WriteResult updateTransaction(TransactionUpdateContainer transactionUpdateContainer) {
        Query findUpdatedTransactionQuery = Query.query(
                Criteria.where("login").is(transactionUpdateContainer.getLogin()).and("transactions._id").is(transactionUpdateContainer.getId()));

        return mongoTemplate.updateMulti(findUpdatedTransactionQuery,
                new Update().set("transactions.$", transactionUpdateContainer.getTransaction()), PersonTransactions.class);
    }

    /**
     * Delete Transaction from DB
     *
     * @param transactionDeleteContainer
     * @return
     */
    public WriteResult deleteTransaction(TransactionDeleteContainer transactionDeleteContainer) {
        Query getPersonTransactionsQuery = Query.query(Criteria.where("login").is(transactionDeleteContainer.getLogin()));
        Query getTransactionQuery = Query.query(Criteria.where("_id").is(transactionDeleteContainer.getId()));

        return mongoTemplate.updateFirst(getPersonTransactionsQuery,
                new Update().pull("transactions", getTransactionQuery), "Transactions");
    }
}
