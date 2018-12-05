package ru.strcss.projects.moneycalc.testutils;

import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.entities.Access;
import ru.strcss.projects.moneycalc.entities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.entities.Identifications;
import ru.strcss.projects.moneycalc.entities.Settings;
import ru.strcss.projects.moneycalc.entities.SpendingSection;
import ru.strcss.projects.moneycalc.entities.Transaction;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        return new Access(login, "qwerty", login + "@mail.ru");
    }

    public static Credentials generateCredentials(String login) {
        int randomInt = ThreadLocalRandom.current().nextInt(names.length);
        return Credentials.builder()
                .access(generateAccess(login))
                .identifications(new Identifications((long) randomInt + 1, names[randomInt]))
                .build();
    }

    public static Settings generateSettings() {
        return new Settings(currentDate(), generateDatePlus(ChronoUnit.MONTHS, 1));
    }

    public static Identifications generateIdentifications() {
        return new Identifications(null, UUID());
    }

    public static Transaction generateTransaction() {
        return generateTransaction(null, null, null, null, null, null);
    }

    public static Transaction generateTransaction(String title, String desc) {
        return generateTransaction(null, null, null, null, title, desc);
    }

    public static Transaction generateTransaction(LocalDate date) {
        return generateTransaction(date, null, null, null, null, null);
    }

    public static Transaction generateTransaction(Integer sectionId) {
        return generateTransaction(null, sectionId, null, null, null, null);
    }

    public static Transaction generateTransaction(Integer sectionId, Integer sum) {
        return generateTransaction(null, sectionId, sum, null, null, null);
    }

    public static Transaction generateTransaction(LocalDate date, Integer sectionId, Integer sum, Long id,
                                                  String title, String desc) {
        return Transaction.builder()
                .id(id)
                .userId(1L)
                .date(date == null ? LocalDate.now() : date)
                .sum(sum == null ? ThreadLocalRandom.current().nextInt(10, 9000) : sum)
                .currency("RUR")
                .title(title == null ? "Магазин" : title)
                .description(desc == null ? "5ка" : desc)
                .sectionId(sectionId == null ? ThreadLocalRandom.current().nextInt(1, 2) : sectionId)
                .build();
    }

    /**
     * Generates {@link List} of {@link Transaction} objects with required parameters
     *
     * @param count      - required objects count
     * @param sectionIds - desired sectionIds for these Transactions
     */
    public static List<Transaction> generateTransactionList(int count, List<Integer> sectionIds) {
        return IntStream.range(0, count)
                .mapToObj(value -> generateTransaction(sectionIds.get(ThreadLocalRandom.current().nextInt(sectionIds.size()))))
                .collect(Collectors.toList());
    }

    public static String UUID() {
        return UUID.randomUUID().toString().toUpperCase().replace("-", "");
    }

    /**
     * Generates {@link List} of {@link SpendingSection} objects with required parameters
     *
     * @param count         - required objects count
     * @param isNonAdded    - last list item will have {@code isAdded} field as false
     * @param isRemoved     - last list item will have {@code isRemoved} field as true
     * @param isRemovedOnly - each object will have {@code isRemoved} field as true
     */
    public static List<SpendingSection> generateSpendingSectionList(int count, boolean isNonAdded,
                                                                    boolean isRemoved,
                                                                    boolean isRemovedOnly) {
        List<SpendingSection> spendingSections = new ArrayList<>(count);

        for (int id = 0; id < count; id++) {
            SpendingSection section = generateSpendingSection(null, id + 1, (long) id, null,
                    null, true, isRemovedOnly);
            spendingSections.add(section);
        }

        if (isNonAdded)
            spendingSections.get(spendingSections.size() - 1).setIsAdded(false);
        if (isRemoved)
            spendingSections.get(spendingSections.size() - 1).setIsRemoved(true);

        return spendingSections;
    }

    public static SpendingSection generateSpendingSection() {
        return generateSpendingSection(null, null, null, null, null, null, null);
    }

    public static SpendingSection generateSpendingSection(String name) {
        return generateSpendingSection(null, null, null, name, null, null, null);
    }

    public static SpendingSection generateSpendingSection(Long budget) {
        return generateSpendingSection(budget, null, null, null, null, null, null);
    }

    public static SpendingSection generateSpendingSection(Long budget, Integer innerId) {
        Long id = innerId + ThreadLocalRandom.current().nextLong(1000);
        return generateSpendingSection(budget, innerId, id, null, null, null, null);
    }

    public static SpendingSection generateSpendingSection(Long budget, Integer innerId, String name) {
        return generateSpendingSection(budget, innerId, null, name, null, null, null);
    }

    public static SpendingSection generateSpendingSection(Long budget, Integer innerId, Long id, String name,
                                                          Integer logoId, Boolean isAdded, Boolean isRemoved) {
        SpendingSection spendingSection = new SpendingSection();
        spendingSection.setId(id == null ? ThreadLocalRandom.current().nextLong(3000) : id);
        spendingSection.setUserId(1L);
        spendingSection.setSectionId(innerId);
        spendingSection.setLogoId(logoId == null ? ThreadLocalRandom.current().nextInt(3000) : logoId);
        spendingSection.setIsAdded(isAdded == null ? true : isAdded);
        spendingSection.setIsRemoved(isRemoved);
        spendingSection.setName(name == null ? "Магазин" + ThreadLocalRandom.current().nextLong(500_000) : name);
        spendingSection.setBudget(budget == null ? 1000 + ThreadLocalRandom.current().nextLong(9000) : budget);
        return spendingSection;
    }

    public static FinanceSummaryBySection generateFinanceSummaryBySection() {
        return FinanceSummaryBySection.builder()
                .moneyLeftAll(5000d)
                .moneySpendAll(1000d)
                .summaryBalance(3000d)
                .todayBalance(100d)
                .sectionId(1)
                .build();
    }

    public static FinanceSummaryCalculationContainer generateFinSummCalculContainer() {
        List<SpendingSection> sectionList = Arrays.asList(generateSpendingSection(100L, 0, "A"),
                generateSpendingSection(100L, 1, "B"), generateSpendingSection(100L, 2, "C"));
        List<Integer> sectionIds = Arrays.asList(0, 1, 2);
        return generateFinSummCalculContainer(sectionList, generateTransactionList(5, sectionIds));
    }

    public static FinanceSummaryCalculationContainer generateFinSummCalculContainer(int sections, int transactionsSum) {
        List<Integer> sectionIds = IntStream.range(0, sections).boxed().collect(Collectors.toList());

        List<SpendingSection> sectionList = sectionIds.stream()
                .map(id -> generateSpendingSection(ThreadLocalRandom.current().nextLong(100, 100000), id, "Name" + id))
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


