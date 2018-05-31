package ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils;

import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils.StatisticsHandlerUtils.*;

public class StatisticsHandlerUtilsTest {
    @Test
    public void testRound() throws Exception {
        assertEquals(round(3.1415926, 2), 3.14);
        assertEquals(round(3.1415926, 3), 3.142);
        assertEquals(round(3.1415926, 4), 3.1416);
    }

    @Test
    public void testGetTodayPositionRange_before() throws Exception {
        FinanceSummaryCalculationContainer calculationContainer = FinanceSummaryCalculationContainer.builder()
                .today(LocalDate.now())
                .rangeFrom(LocalDate.now().plus(1, ChronoUnit.DAYS))
                .rangeTo(LocalDate.now().plus(2, ChronoUnit.DAYS))
                .build();

        assertEquals(getTodayPositionRange(calculationContainer), TodayPositionRange.BEFORE);
    }

    @Test
    public void testGetTodayPositionRange_in() throws Exception {
        FinanceSummaryCalculationContainer calculationContainer = FinanceSummaryCalculationContainer.builder()
                .today(LocalDate.now())
                .rangeFrom(LocalDate.now().minus(1, ChronoUnit.DAYS))
                .rangeTo(LocalDate.now().plus(2, ChronoUnit.DAYS))
                .build();

        assertEquals(getTodayPositionRange(calculationContainer), TodayPositionRange.IN);
    }

    @Test
    public void testGetTodayPositionRange_after() throws Exception {
        FinanceSummaryCalculationContainer calculationContainer = FinanceSummaryCalculationContainer.builder()
                .today(LocalDate.now().plus(3, ChronoUnit.DAYS))
                .rangeFrom(LocalDate.now().minus(1, ChronoUnit.DAYS))
                .rangeTo(LocalDate.now().plus(1, ChronoUnit.DAYS))
                .build();

        assertEquals(getTodayPositionRange(calculationContainer), TodayPositionRange.AFTER);
    }

    @Test
    public void testGetDaysPassed_before() throws Exception {
        assertEquals(getDaysPassed(null, TodayPositionRange.BEFORE, 30), 0);
    }

    @Test
    public void testGetDaysPassed_in() throws Exception {
        FinanceSummaryCalculationContainer calculationContainer = FinanceSummaryCalculationContainer.builder()
                .rangeFrom(LocalDate.now())
                .today(LocalDate.now().plus(1, ChronoUnit.DAYS))
                .build();

        assertEquals(getDaysPassed(calculationContainer, TodayPositionRange.IN, 30), 2);
    }

    @Test
    public void testGetDaysPassed_after() throws Exception {
        assertEquals(getDaysPassed(null, TodayPositionRange.AFTER, 30), 30);
    }

}