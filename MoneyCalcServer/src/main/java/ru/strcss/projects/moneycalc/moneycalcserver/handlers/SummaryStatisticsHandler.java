package ru.strcss.projects.moneycalc.moneycalcserver.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils.TodayPositionRange;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils.StatisticsHandlerUtils.*;

@Slf4j
@Component
public class SummaryStatisticsHandler {

    /**
     * Accuracy of calculations - number of decimal places
     */
    private final int DIGITS = 2;

    public List<FinanceSummaryBySection> calculateSummaryStatisticsBySections(FinanceSummaryCalculationContainer container) {

        final Map<Integer, FinanceSummaryBySection> statistics = new HashMap<>();

        //Создали мапу
        for (Integer sectionID : container.getSections()) {
            FinanceSummaryBySection summaryBySection = FinanceSummaryBySection.builder()
                    .sectionID(sectionID)
                    .moneySpendAll(0)
                    .build();
            statistics.put(sectionID, summaryBySection);
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
            int budget = getBudget(container, id);

            long daysInPeriod = ChronoUnit.DAYS.between(container.getRangeFrom(), container.getRangeTo()) + 1;
            double moneyPerDay = (double) budget / daysInPeriod;

            long daysPassed = getDaysPassed(container, todayPositionRange, daysInPeriod);

            financeSummaryBySection.setTodayBalance(getTodayBalance(todayPositionRange, spendTodayBySection, id, moneyPerDay));
            financeSummaryBySection.setSummaryBalance(round(moneyPerDay * daysPassed - financeSummaryBySection.getMoneySpendAll(), DIGITS));
            financeSummaryBySection.setMoneyLeftAll(budget - financeSummaryBySection.getMoneySpendAll());
        });
        return new ArrayList<>(statistics.values());
    }

    private Double getTodayBalance(TodayPositionRange todayPositionRange, Map<Integer, Double> spendTodayBySection, Integer id, double moneyPerDay) {
        if (todayPositionRange.equals(TodayPositionRange.IN))
            return round(moneyPerDay - spendTodayBySection.getOrDefault(id, 0d), DIGITS);
        else
            return 0d;
    }

    private Integer getBudget(FinanceSummaryCalculationContainer container, Integer id) {
        return container.getSpendingSections().stream()
                .filter(spendingSection -> spendingSection.getSectionId().equals(id))
                .findAny()
                .get()
                .getBudget();
    }

    private void fillMoneySpend(FinanceSummaryCalculationContainer container, Map<Integer, FinanceSummaryBySection> statistics) {
        fillMoneySpend(container, statistics, null);
    }

    private void fillMoneySpend(FinanceSummaryCalculationContainer container, Map<Integer, FinanceSummaryBySection> statistics, Map<Integer, Double> spendTodayBySection) {
        container.getTransactions().forEach(transaction -> {
            Integer sectionID = transaction.getSectionId();
            FinanceSummaryBySection temporary = statistics.get(sectionID);

            temporary.setMoneySpendAll(temporary.getMoneySpendAll() + transaction.getSum());
            if (spendTodayBySection != null && transaction.getDate().isEqual(container.getToday())) {
//            if (spendTodayBySection != null && formatDateFromString(transaction.getDate()).isEqual(container.getToday())) {
                spendTodayBySection.put(sectionID, spendTodayBySection.getOrDefault(sectionID, 0d) + transaction.getSum());
            }
            statistics.put(sectionID, temporary);
        });
    }
}
