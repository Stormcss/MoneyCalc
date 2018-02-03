package ru.strcss.projects.moneycalcserver.controllers;

import com.mongodb.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalcserver.controllers.api.TransactionsAPIService;
import ru.strcss.projects.moneycalcserver.controllers.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.controllers.dto.TransactionContainer;
import ru.strcss.projects.moneycalcserver.controllers.dto.TransactionsSearchContainer;
import ru.strcss.projects.moneycalcserver.controllers.dto.ValidationResult;
import ru.strcss.projects.moneycalcserver.enitities.PersonTransactions;
import ru.strcss.projects.moneycalcserver.enitities.Transaction;
import ru.strcss.projects.moneycalcserver.mongo.PersonTransactionsRepository;

import java.time.LocalDate;
import java.util.List;

import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseError;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseSuccess;
import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.formatDateFromString;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ValidationUtils.validateTransactionContainer;

@Slf4j
@RestController
@RequestMapping("/api/finance/financeStats")
public class FinanceStatisticsController extends AbstractController implements TransactionsAPIService {

    @Autowired
    MongoTemplate mongoTemplate;

    private PersonTransactionsRepository personTransactionsRepository;

    @Autowired
    public FinanceStatisticsController(PersonTransactionsRepository personTransactionsRepository) {
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


    public AjaxRs addTransaction(@RequestBody TransactionContainer transactionContainer) {

        ValidationResult validationResult = validateTransactionContainer(transactionContainer);

        if (!validationResult.isValidated()) {
            log.error("Transaction validation has failed - required fields are empty: {}", validationResult.getReasons());
            return responseError("Required fields are empty: " + validationResult.getReasons());
        }

        Update update = new Update();
        Query query = new Query(Criteria.where("login").is(transactionContainer.getLogin()));

        update.push("transactions", transactionContainer.getTransaction());

        WriteResult writeResult = mongoTemplate.updateFirst(query, update, PersonTransactions.class);

        if (writeResult.wasAcknowledged()) {
            log.debug("Saved new Transaction for login {} : {}", transactionContainer.getLogin(), transactionContainer.getTransaction());
            return responseSuccess(TRANSACTION_SAVED, transactionContainer.getTransaction());

        } else {
            log.error("Saving Transaction for login {} has failed", transactionContainer.getLogin(), transactionContainer.getTransaction());
            return responseError(TRANSACTION_SAVING_ERROR);
        }
    }


//        FinanceStatistics financeStatistics = mongoOperations.findOne(query, FinanceStatistics.class, "FinanceStatistics");

//        if (financeStatistics != null) {
//
//            List<PersonTransactions> listOfIds = financeStatistics.getTransactions();
//
//            Query queryTransactions = new Query(where("_id").in(listOfIds));
//
//            List<Transaction> transactions = mongoOperations.find(queryTransactions, Transaction.class, "Transactions");
//
//            if (transactions != null) {
//                log.debug("returning Transactions for login {}: {}", login, transactions);
//                return responseSuccess(TRANSACTIONS_RETURNED, transactions);
//            } else {
//                log.error("Error returning Transactions for login {}", login);
//                return responseError("ERROR");
//            }
//        } else {
//            log.error("Can not return FinanceStatistics for login {} - no Person found", login);
//            return responseError(NO_PERSON_EXIST);
//        }


//    @Transactional
//    @PostMapping(value = "/getTransactions")
//    public AjaxRs getTransactions(@RequestBody String login) {
//
//        login = login.replace("\"","");
//
//        Query query = new Query(where("_id").is(login));
//        FinanceStatistics financeStatistics = mongoOperations.findOne(query, FinanceStatistics.class,"FinanceStatistics");
//
//        if (financeStatistics != null) {
//
//            List<List<Transaction>> listOfIds = financeStatistics.getTransactions();
//
//            Query queryTransactions = new Query(where("_id").in(listOfIds));
//
//            List<Transaction> transactions = mongoOperations.find(queryTransactions, Transaction.class, "Transactions");
//
//            if (transactions != null){
//                log.debug("returning Transactions for login {}: {}", login, transactions);
//                return responseSuccess(TRANSACTIONS_RETURNED, transactions);
//            } else {
//                log.error("Error returning Transactions for login {}", login);
//                return responseError("ERROR");
//            }
//        } else {
//            log.error("Can not return FinanceStatistics for login {} - no Person found", login);
//            return responseError(NO_PERSON_EXIST);
//        }
//    }
}
