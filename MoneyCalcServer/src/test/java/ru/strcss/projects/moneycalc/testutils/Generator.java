package ru.strcss.projects.moneycalc.testutils;

import ru.strcss.projects.moneycalc.moneycalcdto.dto.Credentials;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.ItemsContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsStats;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.BaseStatistics;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumByDate;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumByDateSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumBySection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SummaryBySection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.currentDate;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;

public class Generator {

    private static String[] names = {"Вася", "Петя", "Вова", "Дуся", "Дима", "Ваня", "Митя", "Шура", "Тоня", "Ася", "Зина",
            "Жора", "Коля", "Гриша", "Слава", "Пелагея", "Митрофана"};

    public static Credentials generateCredentials(Access access, Identifications identifications) {
        return new Credentials(access, identifications);
    }

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

    /**
     * Generates {@link TransactionsSearchRs} object with required parameters
     *
     * @param count      - required objects count
     * @param sectionIds - desired sectionIds for these Transactions
     */
    public static TransactionsSearchRs generateTransactionsSearchRs(int count, List<Integer> sectionIds, boolean isStatsRequired) {
        List<Transaction> transactions = generateTransactionList(count, sectionIds);
        TransactionsStats stats = new TransactionsStats(BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.TEN);
        return new TransactionsSearchRs(transactions.size(), isStatsRequired ? stats : null, transactions);
    }

    public static String UUID() {
        return UUID.randomUUID().toString().toUpperCase().replace("-", "");
    }

    /**
     * Generates {@link SpendingSectionsSearchRs} object with required parameters
     *
     * @param count         - required objects count
     * @param isNonAdded    - last list item will have {@code isAdded} field as false
     * @param isRemoved     - last list item will have {@code isRemoved} field as true
     * @param isRemovedOnly - each object will have {@code isRemoved} field as true
     */
    public static SpendingSectionsSearchRs generateSpendingSectionsSearchRs(int count, boolean isNonAdded,
                                                                            boolean isRemoved,
                                                                            boolean isRemovedOnly) {

        List<SpendingSection> spendingSections = IntStream.range(0, count)
                .mapToObj(id -> generateSpendingSection(null, id + 1, (long) id, null,
                        null, true, isRemovedOnly))
                .collect(Collectors.toList());

        if (isNonAdded)
            spendingSections.get(spendingSections.size() - 1).setIsAdded(false);
        if (isRemoved)
            spendingSections.get(spendingSections.size() - 1).setIsRemoved(true);

        return new SpendingSectionsSearchRs(spendingSections.size(), spendingSections);
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

    public static List<SummaryBySection> generateFinanceSummaryBySectionList(int count) {
        return IntStream.range(0, count)
                .mapToObj(Generator::generateFinanceSummaryBySection)
                .collect(Collectors.toList());
    }

    public static SummaryBySection generateFinanceSummaryBySection(int sectionId) {
        return SummaryBySection.builder()
                .moneyLeftAll(ThreadLocalRandom.current().nextDouble(0, 1000))
                .moneySpendAll(ThreadLocalRandom.current().nextDouble(0, 1000))
                .summaryBalance(ThreadLocalRandom.current().nextDouble(0, 1000))
                .todayBalance(ThreadLocalRandom.current().nextDouble(0, 1000))
                .sectionId(sectionId)
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

    public static <E> ItemsContainer<E> generateItemsContainer(List<E> list) {
        return new ItemsContainer<>((long) list.size(), new BaseStatistics(), list);
    }

    public static List<SumBySection> generateSumBySectionList(int count) {
        return generateCountedList(count, value -> new SumBySection("Name" + value, BigDecimal.valueOf(value)));
    }

    public static List<SumByDate> generateSumByDateList(int count) {
        return generateCountedList(count, value -> {
            LocalDate date = LocalDate.now().minus(value, ChronoUnit.DAYS);
            return new SumByDate(date, BigDecimal.valueOf(value));
        });
    }

    public static List<SumByDateSection> generateSumByDateSectionList(int count) {
        return generateCountedList(count, value -> {
            LocalDate date = LocalDate.now().minus(value, ChronoUnit.DAYS);
            return new SumByDateSection(date, "Name" + value, BigDecimal.valueOf(value));
        });
    }

    private static <E> List<E> generateCountedList(int count, IntFunction<? extends E> mapper) {
        return IntStream.range(0, count)
                .mapToObj(mapper)
                .collect(Collectors.toList());
    }
}


