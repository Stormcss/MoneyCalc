package ru.strcss.projects.moneycalc.moneycalcserver.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.MetricsService;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.TimerType;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.TransactionsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.TransactionsService;

import java.util.List;

@Service
@AllArgsConstructor
public class TransactionsServiceImpl implements TransactionsService {

    private TransactionsMapper transactionsMapper;
    private MetricsService metricsService;

    @Override
    public Transaction getTransactionById(String login, Long transactionId) {
        return transactionsMapper.getTransactionById(login, transactionId);
    }

    @Override
    public List<Transaction> getTransactions(String login, TransactionsSearchFilter getContainer) throws Exception {
        return metricsService.getTimersStorage().get(TimerType.TRANSACTIONS_GET_TIMER)
                .recordCallable(() -> transactionsMapper.getTransactions(login, getContainer));
    }

    @Override
    public Long addTransaction(Long userId, Transaction transaction) throws Exception {
        transaction.setUserId(userId);

        return metricsService.getTimersStorage().get(TimerType.TRANSACTION_ADD_TIMER)
                .recordCallable(() -> transactionsMapper.addTransaction(transaction));
    }

    @Override
    public boolean updateTransaction(String login, Long transactionId, Transaction transaction) throws Exception {
        return metricsService.getTimersStorage().get(TimerType.TRANSACTION_UPDATE_TIMER)
                .recordCallable(() -> transactionsMapper.updateTransaction(login, transactionId, transaction) > 0);
    }

    @Override
    public boolean deleteTransaction(String login, Long transactionId) throws Exception {
        return metricsService.getTimersStorage().get(TimerType.TRANSACTION_UPDATE_TIMER)
                .recordCallable(() -> transactionsMapper.deleteTransaction(login, transactionId) > 0);
    }

}
