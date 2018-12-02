package ru.strcss.projects.moneycalc.integration;

import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.statistics.FinanceSummaryFilter;
import ru.strcss.projects.moneycalc.entities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.entities.Transaction;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Collections.singletonList;
import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.savePersonGetToken;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.sendRequest;
import static ru.strcss.projects.moneycalc.integration.utils.StatisticsControllerTestUtils.checkPersonsSections;
import static ru.strcss.projects.moneycalc.integration.utils.StatisticsControllerTestUtils.getFinanceSummaryBySection;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDateMinus;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;
import static ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils.StatisticsHandlerUtils.round;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransaction;

public class StatisticsControllerIT extends AbstractIT {

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
    public void preparePerson_InPeriodTest() {
        token = savePersonGetToken(service);

        checkPersonsSections(numOfSections, budgetPerSection, service, token);
        List<Transaction> transactions = addTransactions(IN_PERIOD_SECTION_ID, 3, 0);
        System.out.println("transactions = " + transactions);
    }

    @BeforeGroups(groups = "outPeriodTest")
    public void preparePerson_OutPeriodTest() {
        token = savePersonGetToken(service);

        checkPersonsSections(numOfSections, budgetPerSection, service, token);
        List<Transaction> transactions = addTransactions(OUT_PERIOD_SECTION_ID, 5, 2);
        System.out.println("transactions = " + transactions);
    }

    /**
     * Test case when today is before the requested period
     */
    @Test(groups = {"outPeriodTest"})
    public void singleSection_outPeriod_beforePeriod() {

        LocalDate rangeFrom = generateDateMinus(ChronoUnit.DAYS, 7);
        LocalDate rangeTo = generateDateMinus(ChronoUnit.DAYS, 5);
        FinanceSummaryFilter getContainer = new FinanceSummaryFilter(rangeFrom, rangeTo, singletonList(OUT_PERIOD_SECTION_ID));

        FinanceSummaryBySection summary = getFinanceSummaryBySection(getContainer, service, token);

        assertEquals(summary.getMoneyLeftAll(), (double) budgetPerSection, "MoneyLeftAll is incorrect!");
        assertEquals(summary.getMoneySpendAll(), 0d, "MoneySpendAll is incorrect!");
        assertEquals(summary.getTodayBalance(), 0d, "TodayBalance is incorrect!");
        assertEquals(summary.getSummaryBalance(), budgetPerSection, DELTA, "SummaryBalance is incorrect!");
    }

    /**
     * Test case when today is after the requested period
     */
    @Test(groups = {"outPeriodTest"})
    public void singleSection_outPeriod_afterPeriod() {

        LocalDate rangeFrom = generateDateMinus(ChronoUnit.DAYS, 4);
        LocalDate rangeTo = generateDateMinus(ChronoUnit.DAYS, 2);
        FinanceSummaryFilter getContainer = new FinanceSummaryFilter(rangeFrom, rangeTo, singletonList(OUT_PERIOD_SECTION_ID));

        FinanceSummaryBySection summary = getFinanceSummaryBySection(getContainer, service, token);

        assertEquals(summary.getMoneyLeftAll(), (double) budgetPerSection - 900, "MoneyLeftAll is incorrect!");
        assertEquals(summary.getMoneySpendAll(), 900d, "MoneySpendAll is incorrect!");
        assertEquals(summary.getTodayBalance(), 0d, "TodayBalance is incorrect!");
        assertEquals(summary.getSummaryBalance(), budgetPerSection - 900, DELTA, "SummaryBalance is incorrect!");
    }


