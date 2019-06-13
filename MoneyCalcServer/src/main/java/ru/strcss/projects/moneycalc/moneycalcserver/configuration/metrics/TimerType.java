package ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Stormcss
 * Date: 15.12.2018
 */
@Getter
@AllArgsConstructor
public enum TimerType {
    TRANSACTIONS_GET_TIMER("MoneyCalc - transactionsGetTimer", "Время получения списка транзакций"),
    TRANSACTION_ADD_TIMER("MoneyCalc - transactionAddTimer", "Время сохранения транзакции"),
    TRANSACTION_UPDATE_TIMER("MoneyCalc - transactionUpdateTimer", "Время обновления транзакции"),
    TRANSACTION_DELETE_TIMER("MoneyCalc - transactionDeleteTimer", "Время удаления транзакции"),
    SPENDING_SECTIONS_GET_TIMER("MoneyCalc - spendingSectionsGetTimer", "Время получения списка статей расхода"),
    SPENDING_SECTION_ADD_TIMER("MoneyCalc - spendingSectionAddTimer", "Время добавления статьи расхода"),
    SPENDING_SECTION_UPDATE_TIMER("MoneyCalc - spendingSectionUpdateTimer", "Время обновления статьи расхода"),
    SPENDING_SECTION_DELETE_TIMER("MoneyCalc - spendingSectionDeleteTimer", "Время удаления статьи расхода"),
    SETTINGS_GET_TIMER("MoneyCalc - settingsGetTimer", "Время получения настроек"),
    SETTINGS_UPDATE_TIMER("MoneyCalc - settingsUpdateTimer", "Время обновлния настроек"),
    STATS_SUM_BY_SECTION_TIMER("MoneyCalc - statsSumBySectionTimer", "Время получения статистики sumBySection"),
    STATS_SUM_BY_DATE_TIMER("MoneyCalc - statsSumByDateTimer", "Время получения статистики sumByDate"),
    STATS_SUM_BY_DATE_SECTION_TIMER("MoneyCalc - statsSumByDateSectionTimer", "Время получения статистики sumByDateSection"),
    STATS_SUMMARY_BY_SECTION_PROCESS_TIMER("MoneyCalc - statsSummaryBySectionProcessTimer",
            "Время обработки статистики summaryBySection");
    private String name;
    private String description;
}
