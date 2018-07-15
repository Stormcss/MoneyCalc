package ru.strcss.projects.moneycalc.enitities;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Deprecated
public class Finance {
    private String _id;
    private FinanceSummary financeSummary;
}
