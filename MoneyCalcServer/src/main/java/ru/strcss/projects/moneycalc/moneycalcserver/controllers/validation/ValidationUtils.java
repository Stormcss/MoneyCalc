package ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation;

import org.apache.commons.validator.routines.EmailValidator;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ValidationUtils {

    public static boolean isEmailValid(String email){
        return EmailValidator.getInstance().isValid(email);
    }


    public static boolean isDateCorrect(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeException e) {
            return false;
        }
        return true;
    }

    public static boolean isDateSequenceValid(LocalDate dateFrom, LocalDate dateTo) {
//        LocalDate localDateFrom = LocalDate.parse(dateFrom, DateTimeFormatter.ISO_LOCAL_DATE);
//        LocalDate localDateTo = LocalDate.parse(dateTo, DateTimeFormatter.ISO_LOCAL_DATE);
//        return localDateFrom.isBefore(localDateTo) || localDateFrom.isEqual(localDateTo);
        return dateFrom.isBefore(dateTo) || dateFrom.isEqual(dateTo);
    }
}
