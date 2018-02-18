package ru.strcss.projects.moneycalcserver.controllers;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.formatDateToString;
import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.generateSpendingSection;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.generateTransaction;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.savePersonGetLogin;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Utils.sendRequest;

@Slf4j
public class StatisticsControllerTest extends AbstractControllerTest {

    private int numOfAddedTransactionsPerSection = 3;
    private int numOfSections = 3;

    private int budgetPerSection = 5000;
    private String login;
    private Map<Integer, Integer> IdSumMap = new HashMap<>();

    @BeforeClass
    public void preparePerson() {
        login = savePersonGetLogin(service);

        //Adding Sections if required
        checkPersonsSections(numOfSections, login, budgetPerSection);

        //Adding new Transactions
        for (int i = 0; i < numOfSections; i++) {
            // FIXME: 11.02.2018 I suppose it could be done better
            int sectionID = i;
            List<Transaction> transactionsBySection = IntStream.range(0, numOfAddedTransactionsPerSection)
                    .mapToObj(s -> sendRequest(service.addTransaction(new TransactionAddContainer(generateTransaction(sectionID, sectionID * 100 + 100), login))).body())
                    .filter(Objects::nonNull)
                    .map(AjaxRs::getPayload)
                    .collect(Collectors.toList());

            IdSumMap.put(sectionID, transactionsBySection.stream().map(Transaction::getSum).mapToInt(Integer::intValue).sum());
        }
        assertEquals(IdSumMap.keySet().size(), numOfSections, "Map has wrong size!");
    }

    @Test
    public void testGetFinanceSummaryBySection() {

        int daysInPeriod = 2;

        //Request Statistics
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(login, formatDateToString(LocalDate.now()),
                formatDateToString(generateDatePlus(ChronoUnit.DAYS, daysInPeriod - 1)), new ArrayList<>(IdSumMap.keySet()));

        AjaxRs<List<FinanceSummaryBySection>> responseGetStats
                = sendRequest(service.getFinanceSummaryBySection(getContainer)).body();
        assertEquals(responseGetStats.getStatus(), Status.SUCCESS, responseGetStats.getMessage());

        //Validate Statistics
        for (FinanceSummaryBySection summary : responseGetStats.getPayload()) {

            double balancePerDay = budgetPerSection / daysInPeriod;
            int daysPassed = 1; //because only today (first day of period) is checked

            int spendToday = IdSumMap.get(summary.getSectionID());
            int spendAll = spendToday; // because all transactions are added by first day of period

            double todayBalance = balancePerDay - spendToday;
            double summaryBalance = balancePerDay * daysPassed - spendAll;

            int moneySpendAll = IdSumMap.get(summary.getSectionID());
            int moneyLeftAll = budgetPerSection - IdSumMap.get(summary.getSectionID());

            log.debug("EXPECTED: balancePerDay: {} \n spendToday: {} \n todayBalance: {} \n summaryBalance: {} \n moneySpendAll: {}",
                    balancePerDay, spendToday, todayBalance, summaryBalance, moneySpendAll);

            assertEquals(summary.getTodayBalance(), todayBalance, "todayBalance do not match!");
            assertEquals(summary.getSummaryBalance(), summaryBalance, "summaryBalance do not match!");
            assertEquals((int) summary.getMoneySpendAll(), moneySpendAll, "MoneySpendAll do not match!");
            assertEquals((int) summary.getMoneyLeftAll(), moneyLeftAll, "getMoneyLeftAll do not match!");
        }
    }

    /**
     * Add Person's Sections if required (in case if Person by default has less Sections then it is required to test)
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