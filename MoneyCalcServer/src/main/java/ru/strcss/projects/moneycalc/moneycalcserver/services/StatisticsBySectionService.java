package ru.strcss.projects.moneycalc.moneycalcserver.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.ItemsContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.StatisticsFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumBySection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SummaryBySection;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.MetricsService;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.TimerType;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.SummaryStatisticsHandler;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.StatsBySectionMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SettingsService;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SpendingSectionService;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.TransactionsService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Stormcss
 * Date: 06.05.2019
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsBySectionService {
    private final TransactionsService transactionsService;
    private final SpendingSectionService sectionService;
    private final SettingsService settingsService;
    private final SummaryStatisticsHandler statisticsHandler;
    private final MetricsService metricsService;
    private final StatsBySectionMapper statsMapper;

    public ItemsContainer<SummaryBySection> getSummary(String login) throws Exception {
        Settings settings = settingsService.getSettings(login);
        SpendingSectionsSearchRs sectionsSearchRs = sectionService.getSpendingSections(login, false,
                false, false);
        List<Integer> sectionIds = sectionsSearchRs.getItems().stream().map(SpendingSection::getSectionId).collect(Collectors.toList());

        LocalDate dateFrom = settings.getPeriodFrom();
        LocalDate dateTo = settings.getPeriodTo();

        TransactionsSearchFilter transactionsFilter = new TransactionsSearchFilter(dateFrom, dateTo, sectionIds,
                null, null, null, null);

        TransactionsSearchRs transactionsSearchRs = transactionsService.getTransactions(login, transactionsFilter, false);

        FinanceSummaryCalculationContainer calculationContainer = FinanceSummaryCalculationContainer.builder()
                .rangeFrom(dateFrom)
                .rangeTo(dateTo)
                .sections(sectionIds)
                .transactions(transactionsSearchRs.getItems())
                .spendingSections(sectionsSearchRs.getItems())
                .today(LocalDate.now())
                .build();
        // TODO: 13.02.2018 should be client's time

        return metricsService.getTimersStorage().get(TimerType.STATS_PROCESS_TIMER)
                .recordCallable(() -> statisticsHandler.calculateSummaryStatisticsBySection(calculationContainer));
    }

    public ItemsContainer<SumBySection> getSum(String login, StatisticsFilter statisticsFilter) {
        ItemsContainer<SumBySection> sumBySection = statsMapper.getSum(login, statisticsFilter);
        return sumBySection != null ? sumBySection : ItemsContainer.buildEmpty();
    }
}
