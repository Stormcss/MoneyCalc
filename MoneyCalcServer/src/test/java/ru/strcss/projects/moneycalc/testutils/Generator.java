package ru.strcss.projects.moneycalc.testutils;

import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.enitities.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.formatDateToString;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.currentDate;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;

public class Generator {

    private static String[] names = {"Вася", "Петя", "Вова", "Дуся", "Дима", "Ваня", "Митя", "Шура", "Тоня", "Ася", "Зина",
            "Жора", "Коля", "Гриша", "Слава", "Пелагея", "Митрофана"};

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

    public static Settings generateSettings() {
        return Settings.builder()
                .periodFrom(formatDateToString(currentDate()))
                .periodTo(formatDateToString(generateDatePlus(ChronoUnit.MONTHS, 1)))
                .build();
    }

    public static Identifications generateIdentifications() {
        return Identifications.builder()
                .name(UUID())
                .build();
    }

    public static Transaction generateTransaction() {
        return generateTransaction(null, null, null, null);
    }

    public static Transaction generateTransaction(LocalDate date) {
        return generateTransaction(date, null, null, null);
    }

    public static Transaction generateTransaction(Integer sectionID) {
        return generateTransaction(null, sectionID, null, null);
    }

    public static Transaction generateTransaction(Integer sectionID, Integer sum) {
        return generateTransaction(null, sectionID, sum, null);
    }

    public static Transaction generateTransaction(LocalDate date, Integer sectionId, Integer sum, Integer id) {
        return Transaction.builder()
                .id(id)
                .date(date == null ? LocalDate.now() : date)
                .sum(sum == null ? ThreadLocalRandom.current().nextInt(10, 9000) : sum)
                .currency("RUR")
                .title("Магазин")
                .description("5ка")
                .sectionId(sectionId == null ? ThreadLocalRandom.current().nextInt(0, 1) : sectionId)
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

    public static List<SpendingSection> generateSpendingSectionList(int count, boolean ordered) {
        List<SpendingSection> spendingSections = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            SpendingSection section = generateSpendingSection(null, i, i, null, true, false);
            spendingSections.add(section);
        }
        if (!ordered) {
            Collections.shuffle(spendingSections);
        }
        return spendingSections;
    }

    public static SpendingSection generateSpendingSection() {
        return generateSpendingSection(null, null, null, null, null, null);
    }

    public static SpendingSection generateSpendingSection(String name) {
        return generateSpendingSection(null, null, null, name, null, null);
    }

    public static SpendingSection generateSpendingSection(Integer budget) {
        return generateSpendingSection(budget, null, null, null, null, null);
    }

    public static SpendingSection generateSpendingSection(Integer budget, Integer innerId) {
        int id = innerId + ThreadLocalRandom.current().nextInt(1000);
        return generateSpendingSection(budget, innerId, id, null, null, null);
    }

    public static SpendingSection generateSpendingSection(Integer budget, Integer innerId, String name) {
        return generateSpendingSection(budget, innerId, null, name, null, null);
    }

    public static SpendingSection generateSpendingSection(Integer budget, Integer innerId, Integer id, String name,
                                                          Boolean isAdded, Boolean isRemoved) {
        return SpendingSection.builder()
                .id(id == null ? ThreadLocalRandom.current().nextInt(3000) : id)
                .sectionId(innerId)
                .isAdded(isAdded == null ? true : isAdded)
                .isRemoved(isRemoved == null ? false : isRemoved)
                .name(name == null ? "Магазин" + ThreadLocalRandom.current().nextInt(500_000) : name)
                .budget(budget == null ? 1000 + ThreadLocalRandom.current().nextInt(9000) : budget)
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
                .sections(sectionList.stream().map(SpendingSection::getSectionId).collect(Collectors.toList()))
                .spendingSections(sectionList)
                .today(LocalDate.now())
                .transactions(transactionsList)
                .build();
    }
}


