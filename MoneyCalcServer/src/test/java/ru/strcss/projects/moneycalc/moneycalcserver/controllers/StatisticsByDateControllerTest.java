package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.statistics.StatisticsFilter;
import ru.strcss.projects.moneycalc.moneycalcserver.BaseTestContextConfiguration;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.StatsByDateMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.services.StatisticsByDateService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateItemsContainer;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSumByDateList;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.serializeToJson;

/**
 * Created by Stormcss
 * Date: 08.05.2019
 */
@WebMvcTest(controllers = StatisticsByDateController.class)
@ContextConfiguration(classes = {StatisticsByDateController.class, StatisticsByDateControllerTest.Config.class})
@Import(BaseTestContextConfiguration.class)
public class StatisticsByDateControllerTest extends AbstractControllerTest {

    @MockBean
    @Autowired
    StatsByDateMapper statsMapper;

    private final LocalDate customDateFrom = LocalDate.now().minus(1, ChronoUnit.MONTHS);
    private final LocalDate customDateTo = LocalDate.now().minus(1, ChronoUnit.DAYS);
    private final int SECTIONS_COUNT = 10;

    @BeforeMethod
    public void prepareStatisticsSuccessfulScenario() {
        when(statsMapper.getSum(anyString(), any(StatisticsFilter.class)))
                .thenReturn(generateItemsContainer(generateSumByDateList(SECTIONS_COUNT)));
    }

    @Test
    void shouldGetSumWhenResultsAreFound() throws Exception {
        StatisticsFilter statisticsFilter = new StatisticsFilter(customDateFrom, customDateTo, null);

        mockMvc.perform(post("/api/stats/byDate/sum")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(statisticsFilter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()", is(not(0))))
                .andExpect(jsonPath("$.items[0].*", hasSize(2)));
    }

    @Test
    void shouldGetSumWhenResultsAreNotFound() throws Exception {
        when(statsMapper.getSum(anyString(), any(StatisticsFilter.class)))
                .thenReturn(null);

        StatisticsFilter statisticsFilter = new StatisticsFilter(customDateFrom, customDateTo, null);

        mockMvc.perform(post("/api/stats/byDate/sum")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(statisticsFilter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(0)))
                .andExpect(jsonPath("$.items.length()", is(0)));
    }

    @TestConfiguration
    static class Config {
        @Bean
        StatisticsByDateService statisticsByDateService(StatsByDateMapper statsMapper) {
            return new StatisticsByDateService(statsMapper);
        }
    }
}