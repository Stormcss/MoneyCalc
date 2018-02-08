package ru.strcss.projects.moneycalc.api;

import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionDeleteContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.util.List;

public interface TransactionsAPIService {

    AjaxRs<List<Transaction>> getTransactions(TransactionsSearchContainer container);

    AjaxRs<Transaction> addTransaction(TransactionContainer transactionContainer);

    AjaxRs<Transaction> updateTransaction(TransactionUpdateContainer transactionContainer);

    AjaxRs<Void> deleteTransaction(TransactionDeleteContainer transactionContainer);
}
