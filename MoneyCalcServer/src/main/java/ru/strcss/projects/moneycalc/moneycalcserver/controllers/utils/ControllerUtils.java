package ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;

import java.time.LocalDate;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ControllerUtils {

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
