package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.spendingsections.SpendingSectionsSearchRs;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.BaseTestContextConfiguration;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.MetricsService;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.HttpExceptionHandler;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.RegistryMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.SpendingSectionsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.model.dto.SpendingSectionFilter;
import ru.strcss.projects.moneycalc.moneycalcserver.services.SpendingSectionServiceImpl;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SpendingSectionService;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.hamcrest.core.Every.everyItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_EMPTY;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_ID_NOT_EXISTS;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_NAME_EXISTS;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_NOT_DELETED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_NOT_FOUND;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_SAVING_ERROR;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.fillLog;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSpendingSection;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSpendingSectionsSearchRs;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.serializeToJson;

/**
 * Created by Stormcss
 * Date: 01.12.2018
 */
@WebMvcTest(controllers = SpendingSectionsController.class)
@ContextConfiguration(classes = {SpendingSectionsController.class, SpendingSectionsControllerTest.Config.class})
@Import(BaseTestContextConfiguration.class)
public class SpendingSectionsControllerTest extends AbstractControllerTest {

    private final int SECTIONS_COUNT = 5;
    private SpendingSectionsSearchRs sectionsRsDefault =
            generateSpendingSectionsSearchRs(SECTIONS_COUNT, false, false, false);
    private SpendingSectionsSearchRs sectionsRsWithNonAdded =
            generateSpendingSectionsSearchRs(SECTIONS_COUNT, true, false, false);
    private SpendingSectionsSearchRs sectionsRsWithRemoved =
            generateSpendingSectionsSearchRs(SECTIONS_COUNT, false, true, false);
    private SpendingSectionsSearchRs sectionsRsWithRemovedOnly =
            generateSpendingSectionsSearchRs(SECTIONS_COUNT, false, true, true);

    @MockBean
    @Autowired
    private SpendingSectionsMapper sectionsMapper;

    @MockBean
    @Autowired
    private RegistryMapper registryMapper;

    @BeforeMethod
    void prepareSpendingSectionsSuccessfulScenario() {
        doAnswer(invocation -> {
            SpendingSectionFilter sectionFilter = (SpendingSectionFilter) invocation.getArguments()[1];
            if (sectionFilter.isWithRemovedOnly())
                return sectionsRsWithRemovedOnly;
            if (sectionFilter.isWithRemoved())
                return sectionsRsWithRemoved;
            if (sectionFilter.isWithNonAdded())
                return sectionsRsWithNonAdded;
            return sectionsRsDefault;
        }).when(sectionsMapper).getSpendingSections(anyString(), any(SpendingSectionFilter.class));

        when(sectionsMapper.updateSpendingSection(anyString(), anyInt(), any(SpendingSection.class)))
                .thenReturn(1);
        when(sectionsMapper.addSpendingSection(anyLong(), any(SpendingSection.class)))
                .thenReturn(1);
        when(sectionsMapper.deleteSpendingSection(anyString(), anyInt()))
                .thenReturn(1);
        when(sectionsMapper.isSpendingSectionIdExists(anyString(), anyInt()))
                .thenReturn(true);
        when(sectionsMapper.isSpendingSectionNameNew(anyString(), anyString()))
                .thenReturn(true);
        when(registryMapper.getUserIdByLogin(anyString()))
                .thenReturn(1L);
    }

