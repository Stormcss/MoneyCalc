package ru.strcss.projects.moneycalcserver.controllers.utils;

import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.time.LocalDate;
import java.time.temporal.TemporalUnit;
import java.util.UUID;

import static ru.strcss.projects.moneycalcserver.controllers.utils.ControllerUtils.formatDateToString;

public class GenerationUtils {

    public static LocalDate generateDatePlus(TemporalUnit unit, int count) {
        return LocalDate.now().plus(count, unit);
    }

    public static LocalDate generateDateMinus(TemporalUnit unit, int count) {
        return LocalDate.now().minus(count, unit);
    }

    public static LocalDate currentDate() {
        return LocalDate.now();
    }

    public static String currentDateString() {
        return formatDateToString(LocalDate.now());
    }

    public static Transaction generateTransactionID(Transaction transaction) {
        transaction.set_id(generateUUID());
        return transaction;
    }

    public static Transaction generateTransactionID(Transaction transaction, String id) {
        transaction.set_id(id);
        return transaction;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
