package ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces;

import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.entities.Transaction;

import java.util.List;

public interface TransactionsService {

    Transaction getTransactionById(String login, Long transactionId);

    List<Transaction> getTransactions(String login, TransactionsSearchFilter getContainer);

    Long addTransaction(Long userId, Transaction transaction);

    boolean updateTransaction(String login, Long transactionId, Transaction transaction);

    boolean deleteTransaction(String login, Long transactionId);
}
