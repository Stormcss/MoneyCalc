package ru.strcss.projects.moneycalc.moneycalcserver.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.ItemsContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SummaryBySection;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils.TodayPositionRange;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils.StatisticsHandlerUtils.getDaysPassed;
import static ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils.StatisticsHandlerUtils.getTodayPositionRange;
import static ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils.StatisticsHandlerUtils.round;

@Slf4j
@Component
public class SummaryStatisticsHandler {

    /**
     * Accuracy of calculations - number of decimal places
     */
    private static final int DIGITS = 2;

    public ItemsContainer<SummaryBySection> calculateSummaryStatisticsBySection(FinanceSummaryCalculationContainer container) {

        final Map<Integer, SummaryBySection> statistics = new HashMap<>();

        //Создали мапу
        for (Integer sectionId : container.getSections()) {
            SummaryBySection summaryBySection = SummaryBySection.builder()
                    .sectionId(sectionId)
                    .moneySpendAll(0d)
                    .build();
            statistics.put(sectionId, summaryBySection);
        }

        TodayPositionRange todayPositionRange = getTodayPositionRange(container);

        final Map<Integer, Double> spendTodayBySection = new HashMap<>();

        //Заполняем moneySpendAll
        if (todayPositionRange.equals(TodayPositionRange.IN))
            fillMoneySpend(container, statistics, spendTodayBySection);
        else
            fillMoneySpend(container, statistics);

        //Дозаполняем данными
        statistics.forEach((id, financeSummaryBySection) -> {
            SpendingSection currentSection = getSpendingSectionById(container, id);
            Long budget = currentSection.getBudget();

            long daysInPeriod = ChronoUnit.DAYS.between(container.getRangeFrom(), container.getRangeTo()) + 1;
            double moneyPerDay = (double) budget / daysInPeriod;

            long daysPassed = getDaysPassed(container, todayPositionRange, daysInPeriod);

            financeSummaryBySection.setTodayBalance(getTodayBalance(todayPositionRange, spendTodayBySection, id, moneyPerDay));
            financeSummaryBySection.setSummaryBalance(
                    round(moneyPerDay * daysPassed - financeSummaryBySection.getMoneySpendAll(), DIGITS));
            financeSummaryBySection.setMoneyLeftAll(budget - financeSummaryBySection.getMoneySpendAll());
            financeSummaryBySection.setSectionName(currentSection.getName());
        });
        return new ItemsContainer<>(0L, null, new ArrayList<>(statistics.values()));
    }

    private Double getTodayBalance(TodayPositionRange todayPositionRange, Map<Integer, Double> spendTodayBySection,
                                   Integer id, double moneyPerDay) {
        if (todayPositionRange.equals(TodayPositionRange.IN))
            return round(moneyPerDay - spendTodayBySection.getOrDefault(id, 0d), DIGITS);
        else
            return 0d;
    }

    private SpendingSection getSpendingSectionById(FinanceSummaryCalculationContainer container, Integer id) {
        return container.getSpendingSections().stream()
                .filter(spendingSection -> spendingSection.getSectionId().equals(id))
                .findAny()
                .orElseThrow(() -> new InternalError("Holy shit has happened"));
    }

    private void fillMoneySpend(FinanceSummaryCalculationContainer container, Map<Integer, SummaryBySection> statistics) {
        fillMoneySpend(container, statistics, null);
    }

    private void fillMoneySpend(FinanceSummaryCalculationContainer container,
                                Map<Integer, SummaryBySection> statistics, Map<Integer, Double> spendTodayBySection) {
        container.getTransactions().forEach(transaction -> {
            Integer sectionId = transaction.getSectionId();
            SummaryBySection temporary = statistics.get(sectionId);

            temporary.setMoneySpendAll(temporary.getMoneySpendAll() + transaction.getSum());
            if (spendTodayBySection != null && transaction.getDate().isEqual(container.getToday())) {
                spendTodayBySection.put(sectionId, spendTodayBySection.getOrDefault(sectionId, 0d) + transaction.getSum());
            }
            statistics.put(sectionId, temporary);
        });
    }
}
