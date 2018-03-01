package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FinanceSummaryBySection {
    private Integer sectionID;
    private Double todayBalance;
    private Double summaryBalance;
    private Integer moneySpendAll;
    private Integer moneyLeftAll;
}
