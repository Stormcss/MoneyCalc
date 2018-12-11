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
import ru.strcss.projects.moneycalc.entities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcserver.BaseTestContextConfiguration;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.IdentificationsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.services.IdentificationsServiceImpl;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.IdentificationsService;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.strcss.projects.moneycalc.dto.Status.ERROR;
import static ru.strcss.projects.moneycalc.dto.Status.SUCCESS;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.IDENTIFICATIONS_SAVING_ERROR;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateIdentifications;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.serializeToJson;

@WebMvcTest(controllers = IdentificationsController.class)
@ContextConfiguration(classes = {IdentificationsController.class, IdentificationsControllerTest.Config.class})
@Import(BaseTestContextConfiguration.class)
public class IdentificationsControllerTest extends AbstractControllerTest {

    @MockBean
    @Autowired
    private IdentificationsMapper identificationsMapper;

    @BeforeMethod
    public void prepareIdentificationsSuccessfulScenario() {
        when(identificationsMapper.getIdentifications(anyString()))
                .thenReturn(new Identifications(1L, "Vasya"));
        when(identificationsMapper.updateIdentifications(anyString(), any(Identifications.class)))
                .thenReturn(1);
    }

    @Test
    void shouldGetIdentifications() throws Exception {
        mockMvc.perform(get("/api/identifications")
                .with(user(USER_LOGIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serverStatus", is(SUCCESS.name())))
                .andExpect(jsonPath("$.payload[*]", hasSize(2)));
    }

    @Test
    void shouldUpdateIdentifications() throws Exception {
        mockMvc.perform(put("/api/identifications")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(generateIdentifications())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serverStatus", is(SUCCESS.name())))
                .andExpect(jsonPath("$.payload[*]", hasSize(2)));
    }

    @Test
    void shouldNotUpdateIdentifications_incorrectData() throws Exception {
        mockMvc.perform(put("/api/identifications")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new Identifications())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serverStatus", is(ERROR.name())))
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        Arrays.asList("Required fields are incorrect", "name is empty")
                )));
    }

    @Test
    void shouldNotUpdateIdentifications_updatingFaied() throws Exception {
        when(identificationsMapper.updateIdentifications(anyString(), any(Identifications.class)))
                .thenReturn(0);

        mockMvc.perform(put("/api/identifications")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(generateIdentifications())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serverStatus", is(ERROR.name())))
                .andExpect(jsonPath("$.message", is(IDENTIFICATIONS_SAVING_ERROR)));
    }

    @TestConfiguration
    static class Config {
        @Bean
        IdentificationsService identificationsService(IdentificationsMapper identificationsMapper) {
            return new IdentificationsServiceImpl(identificationsMapper);
        }
    }
}