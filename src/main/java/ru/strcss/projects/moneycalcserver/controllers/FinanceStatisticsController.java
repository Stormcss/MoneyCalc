package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.FinanceStatistics;
import ru.strcss.projects.moneycalcserver.enitities.dto.Transaction;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static ru.strcss.projects.moneycalcserver.controllers.Utils.ControllerUtils.responseError;
import static ru.strcss.projects.moneycalcserver.controllers.Utils.ControllerUtils.responseSuccess;

@Slf4j
@RestController
@RequestMapping("/api/finance/financeStats")
public class FinanceStatisticsController extends AbstractController {

    /**
     * Get list of Transactions by user's login
     *
     * @param login
     * @return response object with list of Transactions
     */

    @Transactional
    @PostMapping(value = "/getFinanceStats")
    public AjaxRs getFinanceStats(@RequestBody String login) {

        login = login.replace("\"","");

        Query query = new Query(where("_id").is(login));
        FinanceStatistics financeStatistics = mongoOperations.findOne(query, FinanceStatistics.class,"FinanceStatistics");

        if (financeStatistics != null) {

            List<Transaction> listOfIds = financeStatistics.getTransactions();

            Query queryTransactions = new Query(where("_id").in(listOfIds));

            List<Transaction> transactions = mongoOperations.find(queryTransactions, Transaction.class, "Transactions");

            if (transactions != null){
                log.debug("returning Transactions for login {}: {}", login, transactions);
                return responseSuccess(RETURN_TRANSACTIONS, transactions);
            } else {
                log.error("Error returning Transactions for login {}", login);
                return responseError("ERROR");
            }
        } else {
            log.error("Can not return FinanceStatistics for login {} - no Person found", login);
            return responseError(NO_PERSON_EXIST);
        }
    }
}
