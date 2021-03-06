package ru.strcss.projects.moneycalc.moneycalcserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;

/**
 * Created by Stormcss
 * Date: 01.11.2018
 */
@Mapper
public interface TransactionsMapper {
    Transaction getTransactionById(@Param("login") String login, @Param("transactionId") Long transactionId);

    TransactionsSearchRs getTransactions(@Param("login") String login, @Param("filter") TransactionsSearchFilter filter,
                                         @Param("isStatsRequired") boolean isStatsRequired);

    Long addTransaction(Transaction transaction);

    Integer updateTransaction(@Param("login") String login, @Param("transactionId") Long transactionId,
                              @Param("transaction") Transaction transaction);

    Integer deleteTransaction(@Param("login") String login, @Param("transactionId") Long transactionId);

}
