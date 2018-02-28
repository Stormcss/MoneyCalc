package ru.strcss.projects.moneycalc.integration;

import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.crudcontainers.statistics.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionAddContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalc.integration.utils.Generator;
import ru.strcss.projects.moneycalc.integration.utils.StatisticsControllerTestUtils;
import ru.strcss.projects.moneycalc.integration.utils.Utils;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils;
import ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils.StatisticsHandlerUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.testng.Assert.assertEquals;

public class StatisticsControllerIT extends AbstractControllerIT {

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

    @BeforeGroups(groups = "inPeriodTest")
    public void preparePerson_InPeriodTest() {
        System.out.println("preparePerson_InPeriodTest!");
        login = Utils.savePersonGetLogin(service);
        StatisticsControllerTestUtils.checkPersonsSections(numOfSections, login, budgetPerSection, service);
        addTransactions(0, 3, 0);
    }

    @BeforeGroups(groups = "outPeriodTest")
    public void preparePerson_OutPeriodTest() {
        System.out.println("preparePerson_BeforePeriodTest!");
        login = Utils.savePersonGetLogin(service);
        StatisticsControllerTestUtils.checkPersonsSections(numOfSections, login, budgetPerSection, service);
        addTransactions(1, 5, 2);
    }


    /**
     * Test case when today before the requested period
     */
    @Test(groups = {"outPeriodTest"})
    public void singleSection_outPeriod_beforePeriod() {
        System.out.println("singleSection_outPeriod_beforePeriod");

        String rangeFrom = ControllerUtils.formatDateToString(GenerationUtils.generateDateMinus(ChronoUnit.DAYS, 5));
        String rangeTo = ControllerUtils.formatDateToString(GenerationUtils.generateDateMinus(ChronoUnit.DAYS, 7));
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(login, rangeFrom, rangeTo, Collections.singletonList(1));

        FinanceSummaryBySection summary = StatisticsControllerTestUtils.getFinanceSummaryBySection(getContainer, service);

        assertEquals((int) summary.getMoneyLeftAll(), budgetPerSection, "MoneyLeftAll is incorrect!");
        assertEquals((int) summary.getMoneySpendAll(), 0, "MoneySpendAll is incorrect!");
        assertEquals(summary.getTodayBalance(), 0d, "TodayBalance is incorrect!");
        assertEquals(summary.getSummaryBalance(), budgetPerSection, DELTA, "SummaryBalance is incorrect!");
    }

    /**
     * Test case when today after the requested period
     */
    @Test(groups = {"outPeriodTest"})
    public void singleSection_outPeriod_afterPeriod() {
        System.out.println("singleSection_outPeriod_afterPeriod");

        String rangeFrom = ControllerUtils.formatDateToString(GenerationUtils.generateDateMinus(ChronoUnit.DAYS, 4));
        String rangeTo = ControllerUtils.formatDateToString(GenerationUtils.generateDateMinus(ChronoUnit.DAYS, 2));
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(login, rangeFrom, rangeTo, Collections.singletonList(1));

        FinanceSummaryBySection summary = StatisticsControllerTestUtils.getFinanceSummaryBySection(getContainer, service);

        assertEquals((int) summary.getMoneyLeftAll(), budgetPerSection - 900, "MoneyLeftAll is incorrect!");
        assertEquals((int) summary.getMoneySpendAll(), 900, "MoneySpendAll is incorrect!");
        assertEquals(summary.getTodayBalance(), 0d, "TodayBalance is incorrect!");
        assertEquals(summary.getSummaryBalance(), budgetPerSection  - 900, DELTA, "SummaryBalance is incorrect!");
    }


    /**
     * Test case when today is the start of period
     */
    @Test(groups = {"inPeriodTest"})
    public void singleSection_inPeriod_startOfPeriod() {
        System.out.println("singleSection_startOfPeriod");
        int rangeDays = 3;
        int daysPassed = 1;

        String rangeFrom = ControllerUtils.formatDateToString(LocalDate.now());
        String rangeTo = ControllerUtils.formatDateToString(GenerationUtils.generateDatePlus(ChronoUnit.DAYS, rangeDays - 1));
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(login, rangeFrom, rangeTo, Collections.singletonList(0));

        FinanceSummaryBySection summary = StatisticsControllerTestUtils.getFinanceSummaryBySection(getContainer, service);

        assertEquals((int) summary.getMoneyLeftAll(), budgetPerSection - 200, "MoneyLeftAll is incorrect!");
        assertEquals((int) summary.getMoneySpendAll(), 200, "MoneySpendAll is incorrect!");
        Assert.assertEquals(summary.getTodayBalance(), StatisticsHandlerUtils.round((double) budgetPerSection / rangeDays, DIGITS) - 200, DELTA, "TodayBalance is incorrect!");
        Assert.assertEquals(summary.getSummaryBalance(), StatisticsHandlerUtils.round((double) budgetPerSection / rangeDays * daysPassed, DIGITS) - 200, DELTA, "SummaryBalance is incorrect!");
    }

