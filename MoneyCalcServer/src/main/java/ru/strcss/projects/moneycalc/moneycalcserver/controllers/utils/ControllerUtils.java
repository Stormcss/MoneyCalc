package ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Status;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;

import java.time.LocalDate;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static String fillLog(String template, String... data) {
        if (data.length == 0)
            return template;
        return String.format(template, data);
    }

    public static void fillDefaultValues(Transaction transaction) {
        if (transaction.getDate() == null)
            transaction.setDate(LocalDate.now());
        if (transaction.getCurrency() == null)
            transaction.setCurrency("RUR");
    }
}
