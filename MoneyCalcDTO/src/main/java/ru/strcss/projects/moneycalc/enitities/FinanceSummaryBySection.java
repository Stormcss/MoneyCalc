package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class FinanceSummaryBySection implements Serializable {
    private Integer sectionID;
    private Double todayBalance;
    private Double summaryBalance;
    private Integer moneySpendAll;
    private Integer moneyLeftAll;
}
