package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.FinanceSummaryFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.MetricsService;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.TimerType;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.SummaryStatisticsHandler;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SettingsService;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SpendingSectionService;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.TransactionsService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.DATE_SEQUENCE_INCORRECT;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.STATISTICS_RETURNED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.responseError;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.responseSuccess;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.ValidationUtils.isDateSequenceValid;

@Slf4j
@RestController
@RequestMapping("/api/stats/summaryBySection")
@AllArgsConstructor
public class StatisticsController extends AbstractController {

    private TransactionsService transactionsService;
    private SpendingSectionService sectionService;
    private SettingsService settingsService;
    private SummaryStatisticsHandler statisticsHandler;
    private MetricsService metricsService;

    /**
     * Get finance summary for all active sections
     */
    @GetMapping
    @Timed(value = "stats.summaryBySection.get", extraTags = {"time", "formGetToDbSave"})
    public ResponseEntity<MoneyCalcRs<List<FinanceSummaryBySection>>> getFinanceSummaryBySection() throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Settings settings = settingsService.getSettings(login);
        List<SpendingSection> spendingSections = sectionService.getSpendingSections(login, false,
                false, false);
        List<Integer> sectionIds = spendingSections.stream().map(SpendingSection::getSectionId).collect(Collectors.toList());

        LocalDate dateFrom = settings.getPeriodFrom();
        LocalDate dateTo = settings.getPeriodTo();

        TransactionsSearchFilter transactionsFilter = new TransactionsSearchFilter(dateFrom, dateTo, sectionIds,
                null, null, null, null);

        List<Transaction> transactionsList = transactionsService.getTransactions(login, transactionsFilter);

        FinanceSummaryCalculationContainer calculationContainer = FinanceSummaryCalculationContainer.builder()
                .rangeFrom(dateFrom)
                .rangeTo(dateTo)
                .sections(sectionIds)
                .transactions(transactionsList)
                .spendingSections(spendingSections)
                .today(LocalDate.now())
                .build();
        // TODO: 13.02.2018 should be client's time

        List<FinanceSummaryBySection> financeSummaryResult = metricsService.getTimersStorage().get(TimerType.STATS_PROCESS_TIMER)
                .recordCallable(() -> statisticsHandler.calculateSummaryStatisticsBySection(calculationContainer));

        log.debug("Returned List of FinanceSummaryBySections for login \'{}\' : {}", login, financeSummaryResult);
        return responseSuccess(STATISTICS_RETURNED, financeSummaryResult);
    }

    @PostMapping(value = "/getFiltered")
    public ResponseEntity<MoneyCalcRs<List<FinanceSummaryBySection>>> getFinanceSummaryBySection(
            @RequestBody FinanceSummaryFilter summaryFilter) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        RequestValidation<List<FinanceSummaryBySection>> requestValidation = new Validator(summaryFilter,
                "Getting Finance Summary")
                .addValidation(() -> isDateSequenceValid(summaryFilter.getRangeFrom(), summaryFilter.getRangeTo()),
                        () -> DATE_SEQUENCE_INCORRECT)
                .validate();
        if (!requestValidation.isValid()) return requestValidation.getValidationError();

        TransactionsSearchFilter transactionsFilter = new TransactionsSearchFilter(summaryFilter.getRangeFrom(),
                summaryFilter.getRangeTo(), summaryFilter.getSectionIds(), null, null, null, null);

        List<Transaction> transactions = transactionsService.getTransactions(login, transactionsFilter);

        //оставляю только те секции клиента для которых мне нужна статистика
        List<SpendingSection> spendingSections = sectionService.getSpendingSections(login, false,
                false, false).stream()
                .filter(section -> summaryFilter.getSectionIds().stream().anyMatch(id -> id.equals(section.getSectionId())))
                .collect(Collectors.toList());

        if (spendingSections.size() != summaryFilter.getSectionIds().size()) {
            log.error("List of required IDs is not equal with filtered Person's list for login: \'{}\'", login);
            return responseError("List of required IDs is not equal with filtered Person's list");
        }

        FinanceSummaryCalculationContainer calculationContainer = FinanceSummaryCalculationContainer.builder()
                .rangeFrom(summaryFilter.getRangeFrom())
                .rangeTo((summaryFilter.getRangeTo()))
                .sections(summaryFilter.getSectionIds())
                .transactions(transactions)
                .spendingSections(spendingSections)
                .today(LocalDate.now())
                .build();
        // TODO: 13.02.2018 should be client's time

        List<FinanceSummaryBySection> financeSummaryResult = metricsService.getTimersStorage().get(TimerType.STATS_PROCESS_TIMER)
                .recordCallable(() -> statisticsHandler.calculateSummaryStatisticsBySection(calculationContainer));

        log.debug("Returned List of FinanceSummaryBySection for login \'{}\' : {}", login, financeSummaryResult);
        return responseSuccess(STATISTICS_RETURNED, financeSummaryResult);
    }
}
