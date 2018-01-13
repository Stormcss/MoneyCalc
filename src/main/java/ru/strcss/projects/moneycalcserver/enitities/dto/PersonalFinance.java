package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PersonalFinance {
    private FinanceSummary financeSummary;
    private FinanceStatistics financeStatistics;
}
