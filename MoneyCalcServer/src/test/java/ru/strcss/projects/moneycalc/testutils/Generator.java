package ru.strcss.projects.moneycalc.testutils;

import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.enitities.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.formatDateToString;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.currentDate;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;

public class Generator {

    private static String[] names = {"Вася", "Петя", "Вова", "Дуся", "Дима", "Ваня", "Митя", "Шура", "Тоня", "Ася", "Зина"};

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
                        .name(names[ThreadLocalRandom.current().nextInt(names.length)])
                        .build())
                .build();
    }

    public static Settings generateSettings(String login, boolean withSections) {
        return Settings.builder()
                .login(login)
                .periodFrom(formatDateToString(currentDate()))
                .periodTo(formatDateToString(generateDatePlus(ChronoUnit.MONTHS, 1)))
                .sections(withSections ? Stream.generate(Generator::generateSpendingSection).limit(2).collect(Collectors.toList()) : null)
                .build();
    }

    public static Identifications generateIdentifications() {
        return Identifications.builder()
                .name(UUID())
                .build();
    }

    private static <T, R> Supplier<R> bind(Function<T, R> fn, T val) {
        return () -> fn.apply(val);
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
        return generateTransaction(currentDate(), ThreadLocalRandom.current().nextInt(0, 1), ThreadLocalRandom.current().nextInt(10, 2000));
    }

    public static Transaction generateTransaction(LocalDate date) {
        return generateTransaction(date, ThreadLocalRandom.current().nextInt(0, 1), ThreadLocalRandom.current().nextInt(10, 2000));
    }

    public static Transaction generateTransaction(Integer sectionID) {
        return generateTransaction(currentDate(), sectionID, ThreadLocalRandom.current().nextInt(10, 2000));
    }

    public static Transaction generateTransaction(Integer sectionID, Integer sum) {
        return generateTransaction(currentDate(), sectionID, sum);
    }

    public static Transaction generateTransaction(LocalDate date, Integer sectionID, Integer sum) {
        return Transaction.builder()
                .date(formatDateToString(date))
                .sum(sum)
                .currency("RUR")
                .description("5ка")
                .sectionID(sectionID)
                .build();
    }

    public static String UUID() {
        return UUID.randomUUID().toString().toUpperCase().replace("-", "");
    }

    public static SpendingSection generateSpendingSection() {
        return SpendingSection.builder()
                .budget(ThreadLocalRandom.current().nextInt(5000, Integer.MAX_VALUE))
                .isAdded(true)
                .name("Магазин" + ThreadLocalRandom.current().nextInt(2000))
                .build();
    }

    public static SpendingSection generateSpendingSection(Integer budget) {
        return SpendingSection.builder()
                .budget(budget)
                .isAdded(true)
                .name("Магазин" + ThreadLocalRandom.current().nextInt(1000))
                .build();
    }

    public static SpendingSection generateSpendingSection(Integer budget, Integer id) {
        return SpendingSection.builder()
                .budget(budget)
                .isAdded(true)
                .name("Магазин" + ThreadLocalRandom.current().nextInt(1000))
                .id(id)
                .build();
    }

}
