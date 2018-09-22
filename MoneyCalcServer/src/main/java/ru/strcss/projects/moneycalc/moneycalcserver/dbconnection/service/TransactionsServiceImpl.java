package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service;

import org.springframework.stereotype.Service;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.TransactionsDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.TransactionsService;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionsServiceImpl implements TransactionsService {

    private TransactionsDao transactionsDao;

    public TransactionsServiceImpl(TransactionsDao transactionsDao) {
        this.transactionsDao = transactionsDao;
    }

    @Override
    public Transaction getTransactionById(Integer transactionId) {
        return transactionsDao.getTransactionById(transactionId);
    }

    @Override
    public List<Transaction> getTransactionsByPersonId(Integer personId, LocalDate dateFrom, LocalDate dateTo, List<Integer> sectionIds) {
        return transactionsDao.getTransactionsByPersonId(personId, dateFrom, dateTo, sectionIds);
    }

    @Override
    public List<Transaction> getTransactionsByLogin(String login, LocalDate dateFrom, LocalDate dateTo, List<Integer> sectionIds) {
        return transactionsDao.getTransactionsByLogin(login, dateFrom, dateTo, sectionIds);
    }

    @Override
    public List<Transaction> getTransactionsByLogin(String login) {
        return transactionsDao.getTransactionsByLogin(login);
    }

    @Override
    public Integer addTransaction(Integer personId, Transaction transaction) {
        return transactionsDao.addTransaction(personId, transaction);
    }

    @Override
    public boolean updateTransaction(Transaction transaction) {
        return transactionsDao.updateTransaction(transaction);
    }

    @Override
    public boolean deleteTransaction(Transaction transaction) {
        return transactionsDao.deleteTransaction(transaction);
    }
}
