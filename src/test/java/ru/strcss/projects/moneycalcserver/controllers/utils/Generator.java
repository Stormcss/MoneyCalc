package ru.strcss.projects.moneycalcserver.controllers.utils;

import ru.strcss.projects.moneycalcserver.enitities.dto.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Generator {

//    @Autowired
//    MongoOperations mongoOperations;

    public static Person personGenerator(String login) {
        return generatePerson(login);
    }

    public static Person personGenerator() {
        return generatePerson(UUID());
    }

    private static Person generatePerson(String login) {
        return Person.builder()
                .ID(login)
                .access(Access.builder()
                        .login(login)
                        .password("qwerty")
                        .email(login + "@mail.ru")
                        .build())
                .identifications(Identifications.builder()
                        ._id(login)
                        .name("Вася")
                        .build())
                .settings(generateSettings(login))
                .finance(Finance.builder()
                        ._id(login)
                        .financeSummary(generateFinanceSummary(login))
                        .financeStatistics(generateFinanceStatistics(login))
                        .build())
                .build();
    }

    public static Settings generateSettings(String login) {
        return Settings.builder()
                ._id(login)
                .periodFrom(createDate())
                .periodTo(generateDatePlus(ChronoUnit.MONTHS, 1))
                .sections(Arrays.asList(SettingsSection.builder()
                                .name(String.valueOf(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE)))
                                .ID(login)
                                .build(),
                        SettingsSection.builder()
                                .name(String.valueOf(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE)))
                                .ID(login)
                                .build()))
                .build();
    }

    public static Identifications generateIdentifications(String login) {
        return Identifications.builder()
                ._id(login)
                .name(UUID())
                .build();
    }

    public static FinanceStatistics generateFinanceStatistics(String login) {
        return FinanceStatistics.builder()
                ._id(login)
                .transactions(generateTransactions(1))
                .build();
    }

    public static FinanceSummary generateFinanceSummary(String login) {
        return FinanceSummary.builder()
                ._id(login)
                .daysInMonth(ThreadLocalRandom.current().nextInt(29, 31))
                .daysSpend(ThreadLocalRandom.current().nextInt(0, 29))
                .financeSections(generateSettingsSectionsList(3))
                .build();
    }

    public static List<Transaction> generateTransactions(int count) {
        return Stream.generate(Generator::generateTransaction)
                .limit(count)
                .collect(Collectors.toList());
    }

    public static List<FinanceSummaryBySection> generateSettingsSectionsList(int count) {
        return Stream.generate(Generator::generateSettingsSection)
                .limit(count)
                .collect(Collectors.toList());
    }

    public static FinanceSummaryBySection generateSettingsSection() {
        return FinanceSummaryBySection.builder()
                .summaryBalance(ThreadLocalRandom.current().nextDouble(Integer.MIN_VALUE, Integer.MAX_VALUE))
                .todayBalance(ThreadLocalRandom.current().nextDouble(-500, 500))
                .moneySpendAll(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE))
                .moneyLeftAll(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE))
                .build();
    }

    public static Transaction generateTransaction() {
        return Transaction.builder()
                .date(createDate())
                .sum(ThreadLocalRandom.current().nextInt(10, 2000))
                .currency("RUR")
                .description("5ка")
                .sectionID(ThreadLocalRandom.current().nextInt(0, 10))
                .build();
    }

    public static String UUID() {
        return UUID.randomUUID().toString().toUpperCase().replace("-", "");
    }

    public static String generateDatePlus(TemporalUnit unit, int count) {
        LocalDate now = LocalDate.now().plus(count, unit);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return now.format(formatter);
    }

    public static String createDate() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return now.format(formatter);
    }
}
