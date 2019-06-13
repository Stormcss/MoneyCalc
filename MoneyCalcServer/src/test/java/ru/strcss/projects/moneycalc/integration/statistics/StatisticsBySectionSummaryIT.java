package ru.strcss.projects.moneycalc.integration.statistics;

import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.integration.AbstractIT;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SummaryBySection;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.savePersonGetToken;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.updateSettings;
import static ru.strcss.projects.moneycalc.integration.utils.StatisticsControllerTestUtils.addTransactions;
import static ru.strcss.projects.moneycalc.integration.utils.StatisticsControllerTestUtils.checkPersonsSections;
import static ru.strcss.projects.moneycalc.integration.utils.StatisticsControllerTestUtils.getFinanceSummaryBySection;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDateMinus;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;
import static ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils.StatisticsHandlerUtils.round;

public class StatisticsBySectionSummaryIT extends AbstractIT {

    /**
     * Accuracy of calculations - number of decimal places
     * Must be the same as one in SummaryStatisticsHandler
     */
    private final int DIGITS = 2;

    /**
     * Allowed inaccuracy of calculations - used while checking correctness of income data
     */
    private final double DELTA = 2 / StrictMath.pow(10, DIGITS);

    private final int budgetPerSection = 5000;
    private final int numOfSections = 3;
    private final int IN_PERIOD_SECTION_ID = 1;
    private final int OUT_PERIOD_SECTION_ID = 2;
    private String token;

    @BeforeGroups(groups = "inPeriodTest")
    public void preparePersonInPeriodTest() {
        token = savePersonGetToken(service);

        checkPersonsSections(numOfSections, budgetPerSection, service, token);
        addTransactions(service, token, IN_PERIOD_SECTION_ID, 3, 0);
    }

    @BeforeGroups(groups = "outPeriodTest")
    public void preparePersonOutPeriodTest() {
        token = savePersonGetToken(service);

        checkPersonsSections(numOfSections, budgetPerSection, service, token);
        addTransactions(service, token, OUT_PERIOD_SECTION_ID, 5, 2);
    }

    /**
     * Test case when today is before the requested period
     */
    @Test(groups = {"outPeriodTest"})
    public void singleSectionOutPeriodBeforePeriod() {

        LocalDate rangeFrom = generateDateMinus(ChronoUnit.DAYS, 7);
        LocalDate rangeTo = generateDateMinus(ChronoUnit.DAYS, 5);
        updateSettings(service, token, rangeFrom, rangeTo);

        SummaryBySection summary = getFinanceSummaryBySection(service, token, OUT_PERIOD_SECTION_ID);

        assertEquals(summary.getMoneyLeftAll(), (double) budgetPerSection, "MoneyLeftAll is incorrect!");
        assertEquals(summary.getMoneySpendAll(), 0d, "MoneySpendAll is incorrect!");
        assertEquals(summary.getTodayBalance(), 0d, "TodayBalance is incorrect!");
        assertEquals(summary.getSummaryBalance(), budgetPerSection, DELTA, "SummaryBalance is incorrect!");
    }

    /**
     * Test case when today is after the requested period
     */
    @Test(groups = {"outPeriodTest"})
    public void singleSectionOutPeriodAfterPeriod() {

        LocalDate rangeFrom = generateDateMinus(ChronoUnit.DAYS, 4);
        LocalDate rangeTo = generateDateMinus(ChronoUnit.DAYS, 2);
        updateSettings(service, token, rangeFrom, rangeTo);

        SummaryBySection summary = getFinanceSummaryBySection(service, token, OUT_PERIOD_SECTION_ID);

        assertEquals(summary.getMoneyLeftAll(), (double) budgetPerSection - 900, "MoneyLeftAll is incorrect!");
        assertEquals(summary.getMoneySpendAll(), 900d, "MoneySpendAll is incorrect!");
        assertEquals(summary.getTodayBalance(), 0d, "TodayBalance is incorrect!");
        assertEquals(summary.getSummaryBalance(), budgetPerSection - 900, DELTA, "SummaryBalance is incorrect!");
    }


