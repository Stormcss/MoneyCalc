package ru.strcss.projects.moneycalcserver.controllers;

import com.mongodb.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.api.TransactionsAPIService;
import ru.strcss.projects.moneycalc.dto.*;
import ru.strcss.projects.moneycalc.enitities.PersonTransactions;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalcserver.mongo.PersonTransactionsRepository;

import java.time.LocalDate;
import java.util.List;

import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseError;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseSuccess;
import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.formatDateFromString;
import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.generateTransactionID;

@Slf4j
@RestController
@RequestMapping("/api/finance/financeStats")
public class TransactionsController extends AbstractController implements TransactionsAPIService {

    @Autowired
    MongoTemplate mongoTemplate;

    private PersonTransactionsRepository personTransactionsRepository;

    @Autowired
    public TransactionsController(PersonTransactionsRepository personTransactionsRepository) {
        this.personTransactionsRepository = personTransactionsRepository;
    }

    /**
     * Get list of Transactions by user's login
     *
     * @param container - TransactionsSearchContainer with following fields:
     *                  login - Person's login with searched transactions
     *                  rangeFrom - starting Date of range from which transactions should be received
     *                  rangeTo - ending Date of range from which transactions should be recieved
     * @return response object with list of Transactions
     */
    @PostMapping(value = "/getTransactions")
    public AjaxRs getTransactions(@RequestBody TransactionsSearchContainer container) {

        ValidationResult validationResult = container.isValid();

        if (!validationResult.isValidated()) {
            log.error("Transaction validation has failed - required fields are empty: {}", validationResult.getReasons());
            return responseError("Required fields are empty: " + validationResult.getReasons());
        }

        String login = container.getLogin().replace("\"", "");

        LocalDate rangeFrom = formatDateFromString(container.getRangeFrom());
        LocalDate rangeTo = formatDateFromString(container.getRangeTo());

        List<Transaction> transactions = personTransactionsRepository.findTransactionsBetween(login, rangeFrom, rangeTo);

        log.debug("Returning Transactions for login {}, dateFrom {}, dateTo {} : {}", login, rangeFrom, rangeTo, transactions);

        return responseSuccess(TRANSACTIONS_RETURNED, transactions);
    }

    @PostMapping(value = "/addTransaction")
    public AjaxRs addTransaction(@RequestBody TransactionContainer transactionContainer) {

        ValidationResult validationResult = transactionContainer.isValid();

        if (!validationResult.isValidated()) {
            log.error("TransactionContainer validation has failed - required fields are empty: {}", validationResult.getReasons());
            return responseError("Required fields are empty: " + validationResult.getReasons());
        }

        Transaction savedTransaction = generateTransactionID(transactionContainer.getTransaction());

        Update update = new Update();
        Query addTransactionQuery = new Query(Criteria.where("login").is(transactionContainer.getLogin()));

        update.push("transactions", savedTransaction);

        WriteResult writeResult = mongoTemplate.updateFirst(addTransactionQuery, update, PersonTransactions.class);

        // TODO: 05.02.2018 Statistics recalculation

        if (writeResult.wasAcknowledged()) {
            log.debug("Saved new Transaction for login {} : {}", transactionContainer.getLogin(), savedTransaction);
            return responseSuccess(TRANSACTION_SAVED, savedTransaction);

        } else {
            log.error("Saving Transaction {} for login {} has failed", savedTransaction, transactionContainer.getLogin());
            return responseError(TRANSACTION_SAVING_ERROR);
        }
    }

    /**
     * Update Person's Transaction
     *
//     * Income transaction must have the same _id as updated transaction, otherwise updated transaction won't be found

     * id field in Income Transaction object will be ignored and overwritten with given transactionID
     *
     * @param transactionContainer
     * @return
     */

    @PostMapping(value = "/updateTransaction")
    public AjaxRs updateTransaction(@RequestBody TransactionUpdateContainer transactionContainer) {

        ValidationResult validationResult = transactionContainer.isValid();

        if (!validationResult.isValidated()) {
            log.error("TransactionUpdateContainer validation has failed - required fields are empty: {}", validationResult.getReasons());
            return responseError("Required fields are empty: " + validationResult.getReasons());
        }

        Transaction transactionToUpdate = generateTransactionID(transactionContainer.getTransaction(), transactionContainer.getId());

        Query findUpdatedTransactionQuery = Query.query(Criteria.where("login").is(transactionContainer.getLogin()))
                .addCriteria(Criteria.where("_id").is(transactionContainer.getTransaction().get_id()));

//        WriteResult deleteResult = mongoTemplate.updateFirst(findUpdatedTransactionQuery,
//                new Update().set("transactions", getTransactionQuery), "Transactions");

        WriteResult updateResult = mongoTemplate.updateFirst(findUpdatedTransactionQuery,
                new Update().set("transactions", transactionToUpdate), "Transactions");

        // FIXME: 07.02.2018 FIX Updating

//        WriteResult updateResult = mongoTemplate.updateMulti(
//                new Query(Criteria.where("login").is(transactionContainer.getLogin())),
//                new Update().set("transactions.$", transactionContainer.getTransaction()),
//                PersonTransactions.class
//        );

        // TODO: 07.02.2018 Find out if there are more reliable ways of checking deletion success

        log.debug("updateResult is {}", updateResult);
        if (updateResult.getN() == 0) {
            log.error("Updating Transaction for login {} has failed - ", transactionContainer.getLogin());
            return responseError("Transaction was not updated!");
        }
        // TODO: 05.02.2018 Statistics recalculation

        log.debug("Updated Transaction {}: for login: {}", transactionToUpdate, transactionContainer.getLogin());
        return responseSuccess(TRANSACTION_UPDATED, transactionToUpdate);
    }

    @PostMapping(value = "/deleteTransaction")
    public AjaxRs deleteTransaction(@RequestBody TransactionDeleteContainer transactionContainer) {

        ValidationResult validationResult = transactionContainer.isValid();

        if (!validationResult.isValidated()) {
            log.error("TransactionUpdateContainer validation has failed - required fields are empty: {}", validationResult.getReasons());
            return responseError("Required fields are empty: " + validationResult.getReasons());
        }

        Query getPersonTransactionsQuery = Query.query(Criteria.where("login").is(transactionContainer.getLogin()));
        Query getTransactionQuery = Query.query(Criteria.where("_id").is(transactionContainer.getId()));

        WriteResult deleteResult = mongoTemplate.updateFirst(getPersonTransactionsQuery,
                new Update().pull("transactions", getTransactionQuery), "Transactions");

        // TODO: 07.02.2018 Find out if there are more reliable ways of checking deletion success

        if (deleteResult.getN() == 0) {
            log.error("Deleting Transaction for login {} has failed - ", transactionContainer.getLogin());
            return responseError("Transaction was not deleted!");
        }
        // TODO: 05.02.2018 Statistics recalculation

        log.debug("Deleted Transaction id {}: for login: {}", transactionContainer.getId(), transactionContainer.getLogin());
        // FIXME: 06.02.2018 some payload should be returned
        return responseSuccess(TRANSACTION_DELETED, null);
    }
}
