package ru.strcss.projects.moneycalcserver.enitities.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FinanceSummary {
    private List<FinanceSummaryBySection> financeSections = new ArrayList<>();
    private byte daysSpend;
    private byte daysInMonth;
}