    /**
     * Test case when today is the start of period of 3 days
     */
    @Test(groups = {"inPeriodTest"})
    public void singleSectionInPeriodStartOfPeriod() {
        int rangeDays = 3;
        int daysPassed = 1;

        LocalDate rangeFrom = LocalDate.now();
        LocalDate rangeTo = generateDatePlus(ChronoUnit.DAYS, rangeDays - 1);
        updateSettings(service, token, rangeFrom, rangeTo);

        SummaryBySection summary = getFinanceSummaryBySection(service, token, IN_PERIOD_SECTION_ID);

        assertEquals(summary.getMoneyLeftAll(), (double) budgetPerSection - 200, "MoneyLeftAll is incorrect!");
        assertEquals(summary.getMoneySpendAll(), 200d, "MoneySpendAll is incorrect!");
        assertEquals(summary.getTodayBalance(), round((double) budgetPerSection / rangeDays, DIGITS) - 200,
                DELTA, "TodayBalance is incorrect!");
        assertEquals(summary.getSummaryBalance(), round((double) budgetPerSection / rangeDays * daysPassed, DIGITS) - 200,
                DELTA, "SummaryBalance is incorrect!");
    }

    /**
     * Test case when today is the start of period of 1 month
     */
    @Test(groups = {"inPeriodTest"})
    public void singleSectionInPeriodStartOfPeriod1month() {
        int daysPassed = 1;
        LocalDate rangeFrom = LocalDate.now();
        LocalDate rangeTo = rangeFrom.plus(1, ChronoUnit.MONTHS);
        long rangeDays = ChronoUnit.DAYS.between(rangeFrom, rangeTo) + 1;

        updateSettings(service, token, rangeFrom, rangeTo);

        SummaryBySection summary = getFinanceSummaryBySection(service, token, IN_PERIOD_SECTION_ID);

        assertEquals(summary.getMoneyLeftAll(), (double) budgetPerSection - 200, "MoneyLeftAll is incorrect!");
        assertEquals(summary.getMoneySpendAll(), 200d, "MoneySpendAll is incorrect!");
        assertEquals(summary.getTodayBalance(), round((double) budgetPerSection / rangeDays, DIGITS) - 200,
                DELTA, "TodayBalance is incorrect!");
        assertEquals(summary.getSummaryBalance(), round((double) budgetPerSection / rangeDays * daysPassed, DIGITS) - 200,
                DELTA, "SummaryBalance is incorrect!");
    }

    /**
     * Test case when today is the middle of period
     */
    @Test(groups = {"inPeriodTest"})
    public void singleSectionInPeriodMiddleOfPeriod() {
        int rangeDays = 3;
        int daysPassed = 2;

        LocalDate rangeFrom = generateDateMinus(ChronoUnit.DAYS, 1);
        LocalDate rangeTo = generateDatePlus(ChronoUnit.DAYS, 1);
        updateSettings(service, token, rangeFrom, rangeTo);

        SummaryBySection summary = getFinanceSummaryBySection(service, token, IN_PERIOD_SECTION_ID);

        assertEquals(summary.getMoneyLeftAll(), (double) budgetPerSection - 500, "MoneyLeftAll is incorrect!");
        assertEquals(summary.getMoneySpendAll(), 500d, "MoneySpendAll is incorrect!");
        assertEquals(summary.getTodayBalance(), round((double) budgetPerSection / rangeDays, DIGITS) - 200,
                DELTA, "TodayBalance is incorrect!");
        assertEquals(summary.getSummaryBalance(), round((double) budgetPerSection / rangeDays * daysPassed, DIGITS) - 500,
                DELTA, "SummaryBalance is incorrect!");
    }


    /**
     * Test case when today is the last day of period
     */
    @Test(groups = {"inPeriodTest"})
    public void singleSectionInPeriodEndOfPeriod() {
        int rangeDays = 3;
        int daysPassed = 3;

        LocalDate rangeFrom = generateDateMinus(ChronoUnit.DAYS, rangeDays - 1);
        LocalDate rangeTo = LocalDate.now();
        updateSettings(service, token, rangeFrom, rangeTo);

        SummaryBySection summary = getFinanceSummaryBySection(service, token, IN_PERIOD_SECTION_ID);

        assertEquals(summary.getMoneyLeftAll(), (double) budgetPerSection - 900, "MoneyLeftAll is incorrect!");
        assertEquals(summary.getMoneySpendAll(), 900d, "MoneySpendAll is incorrect!");
        assertEquals(summary.getTodayBalance(), round((double) budgetPerSection / rangeDays, DIGITS) - 200,
                DELTA, "TodayBalance is incorrect!");
        assertEquals(summary.getSummaryBalance(), round((double) budgetPerSection / rangeDays * daysPassed, DIGITS) - 900,
                DELTA, "SummaryBalance is incorrect!");
    }
}
