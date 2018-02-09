package ru.strcss.projects.moneycalcserver.controllers.utils;

import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.enitities.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.strcss.projects.moneycalcserver.controllers.utils.GenerationUtils.*;

public class Generator {

    private static String[] names = {"Вася", "Петя", "Вова", "Дуся", "Дима", "Ваня", "Митя", "Шура"};

    // FIXME: 09.02.2018 get rid of AI
    private static AtomicInteger SpendingSectionID = new AtomicInteger();

    public static Credentials generateCredentials() {
        return generateCredentials(UUID());
    }

    public static Credentials generateCredentials(String login) {
        return Credentials.builder()
                .access(Access.builder()
                        .login(login)
                        .password("qwerty")
                        .email(login + "@mail.ru")
                        .build())
                .identifications(Identifications.builder()
                        ._id(login)
                        .name(names[ThreadLocalRandom.current().nextInt(names.length)])
                        .build())
                .build();
    }

    public static Settings generateSettings(String login, int numOfSections) {
        return Settings.builder()
                ._id(login)
                .periodFrom(formatDateToString(currentDate()))
                .periodTo(formatDateToString(generateDatePlus(ChronoUnit.MONTHS, 1)))
                .sections(Stream.generate(Generator::generateSpendingSection).limit(numOfSections).collect(Collectors.toList()))
                .build();
    }

    public static Identifications generateIdentifications(String login) {
        return Identifications.builder()
                ._id(login)
                .name(UUID())
                .build();
    }

    //    private static FinanceStatistics generateFinanceStatistics(String login, int annualTransactionsCount, int transactionsCount) {
    private static FinanceStatistics generateFinanceStatistics(String login) {
        return FinanceStatistics.builder()
                ._id(login)
                .build();
    }

    private static FinanceSummary generateFinanceSummary(String login) {
        return FinanceSummary.builder()
                ._id(login)
//                .daysInMonth(ThreadLocalRandom.current().nextInt(29, 31))
//                .daysSpend(ThreadLocalRandom.current().nextInt(0, 29))
                .financeSections(generateSettingsSectionsList(3))
                .build();
    }

//    private static List<PersonTransactions> generateAnnualTransactions(int annualTransactionsCount, int transactionsCount) {
//
//        // TODO: 31.01.2018 HOW THE HELL DOES IT WORK???
//        return Stream.generate(bind(Generator::generateAnnualTransactions, transactionsCount))
//                .limit(annualTransactionsCount)
//                .collect(Collectors.toList());
//    }

    private static <T, R> Supplier<R> bind(Function<T, R> fn, T val) {
        return () -> fn.apply(val);
    }

//    private static PersonTransactions generateAnnualTransactions(int transactionsCount){
//        return PersonTransactions.builder()
//                .transactions(generateTransactions(transactionsCount,))
//                .build();
//    }

    private static List<Transaction> generateTransactions(int count, String login) {
//        return Stream.generate(Generator::generateTransaction)
        return Stream.generate(Generator::generateTransaction)
                .limit(count)
                .collect(Collectors.toList());
    }

    private static List<FinanceSummaryBySection> generateSettingsSectionsList(int count) {
        return Stream.generate(Generator::generateSettingsSection)
                .limit(count)
                .collect(Collectors.toList());
    }

    private static FinanceSummaryBySection generateSettingsSection() {
        return FinanceSummaryBySection.builder()
                .summaryBalance(ThreadLocalRandom.current().nextDouble(Integer.MIN_VALUE, Integer.MAX_VALUE))
                .todayBalance(ThreadLocalRandom.current().nextDouble(-500, 500))
                .moneySpendAll(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE))
                .moneyLeftAll(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE))
                .build();
    }

    public static Transaction generateTransaction() {
        return generateTransaction(currentDate());
    }

    public static Transaction generateTransaction(LocalDate date) {
        return Transaction.builder()
                .date(formatDateToString(date))
                .sum(ThreadLocalRandom.current().nextInt(10, 2000))
                .currency("RUR")
                .description("5ка")
                .sectionID(ThreadLocalRandom.current().nextInt(0, 10))
                .build();
    }

    public static String UUID() {
        return UUID.randomUUID().toString().toUpperCase().replace("-", "");
    }

//    public static String currentDate() {
//        LocalDate now = LocalDate.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        return now.format(formatter);
//    }

    public static SpendingSection generateSpendingSection() {
        return SpendingSection.builder()
                .budget(Integer.MAX_VALUE)
                .isAdded(true)
                .name("Магазин" + ThreadLocalRandom.current().nextInt(1000))
                .ID(SpendingSectionID.getAndIncrement())
                .build();
    }

}
