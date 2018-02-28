package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import com.mongodb.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.api.TransactionsAPIService;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.TransactionsDBConnection;

import java.util.List;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.*;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateTransactionID;

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
     * @param searchContainer - TransactionsSearchContainer
     * @return response object with list of Transactions
     */
    @PostMapping(value = "/getTransactions")
    public AjaxRs<List<Transaction>> getTransactions(@RequestBody TransactionsSearchContainer searchContainer) {

        RequestValidation<List<Transaction>> requestValidation = new Validator(searchContainer, "Getting Transactions")
                .addValidation(() -> repository.existsByAccess_Login(formatLogin(searchContainer.getLogin())),
                        () -> fillLog(NO_PERSON_EXIST, searchContainer.getLogin()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        List<Transaction> transactions = transactionsDBConnection.getTransactions(searchContainer);

        log.debug("Returning Transactions for login {}, dateFrom {}, dateTo {} : {}",
                searchContainer.getLogin(), searchContainer.getRangeFrom(), searchContainer.getRangeTo(), transactions);

        return responseSuccess(TRANSACTIONS_RETURNED, transactions);
    }

    @PostMapping(value = "/addTransaction")
    public AjaxRs<Transaction> addTransaction(@RequestBody TransactionAddContainer addContainer) {

        RequestValidation<Transaction> requestValidation = new Validator(addContainer, "Getting Transactions")
                .addValidation(() -> repository.existsByAccess_Login(formatLogin(addContainer.getLogin())),
                        () -> fillLog(NO_PERSON_EXIST, addContainer.getLogin()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        generateTransactionID(addContainer.getTransaction());

        WriteResult writeResult = transactionsDBConnection.addTransaction(addContainer);

        if (writeResult.wasAcknowledged()) {
            log.debug("Saved new Transaction for login {} : {}", addContainer.getLogin(), addContainer.getTransaction());
            return responseSuccess(TRANSACTION_SAVED, addContainer.getTransaction());

        } else {
            log.error("Saving Transaction {} for login {} has failed", addContainer.getTransaction(), addContainer.getLogin());
            return ControllerUtils.responseError(TRANSACTION_SAVING_ERROR);
        }
    }

    /**
     * Update Person's Transaction
     * <p>
     * id field in Income Transaction object will be ignored and overwritten with given transactionID
     *
     * @param updateContainer
     * @return
     */

    @PostMapping(value = "/updateTransaction")
    public AjaxRs<Transaction> updateTransaction(@RequestBody TransactionUpdateContainer updateContainer) {

        RequestValidation<Transaction> requestValidation = new Validator(updateContainer, "Updating Transaction")
                .addValidation(() -> repository.existsByAccess_Login(formatLogin(updateContainer.getLogin())),
                        () -> fillLog(NO_PERSON_EXIST, updateContainer.getLogin()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        Transaction transactionToUpdate = generateTransactionID(updateContainer.getTransaction(), updateContainer.getId());

        updateContainer.setTransaction(transactionToUpdate);

        WriteResult updateResult = transactionsDBConnection.updateTransaction(updateContainer);

        // TODO: 07.02.2018 Find out if there are more reliable ways of checking deletion success

        if (updateResult.getN() == 0) {
            log.error("Updating Transaction for login {} has failed", updateContainer.getLogin());
            return ControllerUtils.responseError("Transaction was not updated!");
        }

        log.debug("Updated Transaction {}: for login: {}", updateContainer.getTransaction());
        return responseSuccess(TRANSACTION_UPDATED, updateContainer.getTransaction());
    }

    @PostMapping(value = "/deleteTransaction")
    public AjaxRs<Void> deleteTransaction(@RequestBody TransactionDeleteContainer deleteContainer) {

        RequestValidation<Void> requestValidation = new Validator(deleteContainer, "Deleting Transaction")
                .addValidation(() -> repository.existsByAccess_Login(formatLogin(deleteContainer.getLogin())),
                        () -> fillLog(NO_PERSON_EXIST, deleteContainer.getLogin()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();


        WriteResult deleteResult = transactionsDBConnection.deleteTransaction(deleteContainer);

        // TODO: 07.02.2018 Find out if there are more reliable ways of checking deletion success

        if (deleteResult.getN() == 0) {
            log.error("Deleting Transaction for login {} has failed", deleteContainer.getLogin());
            return ControllerUtils.responseError("Transaction was not deleted!");
        }
        log.debug("Deleted Transaction id {}: for login: {}", deleteContainer.getId(), deleteContainer.getLogin());
        // FIXME: 06.02.2018 some payload should be returned
        return responseSuccess(TRANSACTION_DELETED, null);
    }
}