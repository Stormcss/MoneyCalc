package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces;

import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.entities.Transaction;

import java.util.List;

public interface TransactionsService {

    Transaction getTransactionById(String login, Long transactionId);

    List<Transaction> getTransactions(String login, TransactionsSearchFilter getContainer);
//    List<Transaction> getTransactions(String login, LocalDate dateFrom, LocalDate dateTo, List<Integer> sectionIds);

//    List<Transaction> getTransactions(String login);

    Long addTransaction(Long userId, Transaction transaction);

    boolean updateTransaction(String login, Transaction transaction);

    boolean deleteTransaction(String login, Long transactionId);
}
