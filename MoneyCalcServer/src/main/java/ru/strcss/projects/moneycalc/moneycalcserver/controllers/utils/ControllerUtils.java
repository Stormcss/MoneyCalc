package ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class ControllerUtils {

    public static <E> ResponseEntity<MoneyCalcRs<E>> responseSuccess(String message, E payload) {
        return new ResponseEntity<>(new MoneyCalcRs<>(Status.SUCCESS, payload, message), HttpStatus.OK);
    }

    public static <E> ResponseEntity<MoneyCalcRs<E>> responseSuccess(String message, E payload, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(new MoneyCalcRs<>(Status.SUCCESS, payload, message), headers, HttpStatus.OK);
    }

    public static <E> ResponseEntity<MoneyCalcRs<E>> responseError(String message) {
        return new ResponseEntity<>(new MoneyCalcRs<>(Status.ERROR, null, message), HttpStatus.BAD_REQUEST);
    }

    public static <E> ResponseEntity<MoneyCalcRs<E>> responseError(String message, HttpStatus httpStatus) {
        return new ResponseEntity<>(new MoneyCalcRs<>(Status.ERROR, null, message), httpStatus);
    }

    public static <E> ResponseEntity<MoneyCalcRs<E>> responseError(String message, MultiValueMap<String, String> headers) {
        return new ResponseEntity<>(new MoneyCalcRs<>(Status.ERROR, null, message), headers, HttpStatus.BAD_REQUEST);
    }

    public static <E> ResponseEntity<MoneyCalcRs<E>> responseError(String message, MultiValueMap<String, String> headers, HttpStatus httpStatus) {
        return new ResponseEntity<>(new MoneyCalcRs<>(Status.ERROR, null, message), headers, httpStatus);
    }

    public static String formatDateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    public static LocalDate formatDateFromString(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
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

    private static boolean isNull(java.io.Serializable obj) {
        return obj == null;
    }
}
