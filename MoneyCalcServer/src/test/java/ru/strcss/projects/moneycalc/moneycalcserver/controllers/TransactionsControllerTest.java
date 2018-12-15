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
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionUpdateContainer;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.crudcontainers.transactions.TransactionsSearchFilter;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.BaseTestContextConfiguration;
import ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics.MetricsService;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.RegistryMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.SpendingSectionsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.TransactionsMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.mapper.UserMapper;
import ru.strcss.projects.moneycalc.moneycalcserver.services.PersonServiceImpl;
import ru.strcss.projects.moneycalc.moneycalcserver.services.SpendingSectionServiceImpl;
import ru.strcss.projects.moneycalc.moneycalcserver.services.TransactionsServiceImpl;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.PersonService;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.SpendingSectionService;
import ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces.TransactionsService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.strcss.projects.moneycalc.moneycalcdto.dto.Status.ERROR;
import static ru.strcss.projects.moneycalc.moneycalcdto.dto.Status.SUCCESS;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.DATE_SEQUENCE_INCORRECT;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_ID_NOT_EXISTS;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.TRANSACTION_DELETED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.TRANSACTION_NOT_DELETED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.TRANSACTION_NOT_FOUND;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.TRANSACTION_NOT_UPDATED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.TRANSACTION_SAVED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.TRANSACTION_SAVING_ERROR;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.TRANSACTION_UPDATED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.fillLog;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransaction;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateTransactionList;
import static ru.strcss.projects.moneycalc.testutils.TestUtils.serializeToJson;

@WebMvcTest(controllers = TransactionsController.class)
@ContextConfiguration(classes = {TransactionsController.class, TransactionsControllerTest.Config.class})
@Import(BaseTestContextConfiguration.class)
public class TransactionsControllerTest extends AbstractControllerTest {

    private final int TRANSACTIONS_COUNT = 5;
    private List<Integer> sectionIds = Arrays.asList(1, 2, 3);

    @MockBean
    @Autowired
    private TransactionsMapper transactionsMapper;

    @MockBean
    @Autowired
    private SpendingSectionsMapper sectionsMapper;

    @MockBean
    @Autowired
    private UserMapper userMapper;

    @MockBean
    @Autowired
    private RegistryMapper registryMapper;

    @BeforeMethod
    public void prepareTransactionsSuccessfulScenario() {
        List<Transaction> transactionList = generateTransactionList(TRANSACTIONS_COUNT, sectionIds);

        when(transactionsMapper.getTransactions(anyString(), eq(null)))
                .thenReturn(transactionList);
        when(transactionsMapper.getTransactions(anyString(), any(TransactionsSearchFilter.class)))
                .thenReturn(transactionList);
        when(userMapper.getUserIdByLogin(anyString()))
                .thenReturn(1L);
        when(sectionsMapper.isSpendingSectionIdExists(anyString(), anyInt()))
                .thenReturn(true);
        when(transactionsMapper.addTransaction(any(Transaction.class)))
                .thenReturn(1L);
        when(transactionsMapper.updateTransaction(anyString(), anyLong(), any(Transaction.class)))
                .thenReturn(1);
        when(transactionsMapper.getTransactionById(anyString(), anyLong()))
                .thenReturn(generateTransaction());
        when(transactionsMapper.deleteTransaction(anyString(), anyLong()))
                .thenReturn(1);
    }

