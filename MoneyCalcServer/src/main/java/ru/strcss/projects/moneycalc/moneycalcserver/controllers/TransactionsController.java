package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import com.mongodb.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.api.TransactionsAPIService;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.SettingsDBConnection;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.TransactionsDBConnection;

import java.util.List;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.*;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.currentDateString;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateTransactionID;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.ValidationUtils.isDateSequenceValid;

@Slf4j
@RestController
@RequestMapping("/api/finance/transactions")
public class TransactionsController extends AbstractController implements TransactionsAPIService {

    private TransactionsDBConnection transactionsDBConnection;
    private SettingsDBConnection settingsDBConnection;

    public TransactionsController(TransactionsDBConnection transactionsDBConnection, SettingsDBConnection settingsDBConnection) {
        this.transactionsDBConnection = transactionsDBConnection;
        this.settingsDBConnection = settingsDBConnection;
    }

    /**
     * Get list of Transactions by user's login
     *
     * @param getContainer - TransactionsSearchContainer
     * @return response object with list of Transactions
     */
    @PostMapping(value = "/getTransactions")
    public ResponseEntity<MoneyCalcRs<List<Transaction>>> getTransactions(@RequestBody TransactionsSearchContainer getContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<List<Transaction>> requestValidation = new Validator(getContainer, "Getting Transactions")
                .addValidation(() -> isDateSequenceValid(getContainer.getRangeFrom(), getContainer.getRangeTo()),
                        () -> DATE_SEQUENCE_INCORRECT)
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        List<Transaction> transactions = sortTransactionList(transactionsDBConnection.getTransactions(login, getContainer));

        log.debug("Returning Transactions for login \"{}\", dateFrom {}, dateTo {} : {}",
                login, getContainer.getRangeFrom(), getContainer.getRangeTo(), transactions);

        return responseSuccess(TRANSACTIONS_RETURNED, transactions);
    }

    @PostMapping(value = "/addTransaction")
    public ResponseEntity<MoneyCalcRs<Transaction>> addTransaction(@RequestBody TransactionAddContainer addContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        System.out.println("addContainer = " + addContainer);

        RequestValidation<Transaction> requestValidation = new Validator(addContainer, "Adding Transactions")
                .addValidation(() -> settingsDBConnection.isSpendingSectionIDExists(login, addContainer.getTransaction().getSectionID()),
                        () -> fillLog(SPENDING_SECTION_ID_NOT_EXISTS, "" + addContainer.getTransaction().getSectionID()))
                .addValidation(() -> addContainer.getTransaction().isValid().isValidated(),
                        () -> fillLog(TRANSACTION_INCORRECT, addContainer.getTransaction().isValid().getReasons().toString()))

                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        generateTransactionID(addContainer.getTransaction());

        if (addContainer.getTransaction().getDate() == null)
            addContainer.getTransaction().setDate(currentDateString());

        WriteResult writeResult = transactionsDBConnection.addTransaction(login, addContainer);

        if (writeResult.wasAcknowledged()) {
            log.debug("Saved new Transaction for login \"{}\" : {}", login, addContainer.getTransaction());
            return responseSuccess(TRANSACTION_SAVED, addContainer.getTransaction());

        } else {
            log.error("Saving Transaction {} for login \"{}\" has failed", addContainer.getTransaction(), login);
            return responseError(TRANSACTION_SAVING_ERROR);
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
    public ResponseEntity<MoneyCalcRs<Transaction>> updateTransaction(@RequestBody TransactionUpdateContainer updateContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<Transaction> requestValidation = new Validator(updateContainer, "Updating Transaction")
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        Transaction transactionToUpdate = generateTransactionID(updateContainer.getTransaction(), updateContainer.getId());

        updateContainer.setTransaction(transactionToUpdate);

        WriteResult updateResult = transactionsDBConnection.updateTransaction(login, updateContainer);

        // TODO: 07.02.2018 Find out if there are more reliable ways of checking deletion success

        if (updateResult.getN() == 0) {
            log.error("Updating Transaction for login \"{}\" has failed", login);
            return responseError("Transaction was not updated!");
        }

        log.debug("Updated Transaction {}: for login: \"{}\"", updateContainer.getTransaction());
        return responseSuccess(TRANSACTION_UPDATED, updateContainer.getTransaction());
    }

    @PostMapping(value = "/deleteTransaction")
    public ResponseEntity<MoneyCalcRs<Void>> deleteTransaction(@RequestBody TransactionDeleteContainer deleteContainer) {

        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<Void> requestValidation = new Validator(deleteContainer, "Deleting Transaction")
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        // TODO: 06.03.2018 Check if transaction exists

        WriteResult deleteResult = transactionsDBConnection.deleteTransaction(login, deleteContainer);

        // TODO: 07.02.2018 Find out if there are more reliable ways of checking deletion success

        if (deleteResult.getN() == 0) {
            log.error("Deleting Transaction for login \"{}\" has failed", login);
            return responseError("Transaction was not deleted!");
        }
        log.debug("Deleted Transaction id \"{}\": for login: \"{}\"", deleteContainer.getId(), login);
        // FIXME: 06.02.2018 some payload should be returned
        return responseSuccess(TRANSACTION_DELETED, null);
    }
}