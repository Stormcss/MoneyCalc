package ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.StatisticsFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.model.exceptions.IncorrectRequestException;

import java.time.LocalDate;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.DATE_SEQUENCE_INCORRECT;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.validation.ValidationUtils.isDateSequenceValid;

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

    public static void verifyDateSequence(StatisticsFilter filter) {
        if (isDateSequenceValid(filter.getDateFrom(), filter.getDateTo())) {
            log.debug(DATE_SEQUENCE_INCORRECT);
            throw new IncorrectRequestException(DATE_SEQUENCE_INCORRECT);
        }
    }
}
