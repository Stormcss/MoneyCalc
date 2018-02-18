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

//    public static LocalDate formatDateFromString(String date) {
//        date = "18-02-2018";
//        System.out.println("date = " + date);

//        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
//                // date/time
//                .appendPattern("yyyyMMdd")
//                // milliseconds (with 2 digits)
//                .appendValue(ChronoField.MILLI_OF_SECOND, 2)
//                // create formatter
//                .toFormatter();

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//        return LocalDate.parse(date, formatter);
//    }
    public static LocalDate formatDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
//        return LocalDate.parse(date,  DateTimeFormatter.ISO_LOCAL_DATE);
//        return LocalDate.parse("2018-02-17",  DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
