package ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces;

import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;

public interface TransactionsService {

    Transaction getTransactionById(String login, Long transactionId);

    TransactionsSearchRs getTransactions(String login, TransactionsSearchFilter getContainer, boolean isStatsRequired) throws Exception;

    Long addTransaction(Long userId, Transaction transaction) throws Exception;

    boolean updateTransaction(String login, Long transactionId, Transaction transaction) throws Exception;

    boolean deleteTransaction(String login, Long transactionId) throws Exception;
}
