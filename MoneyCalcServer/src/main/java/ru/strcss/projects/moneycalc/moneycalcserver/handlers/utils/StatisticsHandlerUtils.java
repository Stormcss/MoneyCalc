package ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Period;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatisticsHandlerUtils {

    public static double round(double value, int places) {
        return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    public static TodayPositionRange getTodayPositionRange(FinanceSummaryCalculationContainer container) {
        if (container.getToday().isBefore(container.getRangeFrom())) {
            return TodayPositionRange.BEFORE;
        } else if (container.getToday().isAfter(container.getRangeTo())) {
            return TodayPositionRange.AFTER;
        } else return TodayPositionRange.IN;
    }

    public static long getDaysPassed(FinanceSummaryCalculationContainer container, TodayPositionRange todayPositionRange, long daysInPeriod) {
        long daysPassed;
        if (todayPositionRange.equals(TodayPositionRange.IN))
            daysPassed = Period.between(container.getRangeFrom(), container.getToday()).getDays() + 1;
        else if (todayPositionRange.equals(TodayPositionRange.BEFORE))
            daysPassed = 0;
        else
            daysPassed = daysInPeriod;
        return daysPassed;
    }
}
