package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FinanceSummaryBySection {

    private Integer sectionID;
    private double todayBalance;
    private double summaryBalance;
    private Integer moneySpendAll;
    private Integer moneyLeftAll;
}
