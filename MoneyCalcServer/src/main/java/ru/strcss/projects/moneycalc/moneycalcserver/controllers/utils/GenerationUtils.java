package ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils;

import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.enitities.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.formatDateToString;

public class GenerationUtils {

    public static LocalDate generateDatePlus(TemporalUnit unit, int count) {
        return LocalDate.now().plus(count, unit);
    }

    public static LocalDate generateDateMinus(TemporalUnit unit, int count) {
        return LocalDate.now().minus(count, unit);
    }

    public static LocalDate currentDate() {
        return LocalDate.now();
    }

    public static String currentDateString() {
        return formatDateToString(LocalDate.now());
    }

    public static Transaction generateTransactionID(Transaction transaction) {
        transaction.set_id(generateUUID());
        return transaction;
    }

    public static Transaction generateTransactionID(Transaction transaction, String id) {
        transaction.set_id(id);
        return transaction;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    public static Person getRegisteringPerson(String login, Credentials credentials){
        return Person.builder()
                .ID(login)
                .access(credentials.getAccess())
                .identifications(credentials.getIdentifications())
                .settings(generateRegisteringSettings(login))
                .finance(Finance.builder()
                        ._id(login)
                        .financeSummary(FinanceSummary.builder()
                                ._id(login)
                                .financeSections(new ArrayList<>())
                                .build())
                        .build())
                .build();
    }
    private static Settings generateRegisteringSettings(String login){
        return Settings.builder()
                .login(login)
                .periodFrom(currentDateString())
                .periodTo(formatDateToString(generateDatePlus(ChronoUnit.MONTHS, 1)))
                .sections(Arrays.asList(SpendingSection.builder()
                                .id(0)
                                .isAdded(true)
                                .isRemoved(false)
                                .budget(5000)
                                .name("Еда")
                                .build(),
                        SpendingSection.builder()
                                .id(1)
                                .budget(5000)
                                .isAdded(true)
                                .isRemoved(false)
                                .name("Прочее")
                                .build()))
                .build();
    }

    public static PersonTransactions getRegisteringPersonTransactions(String login){
            return PersonTransactions.builder()
                    .login(login)
                    .transactions(new ArrayList<>())
                    .build();
    }
}
