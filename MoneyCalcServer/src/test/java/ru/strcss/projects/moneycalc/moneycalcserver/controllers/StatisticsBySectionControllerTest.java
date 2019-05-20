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
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.ItemsContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.StatisticsFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.statistics.BaseStatistics;
import ru.strcss.projects.moneycalc.moneycalcserver.BaseTestContextConfiguration;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.MetricsService;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.HttpExceptionHandler;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.SummaryStatisticsHandler;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.RegistryMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.SettingsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.SpendingSectionsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.StatsBySectionMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.TransactionsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.model.dto.SpendingSectionFilter;
import ru.strcss.projects.moneycalc.moneycalcserver.services.SettingsServiceImpl;
import ru.strcss.projects.moneycalc.moneycalcserver.services.SpendingSectionServiceImpl;
import ru.strcss.projects.moneycalc.moneycalcserver.services.StatisticsBySectionService;
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
import static org.hamcrest.Matchers.not;
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
import static ru.strcss.projects.moneycalc.testutils.Generator.generateItemsContainer;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSpendingSectionsSearchRs;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSumBySectionList;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransactionsSearchRs;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.serializeToJson;

@WebMvcTest(controllers = StatisticsBySectionController.class)
@ContextConfiguration(classes = {StatisticsBySectionController.class, StatisticsBySectionControllerTest.Config.class})
@Import(BaseTestContextConfiguration.class)
public class StatisticsBySectionControllerTest extends AbstractControllerTest {

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
    private SummaryStatisticsHandler statisticsHandler;

    @MockBean
    @Autowired
    private StatsBySectionMapper statsBySectionMapper;

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
            return new ItemsContainer<>((long) size, new BaseStatistics(), generateFinanceSummaryBySectionList(size));
        }).when(statisticsHandler).calculateSummaryStatisticsBySection(any(FinanceSummaryCalculationContainer.class));

        when(statsBySectionMapper.getSum(anyString(), any(StatisticsFilter.class)))
                .thenReturn(generateItemsContainer(generateSumBySectionList(100)));
    }

    @Test
    void shouldGetSummaryBySection() throws Exception {
        mockMvc.perform(get("/api/stats/bySection/summary")
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
    void shouldGetSumBySectionWhenResultsAreFound() throws Exception {
        StatisticsFilter statisticsFilter = new StatisticsFilter(customDateFrom, customDateTo, Arrays.asList(1, 2));

        mockMvc.perform(post("/api/stats/bySection/sum")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(statisticsFilter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()", is(not(0))))
                .andExpect(jsonPath("$.items[0].*", hasSize(2)));
    }

    @Test
    void shouldGetSumBySectionWhenResultsAreNotFound() throws Exception {
        when(statsBySectionMapper.getSum(anyString(), any(StatisticsFilter.class)))
                .thenReturn(null);

        StatisticsFilter statisticsFilter = new StatisticsFilter(customDateFrom, customDateTo, Arrays.asList(1, 2));

        mockMvc.perform(post("/api/stats/bySection/sum")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(statisticsFilter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(0)))
                .andExpect(jsonPath("$.items.length()", is(0)));
    }

    private void resetMocks() {
        reset(transactionsMapper);
        reset(statisticsHandler);
        reset(sectionsMapper);
        reset(settingsMapper);
    }

    @TestConfiguration
    static class Config {

        @MockBean
        RegistryMapper registryMapper;

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
        SettingsService settingsService(SettingsMapper settingsMapper, MetricsService metricsService) {
            return new SettingsServiceImpl(settingsMapper, metricsService);
        }

        @Bean
        StatisticsBySectionService statisticsBySectionService(TransactionsService transactionsService,
                                                              SpendingSectionService spendingSectionService,
                                                              SettingsService settingsService,
                                                              SummaryStatisticsHandler statisticsHandler,
                                                              MetricsService metricsService,
                                                              StatsBySectionMapper statsBySectionMapper) {
            return new StatisticsBySectionService(transactionsService, spendingSectionService, settingsService,
                    statisticsHandler, metricsService, statsBySectionMapper);
        }

        @Bean
        HttpExceptionHandler httpExceptionHandler() {
            return new HttpExceptionHandler();
        }
    }
}
