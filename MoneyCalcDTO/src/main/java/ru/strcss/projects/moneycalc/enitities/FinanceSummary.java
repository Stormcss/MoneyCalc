package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FinanceSummary {
    private String _id;

    private List<FinanceSummaryBySection> financeSections;
    //these fields mustn't be stored in DB

    //    private int daysSpend;
//    private int daysInMonth;
}
