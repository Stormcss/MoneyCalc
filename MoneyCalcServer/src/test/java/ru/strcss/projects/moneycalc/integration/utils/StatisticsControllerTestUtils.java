package ru.strcss.projects.moneycalc.integration.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.strcss.projects.moneycalc.integration.testapi.MoneyCalcClient;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.ItemsContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SummaryBySection;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.sendRequest;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDateMinus;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSpendingSection;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransaction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatisticsControllerTestUtils {

    /**
     * Get SummaryBySection and return it
     */
    public static SummaryBySection getFinanceSummaryBySection(MoneyCalcClient service, String token, Integer sectionId) {
        ItemsContainer<SummaryBySection> responseGetStats = sendRequest(service.getSummaryBySection(token)).body();
        return responseGetStats.getItems().stream()
                .filter(summaryBySection -> summaryBySection.getSectionId().equals(sectionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    /**
     * Add Person's Sections if required (in case if Person by default has less Sections then it is required for test)
     *
     * @param numOfSections - required number of Person's sections
     */
    public static void checkPersonsSections(int numOfSections, long budget, MoneyCalcClient service, String token) {
        List<SpendingSection> spendingSections = sendRequest(service.getSpendingSections(token)).body().getItems();

        if (numOfSections > spendingSections.size()) {
            for (int i = 0; i < numOfSections - spendingSections.size(); i++) {
                sendRequest(service.addSpendingSection(token, generateSpendingSection(budget)));
            }
        }
    }

    /**
     * Add transactions for test suite
     * Both minusMax and minusMin will be used to calculate date of adding Transaction and sum for Transaction
     *
     * @param sectionId - sectionId of added Transaction
     * @param minusMax  - maximum sum for Transaction (will be multiplied by 100).
     * @param minusMin  - minimum sum for Transaction (will be multiplied by 100)
     * @return List of already added Transactions
     */
    public static List<Transaction> addTransactions(MoneyCalcClient service, String token, int sectionId, int minusMax, int minusMin) {
        return addTransactions(service, token, sectionId, minusMax, minusMin, 100);
    }

    public static List<Transaction> addTransactions(MoneyCalcClient service, String token, int sectionId, int minusMax,
                                                    int minusMin, int multiplier) {
        List<Integer> sums = IntStream.range(0, minusMax - minusMin)
                .map(num -> (num + 2) * multiplier)
                .boxed()
                .collect(Collectors.toList());

        List<Transaction> transactions = new ArrayList<>();

        for (Integer sum : sums) {
            transactions.add(generateTransaction(generateDateMinus(ChronoUnit.DAYS, minusMin), sectionId, sum,
                    null, null, null));
            minusMin++;
        }

        return transactions.stream()
                .map(transaction -> sendRequest(service.addTransaction(token, transaction)).body())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
