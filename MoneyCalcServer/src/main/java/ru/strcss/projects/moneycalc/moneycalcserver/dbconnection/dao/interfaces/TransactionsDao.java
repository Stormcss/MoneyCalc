//package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces;
//
//import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchFilter;
//import ru.strcss.projects.moneycalc.entities.Transaction;
//
//import java.time.LocalDate;
//import java.util.List;
//
//public interface TransactionsDao {
//
//    Transaction getTransactionById(Integer transactionId);
//
//    List<Transaction> getTransactionsByPersonId(Integer userId, LocalDate dateFrom, LocalDate dateTo, List<Integer> sectionIds);
//
//    List<Transaction> getTransactions(String login, TransactionsSearchFilter getContainer);
//
//    List<Transaction> getTransactions(String login);
//
//    Integer addTransaction(Integer userId, Transaction transaction);
//
//    boolean updateTransaction(Transaction transaction);
//
//    boolean deleteTransaction(Transaction transaction);
//}
