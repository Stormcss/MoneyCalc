package ru.strcss.projects.moneycalc.dto;

import lombok.Builder;
import lombok.Data;
import ru.strcss.projects.moneycalc.entities.SpendingSection;
import ru.strcss.projects.moneycalc.entities.Transaction;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class FinanceSummaryCalculationContainer {
    private List<Transaction> transactions;
    private List<Integer> sections;
    private List<SpendingSection> spendingSections;
    private LocalDate rangeFrom;
    private LocalDate rangeTo;
    private LocalDate today;
}
