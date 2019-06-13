package ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class SummaryBySection implements Serializable {
    private Integer sectionId;
    private String sectionName;
    private Double todayBalance;
    private Double summaryBalance;
    private Double moneySpendAll;
    private Double moneyLeftAll;
}
