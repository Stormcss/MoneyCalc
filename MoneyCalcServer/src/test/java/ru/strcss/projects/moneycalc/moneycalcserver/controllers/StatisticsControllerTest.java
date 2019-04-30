package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.FinanceSummaryCalculationContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.FinanceSummaryFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.FinanceSummarySearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;
import ru.strcss.projects.moneycalc.moneycalcserver.BaseTestContextConfiguration;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.MetricsService;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.HttpExceptionHandler;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.SummaryStatisticsHandler;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.RegistryMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.SettingsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.SpendingSectionsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.TransactionsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.model.dto.SpendingSectionFilter;
import ru.strcss.projects.moneycalc.moneycalcserver.services.SettingsServiceImpl;
import ru.strcss.projects.moneycalc.moneycalcserver.services.SpendingSectionServiceImpl;
import ru.strcss.projects.moneycalc.moneycalcserver.services.TransactionsServiceImpl;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SettingsService;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SpendingSectionService;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.TransactionsService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.AssertJUnit.assertEquals;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateFinanceSummaryBySectionList;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSpendingSectionsSearchRs;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransactionsSearchRs;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.serializeToJson;

@WebMvcTest(controllers = StatisticsController.class)
@ContextConfiguration(classes = {StatisticsController.class, StatisticsControllerTest.Config.class})
@Import(BaseTestContextConfiguration.class)
public class StatisticsControllerTest extends AbstractControllerTest {

    private final int TRANSACTIONS_COUNT = 50;
    private final int SECTIONS_COUNT = Arrays.asList(1, 2, 3, 4, 5).size();
    private final LocalDate settingsDateFrom = LocalDate.now();
    private final LocalDate settingsDateTo = LocalDate.now().plus(2, ChronoUnit.DAYS);
    private final LocalDate customDateFrom = LocalDate.now().minus(1, ChronoUnit.MONTHS);
    private final LocalDate customDateTo = LocalDate.now().minus(1, ChronoUnit.DAYS);

    private SpendingSectionsSearchRs sectionsSearchRs = generateSpendingSectionsSearchRs(SECTIONS_COUNT, false, false, false);

    @MockBean
    @Autowired
    private TransactionsMapper transactionsMapper;

    @MockBean
    @Autowired
    private SpendingSectionsMapper sectionsMapper;

    @MockBean
    @Autowired
    private SettingsMapper settingsMapper;

    @MockBean
    @Autowired
    private RegistryMapper registryMapper;

    @MockBean
    @Autowired
    private SummaryStatisticsHandler statisticsHandler;

    @BeforeMethod
    public void prepareStatisticsSuccessfulScenario() {
        resetMocks();

        when(settingsMapper.getSettings(anyString()))
                .thenReturn(new Settings(settingsDateFrom, settingsDateTo));
        when(sectionsMapper.getSpendingSections(anyString(), any(SpendingSectionFilter.class)))
                .thenReturn(sectionsSearchRs);

        doAnswer(invocation -> {
            List<Integer> sections = ((TransactionsSearchFilter) invocation.getArgument(1)).getRequiredSections();
            return generateTransactionsSearchRs(TRANSACTIONS_COUNT, sections, false);
        }).when(transactionsMapper).getTransactions(anyString(), any(TransactionsSearchFilter.class), eq(false));

        doAnswer(invocation -> {
            int size = ((FinanceSummaryCalculationContainer) invocation.getArgument(0)).getSections().size();
            return new FinanceSummarySearchRs(generateFinanceSummaryBySectionList(size));
        }).when(statisticsHandler).calculateSummaryStatisticsBySection(any(FinanceSummaryCalculationContainer.class));
    }

