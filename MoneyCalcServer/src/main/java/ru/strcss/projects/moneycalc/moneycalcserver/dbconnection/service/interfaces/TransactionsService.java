package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces;

import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionsService {

    Transaction getTransactionById(Integer transactionId);

    List<Transaction> getTransactionsByPersonId(Integer personId, LocalDate dateFrom, LocalDate dateTo, List<Integer> sectionIds);

    List<Transaction> getTransactionsByLogin(String login, LocalDate dateFrom, LocalDate dateTo, List<Integer> sectionIds);

    Integer addTransaction(Integer personId, Transaction transaction);

    boolean updateTransaction(Transaction transaction);

    boolean deleteTransaction(Transaction transaction);
}
