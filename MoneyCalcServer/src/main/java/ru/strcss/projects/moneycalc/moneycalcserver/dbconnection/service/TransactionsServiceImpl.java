package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service;

import org.springframework.stereotype.Service;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.TransactionsService;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.TransactionsMapper;

import java.util.List;

@Service
public class TransactionsServiceImpl implements TransactionsService {

    private TransactionsMapper transactionsMapper;

    public TransactionsServiceImpl(TransactionsMapper transactionsMapper) {
        this.transactionsMapper = transactionsMapper;
    }

    @Override
    public Transaction getTransactionById(String login, Long transactionId) {
        return transactionsMapper.getTransactionById(login, transactionId);
    }

    @Override
    public List<Transaction> getTransactions(String login, TransactionsSearchFilter getContainer) {
        return transactionsMapper.getTransactions(login, getContainer);
    }

    @Override
    public Long addTransaction(Long userId, Transaction transaction) {
        transaction.setUserId(userId);
        return transactionsMapper.addTransaction(transaction);
    }

    @Override
    public boolean updateTransaction(String login, Transaction transaction) {
//        transactionsMapper.updateTransaction(login, transaction)
        return false;
    }

    @Override
    public boolean deleteTransaction(String login, Long transactionId) {
        transactionsMapper.deleteTransaction(login, transactionId);
        return true;
    }

}
