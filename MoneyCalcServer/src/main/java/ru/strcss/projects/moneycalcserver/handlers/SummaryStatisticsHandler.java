package ru.strcss.projects.moneycalcserver.handlers;

import lombok.extern.slf4j.Slf4j;
import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;

import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.formatDateFromString;

@Slf4j
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
                    .todayBalance(0)
                    .moneySpendAll(0)
                    .moneyLeftAll(0)
                    .build();
            statistics.put(sectionID, summaryBySection);
        }

        final Map<Integer, Integer> spendTodayBySection = new HashMap<>();
        // FIXME: 16.02.2018 get rid of AI



        //Заполняем moneySpendAll
        container.getTransactions().forEach(transaction -> {
            Integer sectionID = transaction.getSectionID();
            FinanceSummaryBySection temporary = statistics.get(sectionID);

            temporary.setMoneySpendAll(temporary.getMoneySpendAll() + transaction.getSum());
            if (formatDateFromString(transaction.getDate()).isEqual(container.getToday())) {
                spendTodayBySection.put(sectionID,   spendTodayBySection.getOrDefault(sectionID, 0) + transaction.getSum());
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


            long daysInPeriod = Period.between(container.getRangeTo(), container.getRangeTo()).getDays() + 2;
            long daysPassed = Period.between(container.getRangeTo(), container.getToday()).getDays() + 2;
            financeSummaryBySection.setTodayBalance(budget / daysInPeriod - spendTodayBySection.get(id));
            financeSummaryBySection.setSummaryBalance(budget / daysInPeriod * daysPassed - financeSummaryBySection.getMoneySpendAll());
            financeSummaryBySection.setMoneyLeftAll(budget - financeSummaryBySection.getMoneySpendAll());
            // TODO: 13.02.2018 calculate rest fields
        });

        return new ArrayList<>(statistics.values());
    }

}
