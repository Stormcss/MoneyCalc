package ru.strcss.projects.moneycalc.api;

import org.springframework.http.ResponseEntity;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.statistics.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;

import java.util.List;

public interface StatisticsAPIService {
    ResponseEntity<MoneyCalcRs<List<FinanceSummaryBySection>>> getFinanceSummaryBySection(FinanceSummaryGetContainer container);
}