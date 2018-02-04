package ru.strcss.projects.moneycalc.api;

import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.TransactionContainer;
import ru.strcss.projects.moneycalc.dto.TransactionsSearchContainer;

public interface TransactionsAPIService {


    AjaxRs getTransactions(TransactionsSearchContainer container);

    AjaxRs addTransaction(TransactionContainer transactionContainer);
}
