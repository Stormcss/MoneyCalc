package ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils;

import lombok.extern.slf4j.Slf4j;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class ControllerUtils {

    public static <E> AjaxRs<E> responseError(String message) {
        return AjaxRs.<E>builder()
                .message(message)
                .status(Status.ERROR)
                .build();
    }

    public static <E> AjaxRs<E> responseSuccess(String message, E payload) {
        return AjaxRs.<E>builder()
                .message(message)
                .status(Status.SUCCESS)
                .payload(payload)
                .build();
    }

    public static String formatDateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    public static LocalDate formatDateFromString(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String formatLogin(String login) {
        return login.replace("\"", "");
    }

    public static String fillLog(String template, String... data) {
        if (data.length == 0)
            return template;
        return String.format(template, data);
    }

    /**
     * Check if Transaction is fully empty
     */
    public static boolean isTransactionEmpty(Transaction transaction) {
        return isNull(transaction.getCurrency()) && isNull(transaction.getDate()) && isNull(transaction.getDescription())
                && isNull(transaction.getSectionID()) && isNull(transaction.getSum());
    }

    /**
     * Check if SpendingSection is fully empty
     */
    public static boolean isSpendingSectionEmpty(SpendingSection section) {
        return isNull(section.getBudget()) && isNull(section.getIsAdded()) && isNull(section.getName());
    }

    private static boolean isNull(Object obj) {
        return obj == null;
    }
}
