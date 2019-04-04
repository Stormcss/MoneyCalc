package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions.IncorrectRequestException;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.PersonService;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SpendingSectionService;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.TransactionsService;

import java.util.List;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.DATE_SEQUENCE_INCORRECT;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.NO_PERSON_LOGIN_EXISTS;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_ID_NOT_EXISTS;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.TRANSACTION_INCORRECT;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.TRANSACTION_NOT_DELETED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.TRANSACTION_NOT_FOUND;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.TRANSACTION_NOT_UPDATED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.TRANSACTION_SAVING_ERROR;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.fillDefaultValues;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.fillLog;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.ValidationUtils.isDateSequenceValid;

@Timed
@Slf4j
@RestController
@RequestMapping("/api/transactions")
@AllArgsConstructor
public class TransactionsController implements AbstractController {

    private TransactionsService transactionsService;
    private PersonService personService;
    private SpendingSectionService spendingSectionService;

    /**
     * Get filtered list of Transactions by user's login
     * Returned transactions are filtered by dates range being set at Settings
     *
     * @return response object with list of Transactions
     */
    @GetMapping
    public TransactionsSearchRs getTransactions() throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        TransactionsSearchRs transactions = transactionsService.getTransactions(login, null, true);

        log.info("Returning Transactions for login '{}' - {}", login, transactions);

        return transactions;
    }

    /**
     * Get filtered list of Transactions by user's login - return transactions only for specific dates range and Ids
     *
     * @param getFilter - TransactionsSearchFilter
     * @return response object with list of Transactions
     */
    @PostMapping(value = "/getFiltered")
    public TransactionsSearchRs getTransactions(@RequestBody TransactionsSearchFilter getFilter) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<List<Transaction>> requestValidation = new Validator(getFilter, "Getting Transactions")
                .addValidation(() -> isDateSequenceValid(getFilter.getDateFrom(), getFilter.getDateTo()),
                        () -> DATE_SEQUENCE_INCORRECT)
                .validate();
        if (!requestValidation.isValid())
            throw new IncorrectRequestException(requestValidation.getReason());

        TransactionsSearchRs transactions = transactionsService.getTransactions(login, getFilter, true);

        log.info("Returning Transactions for login '{}', applying Filter: {} - {}", login, getFilter, transactions);

        return transactions;
    }

    @PostMapping
    public Transaction addTransaction(@RequestBody Transaction transaction) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        log.debug("New transaction for login '{}' is received: {}", login, transaction);

        Long userId = personService.getUserIdByLogin(login);

        if (userId == null)
            throw new IncorrectRequestException(fillLog(NO_PERSON_LOGIN_EXISTS, login));

        RequestValidation<Transaction> requestValidation = new Validator(transaction, "Adding Transaction")
                .addValidation(() -> transaction.isValid().isValidated(),
                        () -> fillLog(TRANSACTION_INCORRECT, transaction.isValid().getReasons().toString()))
                .addValidation(() -> spendingSectionService.isSpendingSectionIdExists(login, transaction.getSectionId()),
                        () -> fillLog(SPENDING_SECTION_ID_NOT_EXISTS, String.valueOf(transaction.getSectionId())))
                .validate();
        if (!requestValidation.isValid())
            throw new IncorrectRequestException(requestValidation.getReason());

        fillDefaultValues(transaction);

        Long addedTransactionId = transactionsService.addTransaction(userId, transaction);

        if (addedTransactionId == null) {
            log.error("Saving Transaction {} for login '{}' has failed", transaction, login);
            throw new IncorrectRequestException(TRANSACTION_SAVING_ERROR);
        }
        log.info("Saved new Transaction for login '{}' : {}", login, transaction);
        return transaction;
    }

    /**
     * Update Person's Transaction
     * <p>
     * id field in Income Transaction object will be ignored and overwritten with given transactionID
     */
    @PutMapping
    public Transaction updateTransaction(@RequestBody TransactionUpdateContainer updateContainer) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<Transaction> requestValidation = new Validator(updateContainer, "Updating Transaction")
                .addValidation(() -> updateContainer.getTransaction().isValid().isValidated(),
                        () -> fillLog(TRANSACTION_INCORRECT, updateContainer.getTransaction().isValid().getReasons().toString()))
                // TODO: 17.02.2019 add validation for sectionId existence
                .validate();
        if (!requestValidation.isValid())
            throw new IncorrectRequestException(requestValidation.getReason());

        boolean isUpdateSuccessful = transactionsService.updateTransaction(login, updateContainer.getId(),
                updateContainer.getTransaction());

        if (!isUpdateSuccessful) {
            log.error("Updating Transaction for login \'{}\' has failed", login);
            throw new IncorrectRequestException(TRANSACTION_NOT_UPDATED);
        }

        Transaction resultTransaction = transactionsService.getTransactionById(login, updateContainer.getId());

        log.info("Updated Transaction {}: for login: \'{}\' with values: {}", resultTransaction, login, updateContainer.getTransaction());
        return resultTransaction;
    }

    @DeleteMapping(value = "/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Transaction deletedTransaction = transactionsService.getTransactionById(login, transactionId);

        if (deletedTransaction == null) {
            log.error("Transaction with id: \'{}\' was not found", transactionId);
            throw new IncorrectRequestException(TRANSACTION_NOT_FOUND);
        }

        boolean isDeleteSuccessful = transactionsService.deleteTransaction(login, transactionId);

        if (!isDeleteSuccessful) {
            log.error("Deleting Transaction for login \'{}\' has failed", login);
            throw new IncorrectRequestException(TRANSACTION_NOT_DELETED);
        }
        log.info("Deleted Transaction id \'{}\': for login: \'{}\'", transactionId, login);

        return ResponseEntity.ok(null);
    }
}