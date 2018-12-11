package ru.strcss.projects.moneycalc.moneycalcserver.handlers;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.entities.FinanceSummaryBySection;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateFinSummCalculContainer;

public class SummaryStatisticsHandlerTest {

    private SummaryStatisticsHandler statisticsHandler;
    private final int DIGITS = 2;

    @BeforeClass
    public void setUp() {
        statisticsHandler = new SummaryStatisticsHandler();
    }

    @Test
    public void testCalculateSummaryStatisticsBySections() {
        int sections = 5;
        int transactionsNum = 100;

        List<FinanceSummaryBySection> financeSummary =
                statisticsHandler.calculateSummaryStatisticsBySection(generateFinSummCalculContainer(sections, transactionsNum));

        assertNotNull(financeSummary, "FinanceSummary is null!");
        assertEquals(financeSummary.size(), sections, "FinanceSummary has wrong size!");
    }

    @Test(invocationCount = 3)
    public void testCalculateSummaryStatistics_decimalPlaces() {
        FinanceSummaryCalculationContainer finSummContainer = generateFinSummCalculContainer(50, 500);

        List<FinanceSummaryBySection> financeSummaryList = statisticsHandler.calculateSummaryStatisticsBySection(finSummContainer);

        for (FinanceSummaryBySection financeSummary : financeSummaryList) {
            int maxDecimalPlaces = maxDecimalPlaces(financeSummary.getTodayBalance(), financeSummary.getSummaryBalance());
            assertTrue(maxDecimalPlaces <= DIGITS, "Incorrect number of decimal digits! Expected " + DIGITS +
                    " but found " + maxDecimalPlaces);
        }
    }

    private int maxDecimalPlaces(double... doubles) {
        int max = 0;
        for (double doubleValue : doubles) {
            int decimalPlaces = howManyDecimalPlaces(doubleValue);
            if (decimalPlaces > max)
                max = decimalPlaces;
        }
        return max;
    }

    private int howManyDecimalPlaces(double doubleValue) {
        String str = Double.toString(doubleValue);
        int integerPlaces = str.indexOf('.');
        return str.length() - integerPlaces - 1;
    }
}