    @Test
    void shouldGetSpendingSectionsWithNoParams() throws Exception {
        mockMvc.perform(get("/api/spendingSections")
                .with(user(USER_LOGIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*]", hasSize(SECTIONS_COUNT)))
                .andExpect(jsonPath("$.items[*].isAdded", everyItem(is(true))))
                .andExpect(jsonPath("$.items[*].isRemoved", everyItem(is(false))));
    }

    @Test
    void shouldGetSpendingSectionsWithNonAdded() throws Exception {
        mockMvc.perform(get("/api/spendingSections?withNonAdded=true")
                .with(user(USER_LOGIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*]", hasSize(SECTIONS_COUNT)))
                .andExpect(jsonPath("$.items[:" + (SECTIONS_COUNT - 1) + "].isAdded", everyItem(is(true))))
                .andExpect(jsonPath("$.items[-1].isAdded", is(false)))
                .andExpect(jsonPath("$.items[*].isRemoved", everyItem(is(false))));
    }

    @Test
    void shouldGetSpendingSectionsWithRemoved() throws Exception {
        mockMvc.perform(get("/api/spendingSections?withRemoved=true")
                .with(user(USER_LOGIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*]", hasSize(SECTIONS_COUNT)))
                .andExpect(jsonPath("$.items[:" + (SECTIONS_COUNT - 1) + "].isRemoved", everyItem(is(false))))
                .andExpect(jsonPath("$.items[-1].isRemoved", is(true)))
                .andExpect(jsonPath("$.items[*].isAdded", everyItem(is(true))));
    }

    @Test
    void shouldGetSpendingSectionsWithRemovedOnly() throws Exception {
        mockMvc.perform(get("/api/spendingSections?withRemovedOnly=true")
                .with(user(USER_LOGIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*]", hasSize(SECTIONS_COUNT)))
                .andExpect(jsonPath("$.items[*].isRemoved", everyItem(is(true))))
                .andExpect(jsonPath("$.items[*].isAdded", everyItem(is(true))));
    }

    @Test
    void shouldAddSpendingSections() throws Exception {
        mockMvc.perform(post("/api/spendingSections")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(generateSpendingSection())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*]", hasSize(SECTIONS_COUNT)));
    }

    @Test
    void shouldUpdateSpendingSection() throws Exception {
        mockMvc.perform(put("/api/spendingSections")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new SpendingSectionUpdateContainer(1, generateSpendingSection()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*]", hasSize(SECTIONS_COUNT)));
    }

    /**
     * Testing case when spending section is updated but name remains old
     */
    @Test
    void shouldUpdateSpendingSectionWithOldName() throws Exception {
        String sectionName = "name";

        SpendingSectionsSearchRs spendingSectionsRs = generateSpendingSectionsSearchRs(5, true,
                false, false);
        spendingSectionsRs.getItems().get(spendingSectionsRs.getItems().size() - 1).setName(sectionName);
        Integer sectionId = spendingSectionsRs.getItems().get(spendingSectionsRs.getItems().size() - 1).getSectionId();

        when(sectionsMapper.getSpendingSections(anyString(), any(SpendingSectionFilter.class)))
                .thenReturn(spendingSectionsRs);

        mockMvc.perform(put("/api/spendingSections")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new SpendingSectionUpdateContainer(sectionId, generateSpendingSection(sectionName)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*]", hasSize(SECTIONS_COUNT)));
    }

    /**
     * Testing case when spending section is updated but name is not set at all
     */
    @Test
    void shouldUpdateSpendingSectionWhenNameNotSet() throws Exception {
        SpendingSection spendingSection = generateSpendingSection();
        spendingSection.setName(null);

        mockMvc.perform(put("/api/spendingSections")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new SpendingSectionUpdateContainer(1, spendingSection))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*]", hasSize(SECTIONS_COUNT)));
    }

    @Test
    void shouldDeleteSpendingSection() throws Exception {
        mockMvc.perform(delete("/api/spendingSections/{sectionId}", 1)
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[*]", hasSize(SECTIONS_COUNT)))
                .andExpect(jsonPath("$.items[*].isAdded", everyItem(is(true))))
                .andExpect(jsonPath("$.items[*].isRemoved", everyItem(is(false))));
    }

    @Test(dataProvider = "incorrectSectionAddDataProvider")
    void shouldNotAddSpendingSectionWhenIncorrectData(SpendingSection section, String expectedHint) throws Exception {
        String content = serializeToJson(section);

        mockMvc.perform(post("/api/spendingSections")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userMessage", stringContainsInOrder(
                        Arrays.asList("Required fields are incorrect:", expectedHint)
                )));
    }

    @Test
    void shouldNotAddSpendingSectionWhenExistingName() throws Exception {
        when(sectionsMapper.isSpendingSectionNameNew(anyString(), anyString()))
                .thenReturn(false);
        String sectionName = "name";

        mockMvc.perform(post("/api/spendingSections")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(generateSpendingSection(sectionName))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userMessage", is(fillLog(SPENDING_SECTION_NAME_EXISTS, sectionName))));
    }

    @Test
    void shouldNotAddSpendingSectionWhenAddingFailed() throws Exception {
        when(sectionsMapper.addSpendingSection(anyLong(), any(SpendingSection.class)))
                .thenReturn(0);

        mockMvc.perform(post("/api/spendingSections")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(generateSpendingSection())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userMessage", is(SPENDING_SECTION_SAVING_ERROR)));
    }

    @Test
    void shouldNotUpdateSpendingSectionWithNonexistentSectionId() throws Exception {
        when(sectionsMapper.isSpendingSectionIdExists(anyString(), anyInt()))
                .thenReturn(false);
        int sectionId = 1;

        mockMvc.perform(put("/api/spendingSections")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new SpendingSectionUpdateContainer(sectionId, generateSpendingSection()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userMessage", is(fillLog(SPENDING_SECTION_ID_NOT_EXISTS, String.valueOf(sectionId)))));
    }

    @Test
    void shouldNotUpdateSpendingSectionWithEmptySection() throws Exception {
        mockMvc.perform(put("/api/spendingSections")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new SpendingSectionUpdateContainer(1, new SpendingSection()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userMessage", is(SPENDING_SECTION_EMPTY)));
    }

    @Test
    void shouldNotUpdateSpendingSectionWithDisallowedName() throws Exception {
        String sectionName = "name";

        SpendingSectionsSearchRs sectionsSearchRs = generateSpendingSectionsSearchRs(5, true, false, false);
        sectionsSearchRs.getItems().get(sectionsSearchRs.getItems().size() - 1).setName(sectionName);

        when(sectionsMapper.getSpendingSections(anyString(), any(SpendingSectionFilter.class)))
                .thenReturn(sectionsSearchRs);

        mockMvc.perform(put("/api/spendingSections")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new SpendingSectionUpdateContainer(1, generateSpendingSection(sectionName)))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userMessage", is(fillLog(SPENDING_SECTION_NAME_EXISTS, sectionName))));
    }

    @Test
    void shouldNotUpdateSpendingSectionWhenUpdatingFailed() throws Exception {
        when(sectionsMapper.updateSpendingSection(anyString(), anyInt(), any(SpendingSection.class)))
                .thenReturn(0);

        mockMvc.perform(put("/api/spendingSections")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new SpendingSectionUpdateContainer(1, generateSpendingSection()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userMessage", is(SPENDING_SECTION_NOT_FOUND)));
    }

    @Test
    void shouldNotDeleteSpendingSectionWhenDeletingFailed() throws Exception {
        when(sectionsMapper.deleteSpendingSection(anyString(), anyInt()))
                .thenReturn(0);

        mockMvc.perform(delete("/api/spendingSections/{sectionId}", 1)
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new SpendingSectionUpdateContainer(1, generateSpendingSection()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userMessage", is(SPENDING_SECTION_NOT_DELETED)));
    }

    @DataProvider(name = "incorrectSectionAddDataProvider")
    public Object[][] dataProvider() {
        SpendingSection nullName = generateSpendingSection();
        nullName.setName(null);
        SpendingSection nullBudget = generateSpendingSection();
        nullBudget.setBudget(null);
        SpendingSection zeroBudget = generateSpendingSection(0L);
        SpendingSection isRemovedTrue = generateSpendingSection();
        isRemovedTrue.setIsRemoved(true);
        return new Object[][]{{nullBudget, "budget is empty"}, {nullName, "name is empty"},
                {zeroBudget, "budget must be >= 0"}, {isRemovedTrue, "isRemoved can not be set as income parameter"}};
    }

    @TestConfiguration
    static class Config {
        @Bean
        SpendingSectionService sectionService(SpendingSectionsMapper sectionsMapper, RegistryMapper registryMapper,
                                              MetricsService metricsService) {
            return new SpendingSectionServiceImpl(sectionsMapper, registryMapper, metricsService);
        }

        @Bean
        HttpExceptionHandler httpExceptionHandler() {
            return new HttpExceptionHandler();
        }
    }
}
