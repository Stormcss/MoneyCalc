package ru.strcss.projects.moneycalc.entities;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class FinanceSummaryBySection implements Serializable {
    private Integer sectionId;
    private String sectionName;
    private Double todayBalance;
    private Double summaryBalance;
    private Double moneySpendAll;
    private Double moneyLeftAll;
}
