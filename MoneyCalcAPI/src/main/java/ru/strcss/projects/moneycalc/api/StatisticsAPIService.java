package ru.strcss.projects.moneycalc.api;

import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummary;

public interface StatisticsAPIService {

    AjaxRs<FinanceSummary> getFinanceSummary(FinanceSummaryGetContainer container);

}