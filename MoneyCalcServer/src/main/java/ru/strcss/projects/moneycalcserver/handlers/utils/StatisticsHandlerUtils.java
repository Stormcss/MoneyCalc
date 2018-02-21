package ru.strcss.projects.moneycalcserver.handlers.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StatisticsHandlerUtils {

    public static double round(double value, int places) {
        return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }
}
