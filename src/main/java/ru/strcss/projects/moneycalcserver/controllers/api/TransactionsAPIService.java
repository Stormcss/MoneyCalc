package ru.strcss.projects.moneycalcserver.controllers.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.strcss.projects.moneycalcserver.controllers.dto.AjaxRs;
import ru.strcss.projects.moneycalcserver.controllers.dto.TransactionContainer;
import ru.strcss.projects.moneycalcserver.controllers.dto.TransactionsSearchContainer;

public interface TransactionsAPIService {

    @PostMapping(value = "/getTransactions")
    AjaxRs getTransactions(@RequestBody TransactionsSearchContainer container);

    @PostMapping(value = "/addTransaction")
    AjaxRs addTransaction(@RequestBody TransactionContainer transactionContainer);
}
