package ru.strcss.projects.moneycalcserver.handlers.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StatisticsHandlerUtils {

    public static double round(double value, int places) {
//        if (places < 0) throw new IllegalArgumentException();
//
//        BigDecimal bd = new BigDecimal(value);
//        bd = bd.setScale(places, RoundingMode.HALF_UP);
//        return bd.doubleValue();
        return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }
}
