package ru.strcss.projects.moneycalcserver.handlers;

import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SummaryStatisticsHandler {

    public List<FinanceSummaryBySection> calculateSummaryStatisticsBySections(FinanceSummaryCalculationContainer container) {

        final HashMap<Integer, FinanceSummaryBySection> statistics = new HashMap<>();

        //вариант1: пробегать по листу N раз, где N - число нужных секций
        //вариант2: Создать List<FinanceSummaryBySection>, заполнять в нём moneySpendAll. Каждый из которых после этого дозаполнить
        //вариант3: Создать Map<Integer,FinanceSummaryBySection>, заполнять в нём moneySpendAll. Каждый из которых после этого дозаполнить

//        Создали мапу
        for (Integer sectionID : container.getSections()) {
            FinanceSummaryBySection summaryBySection = FinanceSummaryBySection.builder()
                    .sectionID(sectionID)
                    .build();
            statistics.put(sectionID, summaryBySection);
        }

        //Заполняем moneySpendAll
        container.getTransactions().forEach(transaction -> {
            FinanceSummaryBySection temporary = statistics.get(transaction.getSectionID());
            temporary.setMoneySpendAll(temporary.getMoneySpendAll() + transaction.getSum());
            statistics.put(transaction.getSectionID(), temporary);
        });

        //Дозаполняем данными
        statistics.forEach((id, financeSummaryBySection) -> {
            int budget = container.getSpendingSections().stream()
                    .filter(spendingSection -> spendingSection.getID().equals(id))
                    .findAny()
                    .get()
                    .getBudget();
            financeSummaryBySection.setMoneyLeftAll(budget - financeSummaryBySection.getMoneySpendAll());
            financeSummaryBySection.setTodayBalance(budget - financeSummaryBySection.getMoneySpendAll());
            // TODO: 13.02.2018 calculate rest fields
        });

        return new ArrayList<>(statistics.values());
    }

}
