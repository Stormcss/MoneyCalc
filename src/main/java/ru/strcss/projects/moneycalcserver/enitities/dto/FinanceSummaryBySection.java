package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinanceSummaryBySection {
    private float todayBalance;
    private float summaryBalance;
    private int moneySpendAll;
    private int moneyLeftAll;
}