    /**
     * Test case when today is the middle of period
     */
    @Test(groups = {"inPeriodTest"})
    public void singleSection_inPeriod_middleOfPeriod() {
        System.out.println("singleSection_middleOfPeriod");
        int rangeDays = 3;
        int daysPassed = 2;

        String rangeFrom = ControllerUtils.formatDateToString(GenerationUtils.generateDateMinus(ChronoUnit.DAYS, 1));
        String rangeTo = ControllerUtils.formatDateToString(GenerationUtils.generateDatePlus(ChronoUnit.DAYS, 1));
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(login, rangeFrom, rangeTo, Collections.singletonList(0));

        FinanceSummaryBySection summary = StatisticsControllerTestUtils.getFinanceSummaryBySection(getContainer, service);

        assertEquals((int) summary.getMoneyLeftAll(), budgetPerSection - 500, "MoneyLeftAll is incorrect!");
        assertEquals((int) summary.getMoneySpendAll(), 500, "MoneySpendAll is incorrect!");
        Assert.assertEquals(summary.getTodayBalance(), StatisticsHandlerUtils.round((double) budgetPerSection / rangeDays, DIGITS) - 200, DELTA, "TodayBalance is incorrect!");
        Assert.assertEquals(summary.getSummaryBalance(), StatisticsHandlerUtils.round((double) budgetPerSection / rangeDays * daysPassed, DIGITS) - 500, DELTA, "SummaryBalance is incorrect!");
    }

    /**
     * Test case when today is the last day of period
     */
    @Test(groups = {"inPeriodTest"})
    public void singleSection_inPeriod_endOfPeriod() {
        System.out.println("singleSection_endOfPeriod");
        int rangeDays = 3;
        int daysPassed = 3;

        String rangeFrom = ControllerUtils.formatDateToString(GenerationUtils.generateDateMinus(ChronoUnit.DAYS, rangeDays - 1));
        String rangeTo = ControllerUtils.formatDateToString(LocalDate.now());
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(login, rangeFrom, rangeTo, Collections.singletonList(0));

        FinanceSummaryBySection summary = StatisticsControllerTestUtils.getFinanceSummaryBySection(getContainer, service);

        assertEquals((int) summary.getMoneyLeftAll(), budgetPerSection - 900, "MoneyLeftAll is incorrect!");
        assertEquals((int) summary.getMoneySpendAll(), 900, "MoneySpendAll is incorrect!");
        Assert.assertEquals(summary.getTodayBalance(), StatisticsHandlerUtils.round((double) budgetPerSection / rangeDays, DIGITS) - 200, DELTA, "TodayBalance is incorrect!");
        Assert.assertEquals(summary.getSummaryBalance(), StatisticsHandlerUtils.round((double) budgetPerSection / rangeDays * daysPassed, DIGITS) - 900, DELTA, "SummaryBalance is incorrect!");
    }


    //    private List<Transaction> addTransactions(int sectionID, int minusMax, int minusMin) {
//        return IntStream.range(minusMin, minusMax)
//                .mapToObj(num -> new TransactionAddContainer(login, generateTransaction(generateDateMinus(ChronoUnit.DAYS, num), sectionID, (num + 2) * 100)))
//                .map(transactionAddContainer -> sendRequest(service.addTransaction(transactionAddContainer)).body())
//                .filter(Objects::nonNull)
//                .map(AjaxRs::getPayload)
//                .collect(Collectors.toList());
//    }
    private List<Transaction> addTransactions(int sectionID, int minusMax, int minusMin) {
        List<Integer> sums = IntStream.range(0, minusMax - minusMin)
                .map(num -> (num + 2) * 100)
                .boxed()
                .collect(Collectors.toList());

        List<TransactionAddContainer> addContainers = new ArrayList<>();

        for (Integer sum : sums) {
            addContainers.add(new TransactionAddContainer(login, Generator.generateTransaction(GenerationUtils.generateDateMinus(ChronoUnit.DAYS, minusMin), sectionID, sum)));
            minusMin++;
        }

        return addContainers.stream()
                .map(transactionAddContainer -> Utils.sendRequest(service.addTransaction(transactionAddContainer)).body())
                .filter(Objects::nonNull)
                .map(AjaxRs::getPayload)
                .collect(Collectors.toList());
    }

}
