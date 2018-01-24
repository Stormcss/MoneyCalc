package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalcserver.controllers.Utils.ControllerUtils;
import ru.strcss.projects.moneycalcserver.enitities.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.enitities.dto.FinanceStatistics;
import ru.strcss.projects.moneycalcserver.enitities.dto.Transaction;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

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

    @PostMapping(value = "/getFinanceStats")
    public AjaxRs getFinanceStats(@RequestBody String login) {

        login = login.replace("\"","");


        // TODO: 20.01.2018 Find out if there are ways to get rid of unnecessary find request to db

        Query query = new Query(where("_id").is(login));
        FinanceStatistics financeStatistics = mongoOperations.findOne(query, FinanceStatistics.class,"FinanceStatistics");

        if (financeStatistics != null) {

            List<Transaction> listOfIds = financeStatistics.getTransactions();

            Query queryTransactions = new Query(where("_id").in(listOfIds));

            List<Transaction> transactions = mongoOperations.find(queryTransactions, Transaction.class, "Transactions");

            if (transactions != null){
                log.debug("returning Transactions for login {}: {}", login, transactions);
                return ControllerUtils.responseSuccess(RETURN_TRANSACTIONS, transactions);
            } else {
                log.error("Error returning Transactions for login {}", login);
                return ControllerUtils.responseError("ERROR");
            }
        } else {
            log.error("Can not return FinanceStatistics for login {} - no Person found", login);
            return ControllerUtils.responseError(NO_PERSON_EXIST);
        }
    }
}
