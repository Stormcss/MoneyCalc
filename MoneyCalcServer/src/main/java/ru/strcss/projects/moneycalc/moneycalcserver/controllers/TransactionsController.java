package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.strcss.projects.moneycalc.api.TransactionsAPIService;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.PersonService;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SpendingSectionService;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.TransactionsService;

import java.util.List;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.*;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.*;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.ValidationUtils.isDateSequenceValid;

@Timed
@Slf4j
@RestController
@RequestMapping("/api/transactions")
public class TransactionsController extends AbstractController implements TransactionsAPIService {

    private TransactionsService transactionsService;
    private PersonService personService;
    private SpendingSectionService spendingSectionService;

    public TransactionsController(TransactionsService transactionsService, SpendingSectionService spendingSectionService, PersonService personService) {
        this.transactionsService = transactionsService;
        this.spendingSectionService = spendingSectionService;
        this.personService = personService;
    }

    // TODO: 26.08.2018 test me

    /**
     * Get filtered list of Transactions by user's login
     * Returned transactions are filtered by dates range at Settings and active
     *
     * @return response object with list of Transactions
     */
    @GetMapping
    public ResponseEntity<MoneyCalcRs<List<Transaction>>> getTransactions() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        List<Transaction> transactions = transactionsService.getTransactions(login, null);

        log.info("Returning Transactions for login \'{}\'", login);

        return responseSuccess(TRANSACTIONS_RETURNED, transactions);
    }

    /**
     * Get filtered list of Transactions by user's login - return transactions only for specific dates range and Ids
     *
     * @param getFilter - TransactionsSearchFilter
     * @return response object with list of Transactions
     */
    @PostMapping(value = "/getFiltered")
    public ResponseEntity<MoneyCalcRs<List<Transaction>>> getTransactions(@RequestBody TransactionsSearchFilter getFilter) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<List<Transaction>> requestValidation = new Validator(getFilter, "Getting Transactions")
                .addValidation(() -> isDateSequenceValid(getFilter.getDateFrom(), getFilter.getDateTo()),
                        () -> DATE_SEQUENCE_INCORRECT)
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        List<Transaction> transactions = transactionsService.getTransactions(login, getFilter);

        log.info("Returning Transactions for login '{}', applying Filter: {} - {}", login, getFilter, transactions);

        return responseSuccess(TRANSACTIONS_RETURNED, transactions);
    }

    @PostMapping
    public ResponseEntity<MoneyCalcRs<Transaction>> addTransaction(@RequestBody TransactionAddContainer addContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        log.debug("addTransaction Request received {}", addContainer);

        Long userId = personService.getUserIdByLogin(login);

        if (userId == null)
            return responseError(fillLog(NO_PERSON_LOGIN_EXISTS, login));

        RequestValidation<Transaction> requestValidation = new Validator(addContainer, "Adding Transactions")
                .addValidation(() -> addContainer.getTransaction().isValid().isValidated(),
                        () -> fillLog(TRANSACTION_INCORRECT, addContainer.getTransaction().isValid().getReasons().toString()))
                .addValidation(() -> spendingSectionService.isSpendingSectionIdExists(login, addContainer.getTransaction().getSectionId()),
                        () -> fillLog(SPENDING_SECTION_ID_NOT_EXISTS, "" + addContainer.getTransaction().getSectionId()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        fillDefaultValues(addContainer.getTransaction());

        Long addedTransactionId = transactionsService.addTransaction(userId, addContainer.getTransaction());

        if (addedTransactionId == null) {
            log.error("Saving Transaction {} for login '{}' has failed", addContainer.getTransaction(), login);
            return responseError(TRANSACTION_SAVING_ERROR);
        }
        log.info("Saved new Transaction for login '{}' : {}", login, addContainer.getTransaction());
        return responseSuccess(TRANSACTION_SAVED, addContainer.getTransaction());
    }

    /**
     * Update Person's Transaction
     * <p>
     * id field in Income Transaction object will be ignored and overwritten with given transactionID
     *
     * @param updateContainer
     * @return
     */

    @PutMapping
    public ResponseEntity<MoneyCalcRs<Transaction>> updateTransaction(@RequestBody TransactionUpdateContainer updateContainer) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<Transaction> requestValidation = new Validator(updateContainer, "Updating Transaction")
                .addValidation(() -> updateContainer.getTransaction().isValid().isValidated(),
                        () -> fillLog(TRANSACTION_INCORRECT, updateContainer.getTransaction().isValid().getReasons().toString()))
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        boolean isUpdateSuccessful = transactionsService.updateTransaction(login, updateContainer.getId(),
                updateContainer.getTransaction());

        if (!isUpdateSuccessful) {
            log.error("Updating Transaction for login \'{}\' has failed", login);
            return responseError(TRANSACTION_UPDATED);
        }

        Transaction resultTransaction = transactionsService.getTransactionById(login, updateContainer.getId());

        log.info("Updated Transaction {}: for login: \'{}\' with values: {}", resultTransaction, login, updateContainer.getTransaction());
        return responseSuccess(TRANSACTION_UPDATED, resultTransaction);
    }

    @DeleteMapping(value = "/{transactionId}")
    public ResponseEntity<MoneyCalcRs<Void>> deleteTransaction(@PathVariable Long transactionId) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();


        Transaction deletedTransaction = transactionsService.getTransactionById(login, transactionId);

        if (deletedTransaction == null) {
            log.error("Transaction with id: \'{}\' was not found", transactionId);
            return responseError(TRANSACTION_NOT_FOUND);
        }

        boolean isDeleteSuccessful = transactionsService.deleteTransaction(login, transactionId);

        if (!isDeleteSuccessful) {
            log.error("Deleting Transaction for login \'{}\' has failed", login);
            return responseError("Transaction was not deleted!");
        }
        log.info("Deleted Transaction id \'{}\': for login: \'{}\'", transactionId, login);

        return responseSuccess(TRANSACTION_DELETED, null);
    }
}