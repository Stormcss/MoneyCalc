package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Deprecated
public class FinanceSummary {
    private String _id;

    private List<FinanceSummaryBySection> financeSections;
}