    /**
     * Test case when today is the start of period of 3 days
     */
    @Test(groups = {"inPeriodTest"})
    public void singleSection_inPeriod_startOfPeriod() {
        int rangeDays = 3;
        int daysPassed = 1;

        LocalDate rangeFrom = LocalDate.now();
        LocalDate rangeTo = generateDatePlus(ChronoUnit.DAYS, rangeDays - 1);
        FinanceSummaryFilter getContainer = new FinanceSummaryFilter(rangeFrom, rangeTo, singletonList(IN_PERIOD_SECTION_ID));

        FinanceSummaryBySection summary = getFinanceSummaryBySection(getContainer, service, token);

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
    public void singleSection_inPeriod_startOfPeriod_1month() {
        LocalDate dateFrom = LocalDate.now();
        long rangeDays = ChronoUnit.DAYS.between(dateFrom, dateFrom.plus(1, ChronoUnit.MONTHS)) + 1;
        int daysPassed = 1;

        LocalDate rangeTo = dateFrom.plus(1, ChronoUnit.MONTHS);
        FinanceSummaryFilter getContainer = new FinanceSummaryFilter(dateFrom, rangeTo, singletonList(IN_PERIOD_SECTION_ID));

        FinanceSummaryBySection summary = getFinanceSummaryBySection(getContainer, service, token);

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
    public void singleSection_inPeriod_middleOfPeriod() {
        int rangeDays = 3;
        int daysPassed = 2;

        LocalDate rangeFrom = generateDateMinus(ChronoUnit.DAYS, 1);
        LocalDate rangeTo = generateDatePlus(ChronoUnit.DAYS, 1);
        FinanceSummaryFilter getContainer = new FinanceSummaryFilter(rangeFrom, rangeTo, singletonList(IN_PERIOD_SECTION_ID));

        FinanceSummaryBySection summary = getFinanceSummaryBySection(getContainer, service, token);

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
    public void singleSection_inPeriod_endOfPeriod() {
        int rangeDays = 3;
        int daysPassed = 3;

        LocalDate rangeFrom = generateDateMinus(ChronoUnit.DAYS, rangeDays - 1);
        LocalDate rangeTo = LocalDate.now();
        FinanceSummaryFilter getContainer = new FinanceSummaryFilter(rangeFrom, rangeTo, singletonList(IN_PERIOD_SECTION_ID));

        FinanceSummaryBySection summary = getFinanceSummaryBySection(getContainer, service, token);

        assertEquals(summary.getMoneyLeftAll(), (double) budgetPerSection - 900, "MoneyLeftAll is incorrect!");
        assertEquals(summary.getMoneySpendAll(), 900d, "MoneySpendAll is incorrect!");
        assertEquals(summary.getTodayBalance(), round((double) budgetPerSection / rangeDays, DIGITS) - 200,
                DELTA, "TodayBalance is incorrect!");
        assertEquals(summary.getSummaryBalance(), round((double) budgetPerSection / rangeDays * daysPassed, DIGITS) - 900,
                DELTA, "SummaryBalance is incorrect!");
    }

    /**
     * Add transactions for test suite
     * Both minusMax and minusMin will be used to calculate date of adding Transaction and sum for Transaction
     *
     * @param sectionId - sectionId of added Transaction
     * @param minusMax  - maximum sum for Transaction (will be multiplied by 100).
     * @param minusMin  - minimum sum for Transaction (will be multiplied by 100)
     * @return List of already added Transactions
     */
    private List<Transaction> addTransactions(int sectionId, int minusMax, int minusMin) {
        List<Integer> sums = IntStream.range(0, minusMax - minusMin)
                .map(num -> (num + 2) * 100)
                .boxed()
                .collect(Collectors.toList());

        List<Transaction> transactions = new ArrayList<>();

        for (Integer sum : sums) {
            transactions.add(generateTransaction(generateDateMinus(ChronoUnit.DAYS, minusMin), sectionId, sum,
                    null, null, null));
            minusMin++;
        }

        return transactions.stream()
                .map(transaction -> sendRequest(service.addTransaction(token, transaction)).body())
                .filter(Objects::nonNull)
                .map(MoneyCalcRs::getPayload)
                .collect(Collectors.toList());
    }

}
