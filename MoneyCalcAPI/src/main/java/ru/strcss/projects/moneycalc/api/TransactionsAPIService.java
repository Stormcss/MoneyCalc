package ru.strcss.projects.moneycalc.api;

import org.springframework.http.ResponseEntity;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.entities.Transaction;

import java.util.List;

public interface TransactionsAPIService {

    ResponseEntity<MoneyCalcRs<List<Transaction>>> getTransactions(TransactionsSearchFilter container);

    ResponseEntity<MoneyCalcRs<Transaction>> addTransaction(TransactionAddContainer transactionContainer);

    ResponseEntity<MoneyCalcRs<Transaction>> updateTransaction(TransactionUpdateContainer transactionContainer);

    ResponseEntity<MoneyCalcRs<Void>> deleteTransaction(Long transactionId);
}
