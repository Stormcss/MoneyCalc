package ru.strcss.projects.moneycalc.api;

import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;

import java.util.List;

public interface StatisticsAPIService {

    AjaxRs<List<FinanceSummaryBySection>> getFinanceSummaryBySection(FinanceSummaryGetContainer container);

}