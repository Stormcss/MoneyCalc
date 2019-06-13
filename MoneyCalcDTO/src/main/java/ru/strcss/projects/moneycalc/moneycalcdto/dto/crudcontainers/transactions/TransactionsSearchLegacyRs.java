package ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.TransactionLegacy;

import java.util.List;

/**
 * Created by Stormcss
 * Date: 31.03.2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsSearchLegacyRs {
    private TransactionsStats stats;
    private List<TransactionLegacy> items;
}
