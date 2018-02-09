package ru.strcss.projects.moneycalc.api;

import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;

public interface StatisticsAPIService {

    AjaxRs<FinanceSummaryBySection> getFinanceSummaryBySection(FinanceSummaryGetContainer container);

}