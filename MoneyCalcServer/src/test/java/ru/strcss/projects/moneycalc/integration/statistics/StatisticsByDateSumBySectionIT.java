package ru.strcss.projects.moneycalc.integration.statistics;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.integration.AbstractIT;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.ItemsContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.StatisticsFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumByDate;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumByDateSection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.savePersonGetToken;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.sendRequest;
import static ru.strcss.projects.moneycalc.integration.utils.StatisticsControllerTestUtils.addTransactions;
import static ru.strcss.projects.moneycalc.integration.utils.StatisticsControllerTestUtils.checkPersonsSections;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDateMinus;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;

public class StatisticsByDateSumBySectionIT extends AbstractIT {

    private String token;
    private final int numOfSections = 2;

    /**
     * Section 1           Section 2
     * Date     -   sum     Date     -   sum
     * today    -   400     today -1 -   600
     * today -1 -   600     today -2 -   900
     * today -2 -   800     today -3 -   1200
     * today -3 -   1000    today -4 -   1500
     * today -4 -   1200    today -5 -   1800
     */
    @BeforeClass
    public void prepareTransactions() {
        token = savePersonGetToken(service);
        checkPersonsSections(numOfSections, 5000, service, token);
        addTransactions(service, token, 1, 5, 0, 200);
        addTransactions(service, token, 2, 6, 1, 300);
    }

    @Test
    public void shouldReturnSumBySectionWhenSectionsFilterIsEmpty() {
        LocalDate rangeFrom = generateDateMinus(DAYS, 3);
        LocalDate rangeTo = generateDatePlus(DAYS, 1);
        StatisticsFilter statisticsFilter = new StatisticsFilter(rangeFrom, rangeTo, Collections.emptyList());

        ItemsContainer<SumByDateSection> sumByDateItems = sendRequest(service.getSumByDateSection(token, statisticsFilter)).body();

        verifyCount(sumByDateItems, 7);
        verifySumDateAndName(sumByDateItems, 0, 400, rangeTo.minus(1, DAYS), "Еда");
        verifySumDateAndName(sumByDateItems, 1, 600, rangeTo.minus(2, DAYS), "Еда");
        verifySumDateAndName(sumByDateItems, 2, 600, rangeTo.minus(2, DAYS), "Свое");
        verifySumDateAndName(sumByDateItems, 3, 800, rangeTo.minus(3, DAYS), "Еда");
        verifySumDateAndName(sumByDateItems, 4, 900, rangeTo.minus(3, DAYS), "Свое");
        verifyOrder(true, sumByDateItems.getItems());
    }

    @Test
    public void shouldReturnSumWhenSectionsFilterIsSet() {
        LocalDate rangeFrom = generateDateMinus(DAYS, 3);
        LocalDate rangeTo = generateDatePlus(DAYS, 0);
        StatisticsFilter statisticsFilter = new StatisticsFilter(rangeFrom, rangeTo, Collections.singletonList(2));

        ItemsContainer<SumByDateSection> sumByDateItems = sendRequest(service.getSumByDateSection(token, statisticsFilter)).body();

        verifyCount(sumByDateItems, 3);
        verifySumDateAndName(sumByDateItems, 0, 600, rangeTo.minus(1, DAYS), "Свое");
        verifySumDateAndName(sumByDateItems, 1, 900, rangeTo.minus(2, DAYS), "Свое");
        verifySumDateAndName(sumByDateItems, 2, 1200, rangeTo.minus(3, DAYS), "Свое");
    }

    private void verifySumDateAndName(ItemsContainer<SumByDateSection> sumByDateItems, int position, int sum, LocalDate date, String name) {
        assertEquals(sumByDateItems.getItems().get(position).getSum(), BigDecimal.valueOf(sum), "Sum is incorrect!");
        assertEquals(sumByDateItems.getItems().get(position).getName(), name, "Name is incorrect!");
        assertEquals(sumByDateItems.getItems().get(position).getDate(), date, "Date is incorrect!");
    }

    private <E> void verifyCount(ItemsContainer<E> itemsContainer, int expectedCount) {
        assertEquals(itemsContainer.getItems().size(), expectedCount, "Incorrect count of items!");
        assertEquals((long) itemsContainer.getCount(), expectedCount, "Incorrect count!");
    }

    private void verifyOrder(boolean isDesc, List<SumByDateSection> items) {
        Comparator<SumByDate> descComparator = (o1, o2) -> o2.getDate().isEqual(o1.getDate()) ? 0 :
                o2.getDate().isAfter(o1.getDate()) ? 1 : -1;
        Comparator<SumByDate> ascComparator = (o1, o2) -> o2.getDate().isEqual(o1.getDate()) ? 0 :
                o2.getDate().isBefore(o1.getDate()) ? 1 : -1;
//        assertEquals(items, items.stream().sorted(isDesc ? descComparator : ascComparator).collect(Collectors.toList()));
    }
}
