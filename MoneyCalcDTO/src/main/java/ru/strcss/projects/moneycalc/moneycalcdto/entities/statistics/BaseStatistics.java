package ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by Stormcss
 * Date: 06.05.2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseStatistics {
    private BigDecimal total;
    private BigDecimal min;
    private BigDecimal max;

    public static BaseStatistics buildEmpty() {
        return new BaseStatistics(BigDecimal.ZERO, null, null);
    }
}
