package ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtils {

    public static Boolean isEmailValid(String email) {
        final boolean valid = EmailValidator.getInstance().isValid(email);
        return valid;
    }

    public static boolean isDateSequenceValid(LocalDate dateFrom, LocalDate dateTo) {
        return dateFrom.isBefore(dateTo) || dateFrom.isEqual(dateTo);
    }
}
