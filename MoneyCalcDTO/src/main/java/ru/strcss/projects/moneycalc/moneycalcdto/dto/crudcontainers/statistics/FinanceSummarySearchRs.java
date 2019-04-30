package ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.FinanceSummaryBySection;

import java.util.List;

/**
 * Created by Stormcss
 * Date: 28.04.2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinanceSummarySearchRs {
    private List<FinanceSummaryBySection> items;
}
