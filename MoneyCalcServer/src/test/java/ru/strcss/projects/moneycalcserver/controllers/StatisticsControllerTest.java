package ru.strcss.projects.moneycalcserver.controllers;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.formatDateToString;
import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.generateDateMinus;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.generateSpendingSection;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.generateTransaction;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.savePersonGetLogin;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;
import static ru.strcss.projects.moneycalcserver.handlers.utils.StatisticsHandlerUtils.round;

public class StatisticsControllerTest extends AbstractControllerTest {

    /**
     * Accuracy of calculations - number of decimal places
     * Must be the same as one in SummaryStatisticsHandler
     */
    private final int DIGITS = 2;

    /**
     * Allowed inaccuracy of calculations - used while checking correctness of income data
     */
    private final double DELTA = 2 / StrictMath.pow(10, DIGITS);

    private int budgetPerSection = 5000;
    private int numOfSections = 3;
    private String login;

    @BeforeMethod(groups = "singleSectionCheck")
    public void preparePersonBeforeSimpleTest() {
        login = savePersonGetLogin(service);

        checkPersonsSections(numOfSections, login, budgetPerSection);

        addTransactions(0);
    }

    @Test(groups = {"singleSectionCheck"})
    public void testFinanceSummaryBySection_simple() {
        int rangeDays = 3;

        String rangeFrom = formatDateToString(generateDateMinus(ChronoUnit.DAYS, rangeDays - 1));
        String rangeTo = formatDateToString(LocalDate.now());
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(login, rangeFrom, rangeTo, Collections.singletonList(0));

        AjaxRs<List<FinanceSummaryBySection>> responseGetStats = sendRequest(service.getFinanceSummaryBySection(getContainer)).body();
        assertEquals(responseGetStats.getStatus(), Status.SUCCESS, responseGetStats.getMessage());
        FinanceSummaryBySection summary = responseGetStats.getPayload().get(0);

        assertEquals((int) summary.getMoneyLeftAll(), budgetPerSection - 900, "MoneyLeftAll is incorrect!");
        assertEquals((int) summary.getMoneySpendAll(), 900, "MoneySpendAll is incorrect!");
        assertEquals(summary.getTodayBalance(), round((double) budgetPerSection / rangeDays, DIGITS) - 200, DELTA, "TodayBalance is incorrect!");
        assertEquals(summary.getSummaryBalance(), budgetPerSection - 900, DELTA, "SummaryBalance is incorrect!");
    }

    private List<Transaction> addTransactions(int sectionID) {
        return IntStream.range(0, 3)
                .mapToObj(num -> new TransactionAddContainer(generateTransaction(generateDateMinus(ChronoUnit.DAYS, num), sectionID, (num + 2) * 100), login))
                .map(transactionAddContainer -> sendRequest(service.addTransaction(transactionAddContainer)).body())
                .filter(Objects::nonNull)
                .map(AjaxRs::getPayload)
                .collect(Collectors.toList());
    }


//    @BeforeTest
//    public void preparePerson() {
//        login = savePersonGetLogin(service);
//
//        //Adding Sections if required
//        checkPersonsSections(numOfSections, login, budgetPerSection);
//
//        //Adding new Transactions
//        for (int i = 0; i < numOfSections; i++) {
//            // FIXME: 11.02.2018 I suppose it could be done better
//            int sectionID = i;
//            List<Transaction> transactionsBySection = IntStream.range(0, numOfAddedTransactionsPerSection)
//                    .mapToObj(s -> sendRequest(service.addTransaction(new TransactionAddContainer(generateTransaction(sectionID, sectionID * 100 + 100), login))).body())
//                    .filter(Objects::nonNull)
//                    .map(AjaxRs::getPayload)
//                    .collect(Collectors.toList());
//
//            IdSumMap.put(sectionID, transactionsBySection.stream().map(Transaction::getSum).mapToInt(Integer::intValue).sum());
//        }
//        assertEquals(IdSumMap.keySet().size(), numOfSections, "Map has wrong size!");
//    }
//
//    @Test
//    public void testFinanceSummary_singleSection() {
//
//    }


    /**
     * Add Person's Sections if required (in case if Person by default has less Sections then it is required for test)
     *
     * @param numOfSections - required number of Person's sections
     * @param login         - Person's login
     */
    private void checkPersonsSections(int numOfSections, String login, int budget) {
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
