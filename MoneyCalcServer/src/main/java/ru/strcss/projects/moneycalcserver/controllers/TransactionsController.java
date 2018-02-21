package ru.strcss.projects.moneycalcserver.controllers;

import com.mongodb.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.api.TransactionsAPIService;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalcserver.dbconnection.TransactionsDBConnection;

import java.util.List;

import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseError;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseSuccess;
import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.generateTransactionID;

@Slf4j
@RestController
@RequestMapping("/api/finance/transactions")
public class TransactionsController extends AbstractController implements TransactionsAPIService {

    private TransactionsDBConnection transactionsDBConnection;

    @Autowired
    public TransactionsController(TransactionsDBConnection transactionsDBConnection) {
        this.transactionsDBConnection = transactionsDBConnection;
    }

    /**
     * Get list of Transactions by user's login
     *
     * @param container - TransactionsSearchContainer
     * @return response object with list of Transactions
     */
    @PostMapping(value = "/getTransactions")
    public AjaxRs<List<Transaction>> getTransactions(@RequestBody TransactionsSearchContainer container) {

        ValidationResult validationResult = container.isValid();

        if (!isPersonExist(container)){
            log.error("Person with login {} does not exist!", container.getLogin());
            return responseError(NO_PERSON_EXIST);
        }

        if (!validationResult.isValidated()) {
            log.error("Transaction validation has failed - required fields are incorrect: {}", validationResult.getReasons());
            return responseError("Required fields are incorrect: " + validationResult.getReasons());
        }

        List<Transaction> transactions = transactionsDBConnection.getTransactions(container);

        log.debug("Returning Transactions for login {}, dateFrom {}, dateTo {} : {}", container.getLogin(), container.getRangeFrom(), container.getRangeTo(), transactions);

        return responseSuccess(TRANSACTIONS_RETURNED, transactions);
    }

    @PostMapping(value = "/addTransaction")
    public AjaxRs<Transaction> addTransaction(@RequestBody TransactionAddContainer transactionAddContainer) {

        ValidationResult validationResult = transactionAddContainer.isValid();

        if (!isPersonExist(transactionAddContainer)){
            log.error("Person with login {} does not exist!", transactionAddContainer.getLogin());
            return responseError(NO_PERSON_EXIST);
        }

        if (!validationResult.isValidated()) {
            log.error("TransactionContainer validation has failed - required fields are incorrect: {}", validationResult.getReasons());
            return responseError("Required fields are incorrect: " + validationResult.getReasons());
        }

        generateTransactionID(transactionAddContainer.getTransaction());

        WriteResult writeResult = transactionsDBConnection.addTransaction(transactionAddContainer);

        if (writeResult.wasAcknowledged()) {
            // TODO: 08.02.2018 TRANSACTIONS REQUIRED

            log.debug("Saved new Transaction for login {} : {}", transactionAddContainer.getLogin(), transactionAddContainer.getTransaction());
            return responseSuccess(TRANSACTION_SAVED, transactionAddContainer.getTransaction());

        } else {
            log.error("Saving Transaction {} for login {} has failed", transactionAddContainer.getTransaction(), transactionAddContainer.getLogin());
            return responseError(TRANSACTION_SAVING_ERROR);
        }
    }

    /**
     * Update Person's Transaction
     * <p>
     * id field in Income Transaction object will be ignored and overwritten with given transactionID
     *
     * @param transactionContainer
     * @return
     */

    @PostMapping(value = "/updateTransaction")
    public AjaxRs<Transaction> updateTransaction(@RequestBody TransactionUpdateContainer transactionContainer) {

        ValidationResult validationResult = transactionContainer.isValid();

        if (!isPersonExist(transactionContainer)){
            log.error("Person with login {} does not exist!", transactionContainer.getLogin());
            return responseError(NO_PERSON_EXIST);
        }

        if (!validationResult.isValidated()) {
            log.error("TransactionUpdateContainer validation has failed - required fields are incorrect: {}", validationResult.getReasons());
            return responseError("Required fields are incorrect: " + validationResult.getReasons());
        }

        Transaction transactionToUpdate = generateTransactionID(transactionContainer.getTransaction(), transactionContainer.getId());

        transactionContainer.setTransaction(transactionToUpdate);

        WriteResult updateResult = transactionsDBConnection.updateTransaction(transactionContainer);

        // TODO: 07.02.2018 Find out if there are more reliable ways of checking deletion success

        if (updateResult.getN() == 0) {
            log.error("Updating Transaction for login {} has failed", transactionContainer.getLogin());
            return responseError("Transaction was not updated!");
        }

        log.debug("Updated Transaction {}: for login: {}", transactionContainer.getTransaction());
        return responseSuccess(TRANSACTION_UPDATED, transactionContainer.getTransaction());
    }

    @PostMapping(value = "/deleteTransaction")
    public AjaxRs<Void> deleteTransaction(@RequestBody TransactionDeleteContainer transactionContainer) {

        ValidationResult validationResult = transactionContainer.isValid();

        if (!isPersonExist(transactionContainer)){
            log.error("Person with login {} does not exist!", transactionContainer.getLogin());
            return responseError(NO_PERSON_EXIST);
        }

        if (!validationResult.isValidated()) {
            log.error("TransactionUpdateContainer validation has failed - required fields are incorrect: {}", validationResult.getReasons());
            return responseError("Required fields are incorrect: " + validationResult.getReasons());
        }

        WriteResult deleteResult = transactionsDBConnection.deleteTransaction(transactionContainer);

        // TODO: 07.02.2018 Find out if there are more reliable ways of checking deletion success

        if (deleteResult.getN() == 0) {
            log.error("Deleting Transaction for login {} has failed", transactionContainer.getLogin());
            return responseError("Transaction was not deleted!");
        }
        log.debug("Deleted Transaction id {}: for login: {}", transactionContainer.getId(), transactionContainer.getLogin());
        // FIXME: 06.02.2018 some payload should be returned
        return responseSuccess(TRANSACTION_DELETED, null);
    }
}