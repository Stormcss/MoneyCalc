package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.api.StatisticsAPIService;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.ValidationResult;
import ru.strcss.projects.moneycalc.dto.crudcontainers.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalcserver.handlers.SummaryStatisticsHandler;

import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.responseError;

@Slf4j
@RestController
@RequestMapping("/api/statistics/financeSummary")
public class StatisticsController extends AbstractController implements StatisticsAPIService {

    @Override
    @PostMapping(value = "/getFinanceSummaryBySection")
    public AjaxRs<FinanceSummaryBySection> getFinanceSummaryBySection(FinanceSummaryGetContainer getContainer) {

        ValidationResult validationResult = getContainer.isValid();

        if (!validationResult.isValidated()) {
            log.error("getting FinanceSummaryBySection has failed - required fields are empty: {}", validationResult.getReasons());
            return responseError("Required fields are empty: " + validationResult.getReasons());
        }

        // TODO: 09.02.2018 Request list of Transactions by Section, range and login

        SummaryStatisticsHandler statisticsHandler = new SummaryStatisticsHandler();

        statisticsHandler.calculateSummayStatisticsBySecion();

        return null;
    }
}
