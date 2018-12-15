package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Settings;
import ru.strcss.projects.moneycalc.moneycalcserver.BaseTestContextConfiguration;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.MetricsService;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.SettingsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.services.SettingsServiceImpl;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SettingsService;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.strcss.projects.moneycalc.moneycalcdto.dto.Status.ERROR;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SETTINGS_NOT_FOUND;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SETTINGS_UPDATING_ERROR;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSettings;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.serializeToJson;

@WebMvcTest(controllers = SettingsController.class)
@ContextConfiguration(classes = {SettingsController.class, SettingsControllerTest.Config.class})
@Import(BaseTestContextConfiguration.class)
public class SettingsControllerTest extends AbstractControllerTest {

    @MockBean
    @Autowired
    private SettingsMapper settingsMapper;

    @Test(groups = "SettingsSuccessfulScenario")
    void shouldGetSettings() throws Exception {
        when(settingsMapper.getSettings(anyString()))
                .thenReturn(generateSettings());

        MvcResult mvcResult = mockMvc.perform(get("/api/settings")
                .with(user("User")))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("mvcResult.getResponse().getContentAsString() = " + mvcResult.getResponse().getContentAsString());
    }

    @Test
    void shouldUpdateSettings() throws Exception {
        when(settingsMapper.getSettings(anyString()))
                .thenReturn(generateSettings());

        MvcResult mvcResult = mockMvc.perform(put("/api/settings")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user("User"))
                .content(serializeToJson(generateSettings())))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("mvcResult.getResponse().getContentAsString() = " + mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturnEmptyFieldsError() throws Exception {
        String content = serializeToJson(new Settings(null, null));

        mockMvc.perform(put("/api/settings")
                .with(user("User"))
                .header("Content-Type", "application/json;charset=UTF-8")
                .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serverStatus", is(ERROR.name())))
                .andExpect(jsonPath("$.message",
                        stringContainsInOrder(
                                Arrays.asList("Required fields are incorrect", "periodFrom is empty", "periodTo is empty")
                        )));
    }

    @Test
    void shouldReturnGettingSettingsError() throws Exception {
        when(settingsMapper.getSettings(anyString()))
                .thenReturn(null);

        mockMvc.perform(get("/api/settings")
                .with(user(USER_LOGIN)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serverStatus", is(ERROR.name())))
                .andExpect(jsonPath("$.message", is(SETTINGS_NOT_FOUND)));
    }

    @Test
    void shouldReturnUpdatingSettingsError() throws Exception {
        when(settingsMapper.getSettings(anyString()))
                .thenReturn(null);

        mockMvc.perform(put("/api/settings")
                .with(user(USER_LOGIN))
                .header("Content-Type", "application/json;charset=UTF-8")
                .content(serializeToJson(generateSettings())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serverStatus", is(ERROR.name())))
                .andExpect(jsonPath("$.message", is(SETTINGS_UPDATING_ERROR)));
    }

    @Configuration
    static class Config {
        @Bean
        SettingsService settingsService(SettingsMapper settingsMapper, MetricsService metricsService) {
            return new SettingsServiceImpl(settingsMapper, metricsService);
        }
    }
}