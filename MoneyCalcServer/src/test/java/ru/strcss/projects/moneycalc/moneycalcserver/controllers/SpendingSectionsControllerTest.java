package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MvcResult;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionUpdateContainer;
import ru.strcss.projects.moneycalc.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.BaseTestContextConfiguration;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SpendingSectionService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.strcss.projects.moneycalc.dto.Status.SUCCESS;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSpendingSection;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSpendingSectionList;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.serializeToJson;

/**
 * Created by Stormcss
 * Date: 01.12.2018
 */
@WebMvcTest(controllers = SpendingSectionsController.class)
//@ContextConfiguration(SpendingSectionsController.class)
@Import(BaseTestContextConfiguration.class)
public class SpendingSectionsControllerTest extends AbstractControllerTest {

    private List<SpendingSection> sectionList = generateSpendingSectionList(5, true);

    @MockBean
    @Autowired
    private SpendingSectionService spendingSectionService;

    @BeforeGroups(groups = "SpendingSectionsSuccessfulScenario")
    public void prepareSpendingSectionsSuccessfulScenario() {
        when(spendingSectionService.getSpendingSections(anyString(), anyBoolean(), anyBoolean(), anyBoolean()))
                .thenReturn(sectionList);
        when(spendingSectionService.updateSpendingSection(anyString(), any(SpendingSectionUpdateContainer.class)))
                .thenReturn(true);
        when(spendingSectionService.addSpendingSection(anyString(), any(SpendingSection.class)))
                .thenReturn(true);
//        when(spendingSectionService.getSectionIdByInnerId(anyInt(), anyInt()))
//                .thenReturn(1);
        when(spendingSectionService.isSpendingSectionIdExists(anyString(), anyInt()))
                .thenReturn(true);
        when(spendingSectionService.isSpendingSectionNameNew(anyString(), anyString()))
                .thenReturn(true);
        when(spendingSectionService.isNewNameAllowed(anyString(), any(SpendingSectionUpdateContainer.class)))
                .thenReturn(true);
    }

    @Test(groups = "SpendingSectionsSuccessfulScenario")
    public void shouldGetSpendingSections() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/spendingSections")
                .with(user("User")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serverStatus", is(SUCCESS.name())))
                .andExpect(jsonPath("$.payload[*]", hasSize(5)))
                .andReturn();

        System.out.println("mvcResult.getResponse().getContentAsString() = " + mvcResult.getResponse().getContentAsString());
    }

    @Test(groups = "SpendingSectionsSuccessfulScenario")
    public void shouldAddSpendingSections() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/spendingSections")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user("User"))
                .content(serializeToJson(generateSpendingSection())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serverStatus", is(SUCCESS.name())))
                .andExpect(jsonPath("$.payload[*]", hasSize(5)))
                .andReturn();

        System.out.println("mvcResult.getResponse().getContentAsString() = " + mvcResult.getResponse().getContentAsString());
    }

    @Test(groups = "SpendingSectionsSuccessfulScenario")
    public void shouldUpdateSpendingSection() throws Exception {

        String content = serializeToJson(new SpendingSectionUpdateContainer(1, generateSpendingSection()));

        MvcResult mvcResult = mockMvc.perform(put("/api/spendingSections")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user("User"))
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serverStatus", is(SUCCESS.name())))
                .andExpect(jsonPath("$.payload[*]", hasSize(5)))
                .andReturn();

        System.out.println("mvcResult.getResponse().getContentAsString() = " + mvcResult.getResponse().getContentAsString());
    }
}
