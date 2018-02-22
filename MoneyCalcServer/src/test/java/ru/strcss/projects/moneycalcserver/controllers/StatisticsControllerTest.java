package ru.strcss.projects.moneycalcserver.controllers;

import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.statistics.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.formatDateToString;
import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.generateDateMinus;
import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;
import static ru.strcss.projects.moneycalcserver.controllers.utils.Generator.generateTransaction;
import static ru.strcss.projects.moneycalcserver.controllers.utils.StatisticsControllerTestUtils.checkPersonsSections;
import static ru.strcss.projects.moneycalcserver.controllers.utils.StatisticsControllerTestUtils.getFinanceSummaryBySection;
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

    @BeforeGroups(groups = "singleSectionCheck")
    public void preparePersonBeforeSimpleTest() {
        System.out.println("preparePersonBeforeSimpleTest!");
        login = savePersonGetLogin(service);
        checkPersonsSections(numOfSections, login, budgetPerSection, service);
        addTransactions(0);
    }

    /**
     * Test case when today is the start of period
     */
    @Test(groups = {"singleSectionCheck"})
    public void singleSection_startOfPeriod() {
        int rangeDays = 3;
        int daysPassed = 1;

        String rangeFrom = formatDateToString(LocalDate.now());
        String rangeTo = formatDateToString(generateDatePlus(ChronoUnit.DAYS, rangeDays - 1));
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(login, rangeFrom, rangeTo, Collections.singletonList(0));

        FinanceSummaryBySection summary = getFinanceSummaryBySection(getContainer, service);

        assertEquals((int) summary.getMoneyLeftAll(), budgetPerSection - 200, "MoneyLeftAll is incorrect!");
        assertEquals((int) summary.getMoneySpendAll(), 200, "MoneySpendAll is incorrect!");
        assertEquals(summary.getTodayBalance(), round((double) budgetPerSection / rangeDays, DIGITS) - 200, DELTA, "TodayBalance is incorrect!");
        assertEquals(summary.getSummaryBalance(), round((double) budgetPerSection / rangeDays * daysPassed, DIGITS) - 200, DELTA, "SummaryBalance is incorrect!");
    }

    /**
     * Test case when today is the middle of period
     */
    @Test(groups = {"singleSectionCheck"})
    public void singleSection_middleOfPeriod() {
        int rangeDays = 3;
        int daysPassed = 2;

        String rangeFrom = formatDateToString(generateDateMinus(ChronoUnit.DAYS, 1));
        String rangeTo = formatDateToString(generateDatePlus(ChronoUnit.DAYS, 1));
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(login, rangeFrom, rangeTo, Collections.singletonList(0));

        FinanceSummaryBySection summary = getFinanceSummaryBySection(getContainer, service);

        assertEquals((int) summary.getMoneyLeftAll(), budgetPerSection - 500, "MoneyLeftAll is incorrect!");
        assertEquals((int) summary.getMoneySpendAll(), 500, "MoneySpendAll is incorrect!");
        assertEquals(summary.getTodayBalance(), round((double) budgetPerSection / rangeDays, DIGITS) - 200, DELTA, "TodayBalance is incorrect!");
        assertEquals(summary.getSummaryBalance(), round((double) budgetPerSection / rangeDays * daysPassed, DIGITS) - 500, DELTA, "SummaryBalance is incorrect!");
    }

    /**
     * Test case when today is the last day of period
     */
    @Test(groups = {"singleSectionCheck"})
    public void singleSection_endOfPeriod() {
        int rangeDays = 3;
        int daysPassed = 3;

        String rangeFrom = formatDateToString(generateDateMinus(ChronoUnit.DAYS, rangeDays - 1));
        String rangeTo = formatDateToString(LocalDate.now());
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(login, rangeFrom, rangeTo, Collections.singletonList(0));

        FinanceSummaryBySection summary = getFinanceSummaryBySection(getContainer, service);

        assertEquals((int) summary.getMoneyLeftAll(), budgetPerSection - 900, "MoneyLeftAll is incorrect!");
        assertEquals((int) summary.getMoneySpendAll(), 900, "MoneySpendAll is incorrect!");
        assertEquals(summary.getTodayBalance(), round((double) budgetPerSection / rangeDays, DIGITS) - 200, DELTA, "TodayBalance is incorrect!");
        assertEquals(summary.getSummaryBalance(), round((double) budgetPerSection / rangeDays * daysPassed, DIGITS) - 900, DELTA, "SummaryBalance is incorrect!");
    }


    private List<Transaction> addTransactions(int sectionID) {
        return IntStream.range(0, 3)
                .mapToObj(num -> new TransactionAddContainer(login, generateTransaction(generateDateMinus(ChronoUnit.DAYS, num), sectionID, (num + 2) * 100)))
                .map(transactionAddContainer -> sendRequest(service.addTransaction(transactionAddContainer)).body())
                .filter(Objects::nonNull)
                .map(AjaxRs::getPayload)
                .collect(Collectors.toList());
    }

}
