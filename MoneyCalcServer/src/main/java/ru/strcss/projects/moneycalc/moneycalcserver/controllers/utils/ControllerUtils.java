package ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.enitities.Transaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    public static String localDate2String(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    public static LocalDate string2LocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String fillLog(String template, String... data) {
        if (data.length == 0)
            return template;
        return String.format(template, data);
    }

    public static List<SpendingSection> sortSpendingSectionList(List<SpendingSection> incomeList) {
        List<SpendingSection> spendingSectionList = new ArrayList<>(incomeList);
        spendingSectionList.sort(Comparator.comparingInt(SpendingSection::getId));

        return spendingSectionList;
    }

    public static List<Transaction> sortTransactionList(List<Transaction> incomeList) {
        List<Transaction> transactionList = new ArrayList<>(incomeList);
        transactionList.sort(Comparator.comparing(Transaction::getDate));
//        transactionList.sort(Comparator.comparing(tr -> string2LocalDate(tr.getDate())));
        return transactionList;
    }

    public static void fillDefaultValues(Transaction transaction) {
        if (transaction.getDate() == null)
            transaction.setDate(LocalDate.now());
//            transaction.setDate(localDate2String(LocalDate.now()));
        if (transaction.getCurrency() == null)
            transaction.setCurrency("RUR");
    }

    public static void fillDefaultValues(TransactionsSearchContainer searchContainer) {
        if (searchContainer.getRequiredSections() == null)
            searchContainer.setRequiredSections(Collections.emptyList());
    }
}
