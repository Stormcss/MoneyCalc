package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.moneycalcserver.BaseTestContextConfiguration;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.HttpExceptionHandler;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.AccessMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.NO_PERSON_EXIST;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateAccess;

@WebMvcTest(controllers = AccessController.class)
@ContextConfiguration(classes = {AccessController.class, AccessControllerTest.Config.class})
@Import(BaseTestContextConfiguration.class)
public class AccessControllerTest extends AbstractControllerTest {

    @MockBean
    @Autowired
    private AccessMapper accessMapper;

    @Test
    void shouldGetAccess() throws Exception {
        when(accessMapper.getAccess(anyString()))
                .thenReturn(generateAccess());

        mockMvc.perform(get("/api/access")
                .with(user(USER_LOGIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", hasSize(3)));
    }

    @Test
    void shouldNotGetAccessGettingFailed() throws Exception {
        when(accessMapper.getAccess(anyString()))
                .thenReturn(null);

        mockMvc.perform(get("/api/access")
                .with(user(USER_LOGIN)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.userMessage", is(NO_PERSON_EXIST)));
    }

    @Test
    void shouldNotUpdateAccess() throws Exception {
        mockMvc.perform(put("/api/access")
                .with(user(USER_LOGIN)))
                .andExpect(status().isBadRequest());
    }

    @TestConfiguration
    static class Config {
        @Bean
        HttpExceptionHandler httpExceptionHandler() {
            return new HttpExceptionHandler();
        }
    }
}