    @Test
    void shouldGetTransactionsWithNoFilter() throws Exception {
        mockMvc.perform(get("/api/transactions")
                .with(user(USER_LOGIN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serverStatus", is(SUCCESS.name())))
                .andExpect(jsonPath("$.payload[*]", hasSize(TRANSACTIONS_COUNT)));
    }

    @Test
    void shouldGetTransactionsWithFilter() throws Exception {
        TransactionsSearchFilter searchFilter = new TransactionsSearchFilter();
        searchFilter.setDateFrom(LocalDate.now().minus(1, ChronoUnit.DAYS));
        searchFilter.setDateTo(LocalDate.now());

        mockMvc.perform(post("/api/transactions/getFiltered")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(searchFilter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serverStatus", is(SUCCESS.name())))
                .andExpect(jsonPath("$.payload[*]", hasSize(TRANSACTIONS_COUNT)));
    }

    @Test
    void shouldAddTransaction() throws Exception {
        mockMvc.perform(post("/api/transactions")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(generateTransaction("title", "desc"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serverStatus", is(SUCCESS.name())))
                .andExpect(jsonPath("$.message", is(TRANSACTION_SAVED)))
                .andExpect(jsonPath("$.payload.title", is("title")))
                .andExpect(jsonPath("$.payload.description", is("desc")));
    }

    @Test
    void shouldUpdateTransaction() throws Exception {
        mockMvc.perform(put("/api/transactions")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new TransactionUpdateContainer(1L, generateTransaction("title", "desc")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serverStatus", is(SUCCESS.name())))
                .andExpect(jsonPath("$.message", is(TRANSACTION_UPDATED)))
                .andExpect(jsonPath("$.payload[*]", hasSize(greaterThan(0))));
    }

    @Test
    void shouldDeleteTransaction() throws Exception {
        mockMvc.perform(delete("/api/transactions/{transactionId}", 1)
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new TransactionUpdateContainer(1L, generateTransaction("title", "desc")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serverStatus", is(SUCCESS.name())))
                .andExpect(jsonPath("$.message", is(TRANSACTION_DELETED)));
    }

    @Test
    void shouldNotGetTransactionsWithFilter_rangeIsEmpty() throws Exception {
        mockMvc.perform(post("/api/transactions/getFiltered")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new TransactionsSearchFilter())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serverStatus", is(ERROR.name())))
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        Arrays.asList("Required fields are incorrect:", "dateFrom is empty", "dateTo is empty")
                )));
    }

    @Test
    void shouldNotGetTransactionsWithFilter_dateSequenceInvalid() throws Exception {
        TransactionsSearchFilter searchFilter = new TransactionsSearchFilter();
        searchFilter.setDateFrom(LocalDate.now());
        searchFilter.setDateTo(LocalDate.now().minus(1, ChronoUnit.DAYS));

        mockMvc.perform(post("/api/transactions/getFiltered")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(searchFilter)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serverStatus", is(ERROR.name())))
                .andExpect(jsonPath("$.message", is(DATE_SEQUENCE_INCORRECT)));
    }

    @Test(dataProvider = "incorrectTransactionAddDataProvider")
    void shouldNotAddTransaction_incorrectData(Transaction transaction, String expectedHint) throws Exception {
        mockMvc.perform(post("/api/transactions")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(transaction)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serverStatus", is(ERROR.name())))
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        Arrays.asList("Required fields are incorrect:", expectedHint)
                )));
    }

    @Test
    void shouldNotAddTransaction_nonexistentSectionId() throws Exception {
        when(sectionsMapper.isSpendingSectionIdExists(anyString(), anyInt()))
                .thenReturn(false);
        int sectionId = 1;

        mockMvc.perform(post("/api/transactions")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(generateTransaction(sectionId))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serverStatus", is(ERROR.name())))
                .andExpect(jsonPath("$.message", is(fillLog(SPENDING_SECTION_ID_NOT_EXISTS, String.valueOf(sectionId)))));
    }

    @Test
    void shouldNotAddTransaction_addingFailed() throws Exception {
        when(transactionsMapper.addTransaction(any(Transaction.class)))
                .thenReturn(null);

        mockMvc.perform(post("/api/transactions")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(generateTransaction())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serverStatus", is(ERROR.name())))
                .andExpect(jsonPath("$.message", is(TRANSACTION_SAVING_ERROR)));
    }

    @Test
    void shouldNotUpdateTransaction_incorrectData() throws Exception {
        Transaction transaction = generateTransaction();
        transaction.setSum(null);

        mockMvc.perform(put("/api/transactions")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new TransactionUpdateContainer(1L, transaction))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serverStatus", is(ERROR.name())))
                .andExpect(jsonPath("$.message", stringContainsInOrder(
                        Collections.singletonList("TRANSACTION has incorrect fields")
                )));
    }

    @Test
    void shouldNotUpdateTransaction_updatingFailed() throws Exception {
        when(transactionsMapper.updateTransaction(anyString(), anyLong(), any(Transaction.class)))
                .thenReturn(0);

        mockMvc.perform(put("/api/transactions")
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new TransactionUpdateContainer(1L, generateTransaction("title", "desc")))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serverStatus", is(ERROR.name())))
                .andExpect(jsonPath("$.message", is(TRANSACTION_NOT_UPDATED)));
    }

    @Test
    void shouldNotDeleteTransaction_idNotFound() throws Exception {
        when(transactionsMapper.getTransactionById(anyString(), anyLong()))
                .thenReturn(null);

        mockMvc.perform(delete("/api/transactions/{transactionId}", 1)
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new TransactionUpdateContainer(1L, generateTransaction("title", "desc")))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serverStatus", is(ERROR.name())))
                .andExpect(jsonPath("$.message", is(TRANSACTION_NOT_FOUND)));
    }

    @Test
    void shouldNotDeleteTransaction_deletionFailed() throws Exception {
        when(transactionsMapper.deleteTransaction(anyString(), anyLong()))
                .thenReturn(0);

        mockMvc.perform(delete("/api/transactions/{transactionId}", 1)
                .header("Content-Type", "application/json;charset=UTF-8")
                .with(user(USER_LOGIN))
                .content(serializeToJson(new TransactionUpdateContainer(1L, generateTransaction("title", "desc")))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.serverStatus", is(ERROR.name())))
                .andExpect(jsonPath("$.message", is(TRANSACTION_NOT_DELETED)));
    }

    @DataProvider(name = "incorrectTransactionAddDataProvider")
    public Object[][] dataProvider() {
        Transaction nullSum = generateTransaction();
        nullSum.setSum(null);
        Transaction zeroSum = generateTransaction();
        zeroSum.setSum(0);
        Transaction nullSectionId = generateTransaction();
        nullSectionId.setSectionId(null);
        return new Object[][]{{nullSum, "Transaction sum can not be empty or 0!"},
                {zeroSum, "Transaction sum can not be empty or 0!"}, {nullSectionId, "sectionId can not be empty or < 0!"}};
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
        PersonService personService(UserMapper userMapper) {
            return new PersonServiceImpl(userMapper);
        }
    }
}
