package ru.strcss.projects.moneycalc.integration.utils;

import ru.strcss.projects.moneycalc.integration.testapi.MoneyCalcClient;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Status;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.FinanceSummaryFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;

import java.util.List;

import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.sendRequest;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSpendingSection;

public class StatisticsControllerTestUtils {

    /**
     * Get FinanceSummaryBySection and return it
     *
     * @param getContainer - container with parameters of requested statistics
     * @return
     */
    public static FinanceSummaryBySection getFinanceSummaryBySection(FinanceSummaryFilter getContainer, MoneyCalcClient service, String token) {
        MoneyCalcRs<List<FinanceSummaryBySection>> responseGetStats = sendRequest(service.getFinanceSummaryBySection(token, getContainer), Status.SUCCESS).body();
        return responseGetStats.getPayload().get(0);
    }

    /**
     * Add Person's Sections if required (in case if Person by default has less Sections then it is required for test)
     *
     * @param numOfSections - required number of Person's sections
     */
    public static void checkPersonsSections(int numOfSections, long budget, MoneyCalcClient service, String token) {
        List<SpendingSection> spendingSections = sendRequest(service.getSpendingSections(token)).body().getItems();

        if (numOfSections > spendingSections.size()) {
            for (int i = 0; i < numOfSections - spendingSections.size(); i++) {
                sendRequest(service.addSpendingSection(token, generateSpendingSection(budget)), Status.SUCCESS);
            }
        }
    }
}
