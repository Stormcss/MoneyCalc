package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.AjaxRs;
import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.statistics.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.dto.crudcontainers.transactions.TransactionsSearchContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.SettingsDBConnection;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.TransactionsDBConnection;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.SummaryStatisticsHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.testutils.Generator.*;

public class StatisticsControllerTest {

    private TransactionsDBConnection transactionsDBConnection = mock(TransactionsDBConnection.class);
    private SettingsDBConnection settingsDBConnection = mock(SettingsDBConnection.class);
    private SummaryStatisticsHandler statisticsHandler = mock(SummaryStatisticsHandler.class);
    private StatisticsController statisticsController;
    private List<Integer> sectionIDs = Arrays.asList(0, 1);

    @BeforeClass
    public void setUp() {
        User user = new User("login", "password", Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @BeforeGroups(groups = {"SuccessfulScenario", "incorrectContainers"})
    public void prepare_successfulScenario_incorrectContainers() {
        when(transactionsDBConnection.getTransactions(anyString(), any(TransactionsSearchContainer.class)))
                .thenReturn(generateTransactionList(50, Arrays.asList(0, 1)));
        when(settingsDBConnection.getSpendingSectionList(anyString()))
                .thenReturn(Arrays.asList(generateSpendingSection(5000, 0), generateSpendingSection(5000, 1)));
        when(statisticsHandler.calculateSummaryStatisticsBySections(any(FinanceSummaryCalculationContainer.class)))
                .thenReturn(Arrays.asList(generateFinanceSummaryBySection(), generateFinanceSummaryBySection()));

        statisticsController = new StatisticsController(transactionsDBConnection, settingsDBConnection, statisticsHandler);
    }

    @Test(groups = "SuccessfulScenario")
    public void testGetFinanceSummaryBySection() {
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer("2017-02-10", "2017-02-20", sectionIDs);
        AjaxRs<List<FinanceSummaryBySection>> financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);

        assertEquals(financeSummaryBySectionRs.getStatus(), Status.SUCCESS, financeSummaryBySectionRs.getMessage());
        assertEquals(financeSummaryBySectionRs.getPayload().size(), sectionIDs.size(), financeSummaryBySectionRs.getMessage());
    }

    @Test(groups = "incorrectContainer")
    public void testGetFinanceSummaryBySection_emptyContainer() {
        AjaxRs<List<FinanceSummaryBySection>> financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(null);
        assertEquals(financeSummaryBySectionRs.getStatus(), Status.ERROR, financeSummaryBySectionRs.getMessage());
    }

    @Test(groups = "incorrectContainer")
    public void testGetFinanceSummaryBySection_emptySectionIds() {
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer("2017-02-10", "2017-02-20", null);
        AjaxRs<List<FinanceSummaryBySection>> financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);

        assertEquals(financeSummaryBySectionRs.getStatus(), Status.ERROR, financeSummaryBySectionRs.getMessage());
    }

    @Test(groups = "incorrectContainer")
    public void testGetFinanceSummaryBySection_incorrect_RangeFrom() {
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer("2017.02.10", "2017-02-20", sectionIDs);
        AjaxRs<List<FinanceSummaryBySection>> financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);

        assertEquals(financeSummaryBySectionRs.getStatus(), Status.ERROR, financeSummaryBySectionRs.getMessage());

        getContainer = new FinanceSummaryGetContainer("10-02-2017", "2017-02-20", sectionIDs);
        financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);

        assertEquals(financeSummaryBySectionRs.getStatus(), Status.ERROR, financeSummaryBySectionRs.getMessage());
    }

    @Test(groups = "incorrectContainer")
    public void testGetFinanceSummaryBySection_incorrect_RangeTo() {
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer("2017-02-10", "2017.02.20", sectionIDs);
        AjaxRs<List<FinanceSummaryBySection>> financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);

        assertEquals(financeSummaryBySectionRs.getStatus(), Status.ERROR, financeSummaryBySectionRs.getMessage());

        getContainer = new FinanceSummaryGetContainer("2017-02-10", "20-02-2017", sectionIDs);
        financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);

        assertEquals(financeSummaryBySectionRs.getStatus(), Status.ERROR, financeSummaryBySectionRs.getMessage());
    }

    @Test(groups = "incorrectContainer")
    public void testGetFinanceSummaryBySection_RangeFrom_after_RangeTo() {
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer("2017-02-20", "2017-02-10", sectionIDs);
        AjaxRs<List<FinanceSummaryBySection>> financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);

        assertEquals(financeSummaryBySectionRs.getStatus(), Status.ERROR, financeSummaryBySectionRs.getMessage());
    }

    @Test(groups = "incorrectContainer")
    public void testGetFinanceSummaryBySection_empty_RangeFrom() {
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(null, "2017-02-20", sectionIDs);
        AjaxRs<List<FinanceSummaryBySection>> financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);

        assertEquals(financeSummaryBySectionRs.getStatus(), Status.ERROR, financeSummaryBySectionRs.getMessage());
    }

    @Test(groups = "incorrectContainer")
    public void testGetFinanceSummaryBySection_empty_RangeTo() {
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer("2017-02-10", null, sectionIDs);
        AjaxRs<List<FinanceSummaryBySection>> financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);

        assertEquals(financeSummaryBySectionRs.getStatus(), Status.ERROR, financeSummaryBySectionRs.getMessage());
    }

}