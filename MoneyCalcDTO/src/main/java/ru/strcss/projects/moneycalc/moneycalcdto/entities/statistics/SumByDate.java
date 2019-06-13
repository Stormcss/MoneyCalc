package ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Stormcss
 * Date: 06.05.2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SumByDate {
    private LocalDate date;
    private BigDecimal sum;
}
