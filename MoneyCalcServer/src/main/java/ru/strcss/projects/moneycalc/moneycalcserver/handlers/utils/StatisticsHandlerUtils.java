package ru.strcss.projects.moneycalc.moneycalcserver.handlers.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.FinanceSummaryCalculationContainer;

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
        } else
            return TodayPositionRange.IN;
    }

    public static long getDaysPassed(FinanceSummaryCalculationContainer container, TodayPositionRange todayPositionRange, long daysInPeriod) {
        if (todayPositionRange.equals(TodayPositionRange.IN))
            return Period.between(container.getRangeFrom(), container.getToday()).getDays() + 1L;
        else if (todayPositionRange.equals(TodayPositionRange.BEFORE))
            return 0;
        else
            return daysInPeriod;
    }
}
