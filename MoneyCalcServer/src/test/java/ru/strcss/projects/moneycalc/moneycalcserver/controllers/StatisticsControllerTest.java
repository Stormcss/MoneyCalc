package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.dto.Status;
import ru.strcss.projects.moneycalc.dto.crudcontainers.statistics.FinanceSummaryGetContainer;
import ru.strcss.projects.moneycalc.enitities.FinanceSummaryBySection;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.PersonDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.SettingsDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.SpendingSectionDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.TransactionsDao;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.SummaryStatisticsHandler;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.testutils.Generator.*;

public class StatisticsControllerTest {

    private TransactionsDao transactionsDao = mock(TransactionsDao.class);
    private SpendingSectionDao spendingSectionDao = mock(SpendingSectionDao.class);
    private PersonDao personDao = mock(PersonDao.class);
    private SettingsDao settingsDao = mock(SettingsDao.class);
    private SummaryStatisticsHandler statisticsHandler = mock(SummaryStatisticsHandler.class);
    private StatisticsController statisticsController;
    private List<Integer> sectionIDs = Arrays.asList(0, 1);

    @BeforeClass
    public void setUp() {
        User user = new User("login", "password", Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @BeforeGroups(groups = {"successfulScenario", "incorrectContainers"})
    public void prepare_successfulScenario_incorrectContainers() {
        when(transactionsDao.getTransactionsByPersonId(anyInt(), any(LocalDate.class), any(LocalDate.class), anyList()))
                .thenReturn(generateTransactionList(50, Arrays.asList(0, 1)));
        when(spendingSectionDao.getSpendingSectionsByPersonId(anyInt()))
                .thenReturn(Arrays.asList(generateSpendingSection(5000, 0), generateSpendingSection(5000, 1)));
        when(settingsDao.getSettingsById(anyInt()))
                .thenReturn(new Settings(1, LocalDate.now(), LocalDate.now().plus(1, ChronoUnit.MONTHS)));

        when(personDao.getPersonIdByLogin(anyString()))
                .thenReturn(1);

        when(statisticsHandler.calculateSummaryStatisticsBySections(any(FinanceSummaryCalculationContainer.class)))
                .thenReturn(Arrays.asList(generateFinanceSummaryBySection(), generateFinanceSummaryBySection()));

        statisticsController = new StatisticsController(transactionsDao, spendingSectionDao, personDao, settingsDao, statisticsHandler);
    }

    @Test(groups = "successfulScenario")
    public void testGetFinanceSummaryBySection() {
        LocalDate dateFrom = LocalDate.of(2017, 2, 10);
        LocalDate dateTo = LocalDate.of(2017, 2, 20);
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(dateFrom, dateTo, sectionIDs);

        ResponseEntity<MoneyCalcRs<List<FinanceSummaryBySection>>> financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);

        assertEquals(financeSummaryBySectionRs.getBody().getServerStatus(), Status.SUCCESS, financeSummaryBySectionRs.getBody().getMessage());
        assertEquals(financeSummaryBySectionRs.getBody().getPayload().size(), sectionIDs.size(), financeSummaryBySectionRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainer")
    public void testGetFinanceSummaryBySection_emptyContainer() {
        ResponseEntity<MoneyCalcRs<List<FinanceSummaryBySection>>> financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(null);
        assertEquals(financeSummaryBySectionRs.getBody().getServerStatus(), Status.ERROR, financeSummaryBySectionRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainer")
    public void testGetFinanceSummaryBySection_emptySectionIds() {
        LocalDate dateFrom = LocalDate.of(2017, 2, 10);
        LocalDate dateTo = LocalDate.of(2017, 2, 20);
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(dateFrom, dateTo, null);

        ResponseEntity<MoneyCalcRs<List<FinanceSummaryBySection>>> financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);

        assertEquals(financeSummaryBySectionRs.getBody().getServerStatus(), Status.ERROR, financeSummaryBySectionRs.getBody().getMessage());
    }

//    @Test(groups = "incorrectContainer")
//    public void testGetFinanceSummaryBySection_incorrect_RangeFrom() {
//        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer("2017.02.10", "2017-02-20", sectionIDs);
//        ResponseEntity<MoneyCalcRs<List<FinanceSummaryBySection>>> financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);
//
//        assertEquals(financeSummaryBySectionRs.getBody().getServerStatus(), Status.ERROR, financeSummaryBySectionRs.getBody().getMessage());
//
//        getContainer = new FinanceSummaryGetContainer("10-02-2017", "2017-02-20", sectionIDs);
//        financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);
//
//        assertEquals(financeSummaryBySectionRs.getBody().getServerStatus(), Status.ERROR, financeSummaryBySectionRs.getBody().getMessage());
//    }

//    @Test(groups = "incorrectContainer")
//    public void testGetFinanceSummaryBySection_incorrect_RangeTo() {
//        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer("2017-02-10", "2017.02.20", sectionIDs);
//        ResponseEntity<MoneyCalcRs<List<FinanceSummaryBySection>>> financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);
//
//        assertEquals(financeSummaryBySectionRs.getBody().getServerStatus(), Status.ERROR, financeSummaryBySectionRs.getBody().getMessage());
//
//        getContainer = new FinanceSummaryGetContainer("2017-02-10", "20-02-2017", sectionIDs);
//        financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);
//
//        assertEquals(financeSummaryBySectionRs.getBody().getServerStatus(), Status.ERROR, financeSummaryBySectionRs.getBody().getMessage());
//    }

    @Test(groups = "incorrectContainer")
    public void testGetFinanceSummaryBySection_RangeFrom_after_RangeTo() {
        LocalDate dateFrom = LocalDate.of(2017, 2, 20);
        LocalDate dateTo = LocalDate.of(2017, 2, 10);
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(dateFrom, dateTo, sectionIDs);

        ResponseEntity<MoneyCalcRs<List<FinanceSummaryBySection>>> financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);

        assertEquals(financeSummaryBySectionRs.getBody().getServerStatus(), Status.ERROR, financeSummaryBySectionRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainer")
    public void testGetFinanceSummaryBySection_empty_RangeFrom() {
        LocalDate dateTo = LocalDate.of(2017, 2, 10);
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(null, dateTo, sectionIDs);

        ResponseEntity<MoneyCalcRs<List<FinanceSummaryBySection>>> financeSummaryBySectionRs =
                statisticsController.getFinanceSummaryBySection(getContainer);

        assertEquals(financeSummaryBySectionRs.getBody().getServerStatus(), Status.ERROR, financeSummaryBySectionRs.getBody().getMessage());
    }

    @Test(groups = "incorrectContainer")
    public void testGetFinanceSummaryBySection_empty_RangeTo() {
        LocalDate dateFrom = LocalDate.of(2017, 2, 20);
        FinanceSummaryGetContainer getContainer = new FinanceSummaryGetContainer(dateFrom, null, sectionIDs);

        ResponseEntity<MoneyCalcRs<List<FinanceSummaryBySection>>> financeSummaryBySectionRs = statisticsController.getFinanceSummaryBySection(getContainer);

        assertEquals(financeSummaryBySectionRs.getBody().getServerStatus(), Status.ERROR, financeSummaryBySectionRs.getBody().getMessage());
    }

}