package ru.strcss.projects.moneycalcserver.controllers.utils;

import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.LoginGetContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionAddContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.statistics.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalcserver.controllers.testapi.MoneyCalcClient;

import java.util.List;

import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.generateSpendingSection;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

public class StatisticsControllerTestUtils {

    /**
     * Get FinanceSummaryBySection and return it
     *
     * @param getContainer - container with parameters of requested statistics
     * @return
     */
    public static FinanceSummaryBySection getFinanceSummaryBySection(FinanceSummaryGetContainer getContainer, MoneyCalcClient service) {
        AjaxRs<List<FinanceSummaryBySection>> responseGetStats = sendRequest(service.getFinanceSummaryBySection(getContainer), Status.SUCCESS).body();
        return responseGetStats.getPayload().get(0);
    }

    /**
     * Add Person's Sections if required (in case if Person by default has less Sections then it is required for test)
     *
     * @param numOfSections - required number of Person's sections
     * @param login         - Person's login
     */
    public static void checkPersonsSections(int numOfSections, String login, int budget, MoneyCalcClient service) {
        List<SpendingSection> spendingSections = sendRequest(service.getSpendingSections(new LoginGetContainer(login))).body().getPayload();

        if (numOfSections > spendingSections.size()) {
            for (int i = 0; i < numOfSections - spendingSections.size(); i++) {
                sendRequest(service.addSpendingSection(new SpendingSectionAddContainer(login, generateSpendingSection(budget))), Status.SUCCESS).body();
            }
//            for (SpendingSection section : spendingSections) {
//                if (section.getBudget() != budget) section.setBudget(budget);
//            }
//
//            assertTrue(spendingSections.stream().allMatch(spendingSection -> spendingSection.getBudget() == budget));
//
//            sendRequest(service.saveSettings(personSettings), Status.SUCCESS).body();
        }
    }

}
