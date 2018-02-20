package ru.strcss.projects.moneycalcserver.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;

import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.formatDateFromString;
import static ru.strcss.projects.moneycalcserver.handlers.utils.StatisticsHandlerUtils.round;

@Slf4j
@Component
public class SummaryStatisticsHandler {
    /**
     * Accuracy of calculations - number of decimal places
     */
    private final int DIGITS = 2;

    public List<FinanceSummaryBySection> calculateSummaryStatisticsBySections(FinanceSummaryCalculationContainer container) {

        final Map<Integer, FinanceSummaryBySection> statistics = new HashMap<>();
        //Создать Map<Integer,FinanceSummaryBySection>, заполнять в нём moneySpendAll. Каждый из которых после этого дозаполнить

        //Создали мапу
        for (Integer sectionID : container.getSections()) {
            FinanceSummaryBySection summaryBySection = FinanceSummaryBySection.builder()
                    .sectionID(sectionID)
                    .todayBalance(0)
                    .moneySpendAll(0)
                    .moneyLeftAll(0)
                    .build();
            statistics.put(sectionID, summaryBySection);
        }

        final Map<Integer, Double> spendTodayBySection = new HashMap<>();

        //Заполняем moneySpendAll
        container.getTransactions().forEach(transaction -> {
            Integer sectionID = transaction.getSectionID();
            FinanceSummaryBySection temporary = statistics.get(sectionID);

            temporary.setMoneySpendAll(temporary.getMoneySpendAll() + transaction.getSum());
            if (formatDateFromString(transaction.getDate()).isEqual(container.getToday())) {
                spendTodayBySection.put(sectionID, spendTodayBySection.getOrDefault(sectionID, 0d) + transaction.getSum());
            }
            statistics.put(sectionID, temporary);
        });


        //Дозаполняем данными
        statistics.forEach((id, financeSummaryBySection) -> {
            int budget = container.getSpendingSections().stream()
                    .filter(spendingSection -> spendingSection.getId().equals(id))
                    .findAny()
                    .get()
                    .getBudget();

            long daysInPeriod = Period.between(container.getRangeFrom(), container.getRangeTo()).getDays() + 1;

            long daysPassed;

            if (container.getToday().isAfter(container.getRangeTo()))
                daysPassed = daysInPeriod;
            else
                daysPassed = Period.between(container.getRangeFrom(), container.getToday()).getDays() + 1;

            double moneyPerDay = round((double) budget / daysInPeriod, DIGITS);

            financeSummaryBySection.setTodayBalance(moneyPerDay - spendTodayBySection.getOrDefault(id, 0d));
            financeSummaryBySection.setSummaryBalance(moneyPerDay * daysPassed - financeSummaryBySection.getMoneySpendAll());
            financeSummaryBySection.setMoneyLeftAll(budget - financeSummaryBySection.getMoneySpendAll());

            log.error("todayBalance: {}; summaryBalance: {}", financeSummaryBySection.getTodayBalance(), financeSummaryBySection.getSummaryBalance());
        });

        return new ArrayList<>(statistics.values());
    }

}
