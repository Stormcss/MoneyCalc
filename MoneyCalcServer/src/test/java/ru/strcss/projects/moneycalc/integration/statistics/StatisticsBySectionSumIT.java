package ru.strcss.projects.moneycalc.integration.statistics;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.integration.AbstractIT;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.ItemsContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.StatisticsFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.SumBySection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.savePersonGetToken;
import static ru.strcss.projects.moneycalc.integration.utils.IntegrationTestUtils.sendRequest;
import static ru.strcss.projects.moneycalc.integration.utils.StatisticsControllerTestUtils.addTransactions;
import static ru.strcss.projects.moneycalc.integration.utils.StatisticsControllerTestUtils.checkPersonsSections;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDateMinus;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.GenerationUtils.generateDatePlus;

public class StatisticsBySectionSumIT extends AbstractIT {

    private String token;
    private final int numOfSections = 2;

    @BeforeClass
    public void prepareTransactions() {
        token = savePersonGetToken(service);
        checkPersonsSections(numOfSections, 5000, service, token);
        addTransactions(service, token, 1, 5, 0, 200);
        addTransactions(service, token, 2, 6, 1, 300);
    }

    @Test
    public void shouldReturnSumWhenSectionsFilterIsEmpty() {
        LocalDate dateFrom = generateDateMinus(DAYS, 3);
        LocalDate dateTo = generateDatePlus(DAYS, 1);
        StatisticsFilter statisticsFilter = new StatisticsFilter(dateFrom, dateTo, Collections.emptyList());

        ItemsContainer<SumBySection> sumBySectionItems = sendRequest(service.getSumBySection(token, statisticsFilter)).body();

        verifyCount(sumBySectionItems, numOfSections);
        verifySumAndName(sumBySectionItems, 0, 2800, "Еда");
        verifySumAndName(sumBySectionItems, 1, 2700, "Свое");
        verifyOrder(true, sumBySectionItems.getItems());
    }

    @Test
    public void shouldReturnSumWhenSectionsFilterIsSet() {
        LocalDate dateFrom = generateDateMinus(DAYS, 3);
        LocalDate dateTo = generateDatePlus(DAYS, 1);
        StatisticsFilter statisticsFilter = new StatisticsFilter(dateFrom, dateTo, Collections.singletonList(2));

        ItemsContainer<SumBySection> sumBySectionItems = sendRequest(service.getSumBySection(token, statisticsFilter)).body();

        verifyCount(sumBySectionItems, 1);
        verifySumAndName(sumBySectionItems, 0, 2700, "Свое");
    }

    private void verifySumAndName(ItemsContainer<SumBySection> sumBySectionItems, int position, int sum, String name) {
        assertEquals(sumBySectionItems.getItems().get(position).getSum(), BigDecimal.valueOf(sum), "Sum is incorrect!");
        assertEquals(sumBySectionItems.getItems().get(position).getName(), name, "Name is incorrect!");
    }

    private <E> void verifyCount(ItemsContainer<E> itemsContainer, int expectedCount) {
        assertEquals(itemsContainer.getItems().size(), expectedCount, "Incorrect count of items!");
        assertEquals((long) itemsContainer.getCount(), expectedCount, "Incorrect count!");
    }

    private void verifyOrder(boolean isDesc, List<SumBySection> items) {
        Comparator<SumBySection> descComparator = (o1, o2) -> o2.getSum().subtract(o1.getSum()).intValue();
        Comparator<SumBySection> ascComparator = (o1, o2) -> o1.getSum().subtract(o2.getSum()).intValue();
        assertEquals(items, items.stream().sorted(isDesc ? descComparator : ascComparator).collect(Collectors.toList()));
    }
}
