package ru.strcss.projects.moneycalcserver.controllers.utils;

import lombok.extern.slf4j.Slf4j;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.Status;

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
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        return LocalDate.parse(date, formatter);
        return LocalDate.parse(date,  DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
