package ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils;

import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.FinanceSummaryCalculationContainer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils.StatisticsHandlerUtils.getDaysPassed;
import static ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils.StatisticsHandlerUtils.getTodayPositionRange;
import static ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils.StatisticsHandlerUtils.round;

public class StatisticsHandlerUtilsTest {
    @Test
    void testRound() {
        assertEquals(round(3.1415926, 2), 3.14);
        assertEquals(round(3.1415926, 3), 3.142);
        assertEquals(round(3.1415926, 4), 3.1416);
    }

    @Test
    void testGetTodayPositionRangeBefore() {
        FinanceSummaryCalculationContainer calculationContainer = FinanceSummaryCalculationContainer.builder()
                .today(LocalDate.now())
                .rangeFrom(LocalDate.now().plus(1, ChronoUnit.DAYS))
                .rangeTo(LocalDate.now().plus(2, ChronoUnit.DAYS))
                .build();

        assertEquals(getTodayPositionRange(calculationContainer), TodayPositionRange.BEFORE);
    }

    @Test
    void testGetTodayPositionRangeIn() {
        FinanceSummaryCalculationContainer calculationContainer = FinanceSummaryCalculationContainer.builder()
                .today(LocalDate.now())
                .rangeFrom(LocalDate.now().minus(1, ChronoUnit.DAYS))
                .rangeTo(LocalDate.now().plus(2, ChronoUnit.DAYS))
                .build();

        assertEquals(getTodayPositionRange(calculationContainer), TodayPositionRange.IN);
    }

    @Test
    void testGetTodayPositionRangeAfter() {
        FinanceSummaryCalculationContainer calculationContainer = FinanceSummaryCalculationContainer.builder()
                .today(LocalDate.now().plus(3, ChronoUnit.DAYS))
                .rangeFrom(LocalDate.now().minus(1, ChronoUnit.DAYS))
                .rangeTo(LocalDate.now().plus(1, ChronoUnit.DAYS))
                .build();

        assertEquals(getTodayPositionRange(calculationContainer), TodayPositionRange.AFTER);
    }

    @Test
    void testGetDaysPassedBefore() {
        assertEquals(getDaysPassed(null, TodayPositionRange.BEFORE, 30), 0);
    }

    @Test
    void testGetDaysPassedIn() {
        FinanceSummaryCalculationContainer calculationContainer = FinanceSummaryCalculationContainer.builder()
                .rangeFrom(LocalDate.now())
                .today(LocalDate.now().plus(1, ChronoUnit.DAYS))
                .build();

        assertEquals(getDaysPassed(calculationContainer, TodayPositionRange.IN, 30), 2);
    }

    @Test
    void testGetDaysPassedAfter() {
        assertEquals(getDaysPassed(null, TodayPositionRange.AFTER, 30), 30);
    }

}