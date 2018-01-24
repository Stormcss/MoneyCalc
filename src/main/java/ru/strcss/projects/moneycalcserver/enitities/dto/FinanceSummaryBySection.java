package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@Document
@ToString
public class FinanceSummaryBySection {
    private double todayBalance;
    private double summaryBalance;
    private int moneySpendAll;
    private int moneyLeftAll;
}
