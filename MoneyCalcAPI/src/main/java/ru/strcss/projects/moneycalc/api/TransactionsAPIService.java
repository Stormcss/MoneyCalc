package ru.strcss.projects.moneycalc.api;

import ru.strcss.projects.moneycalc.dto.*;

public interface TransactionsAPIService {

    AjaxRs getTransactions(TransactionsSearchContainer container);

    AjaxRs addTransaction(TransactionContainer transactionContainer);

    AjaxRs updateTransaction(TransactionUpdateContainer transactionContainer);

    AjaxRs deleteTransaction(TransactionDeleteContainer transactionContainer);
}
