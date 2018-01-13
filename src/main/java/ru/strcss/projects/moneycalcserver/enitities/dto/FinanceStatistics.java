package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Data;

import java.util.List;

@Data
public class FinanceStatistics {
    private List<Transaction> transactions;
}
