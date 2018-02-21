package ru.strcss.projects.moneycalcserver.controllers.utils;

import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalcserver.controllers.testapi.MoneyCalcClient;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
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
        AjaxRs<List<FinanceSummaryBySection>> responseGetStats = sendRequest(service.getFinanceSummaryBySection(getContainer)).body();
        assertEquals(responseGetStats.getStatus(), Status.SUCCESS, responseGetStats.getMessage());
        return responseGetStats.getPayload().get(0);
    }

    /**
     * Add Person's Sections if required (in case if Person by default has less Sections then it is required for test)
     *
     * @param numOfSections - required number of Person's sections
     * @param login         - Person's login
     */
    public static void checkPersonsSections(int numOfSections, String login, int budget, MoneyCalcClient service) {
        Settings personSettings = sendRequest(service.getSettings(login)).body().getPayload();

        if (numOfSections > personSettings.getSections().size()) {
            for (int i = 0; i < numOfSections - personSettings.getSections().size(); i++) {
                personSettings.getSections().add(generateSpendingSection(budget, personSettings.getSections().size() + i));
            }
            for (SpendingSection section : personSettings.getSections()) {
                if (section.getBudget() != budget) section.setBudget(budget);
            }

            assertTrue(personSettings.getSections().stream().allMatch(spendingSection -> spendingSection.getBudget() == budget));

            AjaxRs<Settings> updateSettingsResponse = sendRequest(service.saveSettings(personSettings)).body();
            assertEquals(updateSettingsResponse.getStatus(), Status.SUCCESS, updateSettingsResponse.getMessage());
        }
    }

}
