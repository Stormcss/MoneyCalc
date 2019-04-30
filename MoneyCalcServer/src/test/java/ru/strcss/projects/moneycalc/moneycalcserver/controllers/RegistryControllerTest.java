package ru.strcss.projects.moneycalc.moneycalcserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Credentials;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Person;
import ru.strcss.projects.moneycalc.moneycalcserver.BaseTestContextConfiguration;
import ru.strcss.projects.moneycalc.moneycalcserver.handlers.HttpExceptionHandler;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.RegistryMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.SpendingSectionsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.services.RegisterServiceImpl;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.RegisterService;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.PERSON_EMAIL_ALREADY_EXISTS;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.PERSON_EMAIL_INCORRECT;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.PERSON_LOGIN_ALREADY_EXISTS;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.fillLog;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateAccess;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateCredentials;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateIdentifications;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.serializeToJson;

@WebMvcTest(controllers = RegistryController.class)
@ContextConfiguration(classes = {RegistryController.class, RegistryControllerTest.Config.class})
@Import(BaseTestContextConfiguration.class)
public class RegistryControllerTest extends AbstractControllerTest {

    @MockBean
    @Autowired
    private RegistryMapper registryMapper;

    @MockBean
    @Autowired
    private SpendingSectionsMapper sectionsMapper;

    @BeforeMethod
    public void prepareRegistrySuccessfulScenario() {
        when(registryMapper.isUserExistsByLogin(anyString()))
                .thenReturn(false);
        when(registryMapper.isUserExistsByEmail(anyString()))
                .thenReturn(false);
        when(registryMapper.registerIds())
                .thenReturn(new Person(1L, 1L, 1L, 1L));
    }

    @Test(dataProvider = "correctEmailRegistrationDataProvider")
    void shouldPerformRegistration(String email) throws Exception {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setEmail(email);

        mockMvc.perform(post("/api/registration/register")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]", hasSize(4)));
    }

    @Test(dataProvider = "incorrectAccessRegistrationDataProvider")
    void shouldNotPerformRegistrationInvalidAccess(Access access, String expectedHint) throws Exception {
        final MvcResult mvcResult = mockMvc.perform(post("/api/registration/register")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(generateCredentials(access, generateIdentifications()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userMessage", stringContainsInOrder(
                        Arrays.asList("Can not perform registration:", expectedHint)
                )))
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void shouldNotPerformRegistrationInvalidIdentifications() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(post("/api/registration/register")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(generateCredentials(generateAccess(), new Identifications()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userMessage", stringContainsInOrder(
                        Arrays.asList("Can not perform registration:", "name is empty")
                )))
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void shouldNotPerformRegistrationLoginAlreadyExists() throws Exception {
        when(registryMapper.isUserExistsByLogin(anyString()))
                .thenReturn(true);
        Credentials credentials = generateCredentials();
        credentials.getAccess().setLogin(USER_LOGIN);

        final MvcResult mvcResult = mockMvc.perform(post("/api/registration/register")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(generateCredentials(USER_LOGIN))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userMessage", is(fillLog(PERSON_LOGIN_ALREADY_EXISTS, USER_LOGIN))))
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void shouldNotPerformRegistrationEmailAlreadyExists() throws Exception {
        when(registryMapper.isUserExistsByEmail(anyString()))
                .thenReturn(true);
        final String email = "mail@mail.ru";
        Credentials credentials = generateCredentials();
        credentials.getAccess().setEmail(email);

        final MvcResult mvcResult = mockMvc.perform(post("/api/registration/register")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(credentials)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userMessage", is(fillLog(PERSON_EMAIL_ALREADY_EXISTS, email))))
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test(dataProvider = "incorrectEmailRegistrationDataProvider")
    void shouldNotPerformRegistrationEmailInvalid(String email) throws Exception {
        Credentials credentials = generateCredentials();
        credentials.getAccess().setEmail(email);

        final MvcResult mvcResult = mockMvc.perform(post("/api/registration/register")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(credentials)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.userMessage", is(fillLog(PERSON_EMAIL_INCORRECT, email))))
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @DataProvider(name = "incorrectAccessRegistrationDataProvider")
    public Object[][] dataProviderAccess() {
        Access nullLogin = generateAccess(null);
        Access nullPassword = generateAccess();
        nullPassword.setPassword(null);
        Access nullEmail = generateAccess();
        nullEmail.setEmail(null);
        return new Object[][]{{nullLogin, "login is empty"}, {nullPassword, "password is empty"}, {nullEmail, "email is empty"}};
    }

    @DataProvider(name = "correctEmailRegistrationDataProvider")
    public Object[] dataProviderCorrectEmails() {
        return new Object[]{"qwergbb@gmail.com",
                "mailbox+tag@hostname.ru",
                "Allen@example.com",
                "f.i.r.s.t.l.a.s.t@gmail.com",
                "postmaster@xn--55gaaaaaa281gfaqg86dja792anqa.ws",
                "imyafamiliya334356@mail.ru",
                "qwergbb@gmail",
                "Miles.O'Brian@example.com",
                "allen@[127.0.0.1]",
                "Miles.O'Brian@example.com"};
    }

    @DataProvider(name = "incorrectEmailRegistrationDataProvider")
    public Object[] dataProviderIncorrectEmails() {
        return new Object[]{
                "@gmail.com.",
                "abc.com",
                "nothing",
                "mailbox tag@hostname.ru",
                "allen@[IPv6:0:0:1]"};
    }

    @TestConfiguration
    static class Config {
        @Bean
        RegisterService sectionService(RegistryMapper registryMapper, SpendingSectionsMapper sectionsMapper,
                                       BCryptPasswordEncoder bCryptPasswordEncoder) {
            return new RegisterServiceImpl(registryMapper, sectionsMapper, bCryptPasswordEncoder);
        }

        @Bean
        HttpExceptionHandler httpExceptionHandler() {
            return new HttpExceptionHandler();
        }
    }
}