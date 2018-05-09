package ru.strcss.projects.moneycalc.testutils;

import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.enitities.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.formatDateToString;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.currentDate;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;

public class Generator {

    private static String[] names = {"Вася", "Петя", "Вова", "Дуся", "Дима", "Ваня", "Митя", "Шура", "Тоня", "Ася", "Зина"};

    public static Credentials generateCredentials() {
        return generateCredentials(UUID());
    }

    public static Access generateAccess() {
        return generateAccess(UUID());
    }

    public static Access generateAccess(String login) {
        return Access.builder()
                .login(login)
                .password("qwerty")
                .email(login + "@mail.ru")
                .build();
    }
    public static Credentials generateCredentials(String login) {
        return Credentials.builder()
                .access(generateAccess(login))
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

    public static List<Transaction> generateTransactionList(int count, List<Integer> ids) {
        return IntStream.range(0, count)
                .mapToObj(value -> generateTransaction(ids.get(ThreadLocalRandom.current().nextInt(ids.size()))))
                .collect(Collectors.toList());
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

    public static SpendingSection generateSpendingSection(String name) {
        return SpendingSection.builder()
                .budget(ThreadLocalRandom.current().nextInt(5000, Integer.MAX_VALUE))
                .isAdded(true)
                .name(name)
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

    public static SpendingSection generateSpendingSection(Integer budget, Integer id, String name) {
        return SpendingSection.builder()
                .budget(budget)
                .isAdded(true)
                .name(name)
                .id(id)
                .build();
    }

    public static FinanceSummaryBySection generateFinanceSummaryBySection() {
        return FinanceSummaryBySection.builder()
                .moneyLeftAll(5000)
                .moneySpendAll(1000)
                .summaryBalance(3000d)
                .todayBalance(100d)
                .sectionID(1)
                .build();
    }

    public static FinanceSummaryCalculationContainer generateFinSummCalculContainer() {
        List<SpendingSection> sectionList = Arrays.asList(generateSpendingSection(100, 0, "A"),
                generateSpendingSection(100, 1, "B"), generateSpendingSection(100, 2, "C"));
        List<Integer> sectionIds = Arrays.asList(0, 1, 2);
        return generateFinSummCalculContainer(sectionList, generateTransactionList(5, sectionIds));
    }

    public static FinanceSummaryCalculationContainer generateFinSummCalculContainer(int sections, int transactionsSum) {
        List<Integer> sectionIds = IntStream.range(0, sections).boxed().collect(Collectors.toList());

        List<SpendingSection> sectionList = sectionIds.stream()
                .map(id -> generateSpendingSection(ThreadLocalRandom.current().nextInt(100, 100000), id, "Name" + id))
                .collect(Collectors.toList());

        return generateFinSummCalculContainer(sectionList, generateTransactionList(transactionsSum, sectionIds));
    }

    public static FinanceSummaryCalculationContainer generateFinSummCalculContainer(List<SpendingSection> sectionList,
                                                                                    List<Transaction> transactionsList) {
        LocalDate rangeFrom = LocalDate.now().minus(2, ChronoUnit.DAYS);
        return FinanceSummaryCalculationContainer.builder()
                .rangeFrom(rangeFrom)
                .rangeTo(rangeFrom.plus(1, ChronoUnit.MONTHS))
                .sections(sectionList.stream().map(SpendingSection::getId).collect(Collectors.toList()))
                .spendingSections(sectionList)
                .today(LocalDate.now())
                .transactions(transactionsList)
                .build();
    }
}
