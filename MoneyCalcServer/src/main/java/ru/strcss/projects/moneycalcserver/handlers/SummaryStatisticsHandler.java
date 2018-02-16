package ru.strcss.projects.moneycalcserver.handlers;

import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.formatDateFromString;

public class SummaryStatisticsHandler {

    public List<FinanceSummaryBySection> calculateSummaryStatisticsBySections(FinanceSummaryCalculationContainer container) {

        final Map<Integer, FinanceSummaryBySection> statistics = new HashMap<>();

        //вариант1: пробегать по листу N раз, где N - число нужных секций
        //вариант2: Создать List<FinanceSummaryBySection>, заполнять в нём moneySpendAll. Каждый из которых после этого дозаполнить
        //вариант3: Создать Map<Integer,FinanceSummaryBySection>, заполнять в нём moneySpendAll. Каждый из которых после этого дозаполнить

        //Создали мапу
        for (Integer sectionID : container.getSections()) {
            FinanceSummaryBySection summaryBySection = FinanceSummaryBySection.builder()
                    .sectionID(sectionID)
                    .moneySpendAll(0)
                    .moneyLeftAll(0)
                    .build();
            statistics.put(sectionID, summaryBySection);
        }

        final AtomicInteger spendToday = new AtomicInteger();
        // FIXME: 16.02.2018 get rid of AI

        //Заполняем moneySpendAll
        container.getTransactions().forEach(transaction -> {
            FinanceSummaryBySection temporary = statistics.get(transaction.getSectionID());

            temporary.setMoneySpendAll(temporary.getMoneySpendAll() + transaction.getSum());
            if (formatDateFromString(transaction.getDate()).isEqual(container.getToday())) {
                spendToday.set(spendToday.get() + transaction.getSum());
            }
            statistics.put(transaction.getSectionID(), temporary);
        });

        //Дозаполняем данными
        statistics.forEach((id, financeSummaryBySection) -> {
            int budget = container.getSpendingSections().stream()
                    .filter(spendingSection -> spendingSection.getId().equals(id))
                    .findAny()
                    .get()
                    .getBudget();


            long daysInPeriod = Duration.between(container.getRangeTo(), container.getRangeTo()).toDays();
            long daysPassed = Duration.between(container.getRangeTo(), container.getToday()).toDays();
            financeSummaryBySection.setTodayBalance(budget / daysInPeriod - spendToday.get());
            financeSummaryBySection.setSummaryBalance(budget / daysInPeriod * daysPassed - financeSummaryBySection.getMoneySpendAll());
            financeSummaryBySection.setMoneyLeftAll(budget - financeSummaryBySection.getMoneySpendAll());
            // TODO: 13.02.2018 calculate rest fields
        });

        return new ArrayList<>(statistics.values());
    }

}
