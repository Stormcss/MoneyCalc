package ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stormcss
 * Date: 31.03.2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsSearchRs {
    private Integer count;
    private TransactionsStats stats;
    private List<Transaction> items;

    public static TransactionsSearchRs generateEmpty() {
        return new TransactionsSearchRs(0, new TransactionsStats(), new ArrayList<>());
    }
}
