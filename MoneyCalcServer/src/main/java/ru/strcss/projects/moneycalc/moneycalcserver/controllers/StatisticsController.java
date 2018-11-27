package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.strcss.projects.moneycalc.api.StatisticsAPIService;
import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.statistics.FinanceSummaryFilter;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.entities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.entities.Settings;
import ru.strcss.projects.moneycalc.entities.SpendingSection;
import ru.strcss.projects.moneycalc.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.RequestValidation.Validator;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.SettingsService;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.SpendingSectionService;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.TransactionsService;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.SummaryStatisticsHandler;

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
public class StatisticsController extends AbstractController implements StatisticsAPIService {

    private TransactionsService transactionsService;
    private SpendingSectionService sectionService;
    private SettingsService settingsService;
    private SummaryStatisticsHandler statisticsHandler;

    public StatisticsController(TransactionsService transactionsService, SpendingSectionService spendingSectionService,
                                SettingsService settingsService, SummaryStatisticsHandler statisticsHandler) {
        this.transactionsService = transactionsService;
        this.sectionService = spendingSectionService;
        this.settingsService = settingsService;
        this.statisticsHandler = statisticsHandler;
    }

    // TODO: 26.08.2018 test me

    /**
     * Get finance summary for all active sections
     */
    @Override
    @GetMapping(value = "/get")
    public ResponseEntity<MoneyCalcRs<List<FinanceSummaryBySection>>> getFinanceSummaryBySection() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
//        Integer userId = personService.getPersonIdByLogin(login);

        Settings settings = settingsService.getSettings(login);
        List<SpendingSection> spendingSections = sectionService.getSpendingSections(login, false,
                false, false);
        List<Integer> sectionIds = spendingSections.stream().map(SpendingSection::getSectionId).collect(Collectors.toList());

        LocalDate dateFrom = settings.getPeriodFrom();
        LocalDate dateTo = settings.getPeriodTo();

        TransactionsSearchFilter transactionsFilter = new TransactionsSearchFilter(dateFrom, dateTo, sectionIds,
                null, null, null, null);

        List<Transaction> transactionsList = transactionsService.getTransactions(login, transactionsFilter);
//        List<Transaction> transactionsList = transactionsService.getTransactions(login, dateFrom, dateTo, sectionIds);

        FinanceSummaryCalculationContainer calculationContainer = FinanceSummaryCalculationContainer.builder()
                .rangeFrom(dateFrom)
                .rangeTo(dateTo)
                .sections(sectionIds)
                .transactions(transactionsList)
                .spendingSections(spendingSections)
                .today(LocalDate.now())
                .build();
        // TODO: 13.02.2018 should be client's time

        List<FinanceSummaryBySection> financeSummaryResult = statisticsHandler.calculateSummaryStatisticsBySection(calculationContainer);

        log.debug("Returned List of FinanceSummaryBySections for login \'{}\' : {}", login, financeSummaryResult);
        return responseSuccess(STATISTICS_RETURNED, financeSummaryResult);
    }

    @Override
    @PostMapping(value = "/getFiltered")
    public ResponseEntity<MoneyCalcRs<List<FinanceSummaryBySection>>> getFinanceSummaryBySection(
            @RequestBody FinanceSummaryFilter summaryFilter) {
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
            log.error("List of required IDs is not equal with list filtered Person's list for login: \'{}\'", login);
            return responseError("List of required IDs is not equal with list filtered Person's list");
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

        List<FinanceSummaryBySection> financeSummaryResult = statisticsHandler.calculateSummaryStatisticsBySection(calculationContainer);

        log.debug("Returned List of FinanceSummaryBySection for login \'{}\' : {}", login, financeSummaryResult);
        return responseSuccess(STATISTICS_RETURNED, financeSummaryResult);
    }
}
