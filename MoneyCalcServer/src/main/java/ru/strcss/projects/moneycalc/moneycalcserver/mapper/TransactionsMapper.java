package ru.strcss.projects.moneycalc.moneycalcserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.entities.Transaction;

import java.util.List;

/**
 * Created by Stormcss
 * Date: 01.11.2018
 */
@Mapper
public interface TransactionsMapper {
    //    @Select("select t.* from \"Transactions\" t" +
//            "join \"Person\" p on t.\"userId\" = p.id" +
//            "join \"Access\" a on p.\"accessId\" = a.id" +
//            "where t.id = 1 and a.login = '277B20BF58674A75BB5F95759E1B2BAE'")
    Transaction getTransactionById(@Param("login") String login, @Param("transactionId") Long transactionId);

    List<Transaction> getTransactions(@Param("login") String login, @Param("filter") TransactionsSearchFilter filter);

//    List<Transaction> getTransactions(@Param("login") String login, @Param("dateFrom") LocalDate dateFrom,
//                                      @Param("dateTo") LocalDate dateTo, @Param("sectionIds") List<Integer> sectionIds);

    Long addTransaction(Transaction transaction);

    Integer updateTransaction(@Param("login") String login, @Param("transactionId") Long transactionId,
                              @Param("transaction") Transaction transaction);

    Integer deleteTransaction(@Param("login") String login, @Param("transactionId") Long transactionId);

}