    @Test
    void shouldGetStatsWithNoFilter() throws Exception {
        mockMvc.perform(get("/api/stats/summaryBySection")
                .with(user(USER_LOGIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()", is(SECTIONS_COUNT)))
                .andExpect(jsonPath("$.items[0].*", hasSize(6)));

        //asserting that correct transactions are requested
        ArgumentCaptor<TransactionsSearchFilter> transactionSearchArgument = ArgumentCaptor.forClass(TransactionsSearchFilter.class);
        verify(transactionsMapper).getTransactions(anyString(), transactionSearchArgument.capture(), eq(false));
        assertEquals(transactionSearchArgument.getValue().getDateFrom(), settingsDateFrom);
        assertEquals(transactionSearchArgument.getValue().getDateTo(), settingsDateTo);
        assertEquals(transactionSearchArgument.getValue().getRequiredSections().size(), SECTIONS_COUNT);

        //asserting that correct data for statistics processing is passed
        ArgumentCaptor<FinanceSummaryCalculationContainer> statsArgument = ArgumentCaptor.forClass(FinanceSummaryCalculationContainer.class);
        verify(statisticsHandler).calculateSummaryStatisticsBySection(statsArgument.capture());
        assertEquals(statsArgument.getValue().getSections().size(), SECTIONS_COUNT);
        assertEquals(statsArgument.getValue().getRangeFrom(), settingsDateFrom);
        assertEquals(statsArgument.getValue().getRangeTo(), settingsDateTo);
    }

    @Test
    void shouldGetStatsWithFilter() throws Exception {
        List<Integer> sectionIds = Arrays.asList(1, 2);

        FinanceSummaryFilter financeSummaryFilter = new FinanceSummaryFilter();
        financeSummaryFilter.setRangeFrom(customDateFrom);
        financeSummaryFilter.setRangeTo(customDateTo);
        financeSummaryFilter.setSectionIds(sectionIds);

        mockMvc.perform(post("/api/stats/summaryBySection/getFiltered")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(financeSummaryFilter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()", is(2)))
                .andExpect(jsonPath("$.items[0].*", hasSize(6)));

        //asserting that correct transactions are requested
        ArgumentCaptor<TransactionsSearchFilter> transactionSearchArgument = ArgumentCaptor.forClass(TransactionsSearchFilter.class);
        verify(transactionsMapper).getTransactions(anyString(), transactionSearchArgument.capture(), eq(false));
        assertEquals(transactionSearchArgument.getValue().getDateFrom(), customDateFrom);
        assertEquals(transactionSearchArgument.getValue().getDateTo(), customDateTo);
        assertEquals(transactionSearchArgument.getValue().getRequiredSections().size(), sectionIds.size());

        //asserting that correct data for statistics processing is passed
        ArgumentCaptor<FinanceSummaryCalculationContainer> statsArgument = ArgumentCaptor.forClass(FinanceSummaryCalculationContainer.class);
        verify(statisticsHandler).calculateSummaryStatisticsBySection(statsArgument.capture());
        assertEquals(statsArgument.getValue().getSections().size(), sectionIds.size());
        assertEquals(statsArgument.getValue().getRangeFrom(), customDateFrom);
        assertEquals(statsArgument.getValue().getRangeTo(), customDateTo);
    }

    private void resetMocks() {
        reset(transactionsMapper);
        reset(statisticsHandler);
        reset(sectionsMapper);
        reset(settingsMapper);
    }

    @TestConfiguration
    static class Config {
        @Bean
        TransactionsService transactionsService(TransactionsMapper transactionsMapper, MetricsService metricsService) {
            return new TransactionsServiceImpl(transactionsMapper, metricsService);
        }

        @Bean
        SpendingSectionService sectionService(SpendingSectionsMapper sectionsMapper, RegistryMapper registryMapper,
                                              MetricsService metricsService) {
            return new SpendingSectionServiceImpl(sectionsMapper, registryMapper, metricsService);
        }

        @Bean
        SettingsService personService(SettingsMapper settingsMapper, MetricsService metricsService) {
            return new SettingsServiceImpl(settingsMapper, metricsService);
        }

        @Bean
        HttpExceptionHandler httpExceptionHandler() {
            return new HttpExceptionHandler();
        }
    }
}
