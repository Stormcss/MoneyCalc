package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection;

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
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils;
import ru.strcss.projects.moneycalc.moneycalcserver.mongo.PersonTransactionsRepository;

import java.time.LocalDate;
import java.util.List;

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
    public List<Transaction> getTransactions(String login, TransactionsSearchContainer container) {

        LocalDate rangeFrom = ControllerUtils.formatDateFromString(container.getRangeFrom());
        LocalDate rangeTo = ControllerUtils.formatDateFromString(container.getRangeTo());

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
