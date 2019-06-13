package ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by Stormcss
 * Date: 31.03.2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsStats {
    private BigDecimal sum;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal avg;
}
