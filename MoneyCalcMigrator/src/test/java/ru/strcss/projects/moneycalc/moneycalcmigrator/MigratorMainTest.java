package ru.strcss.projects.moneycalc.moneycalcmigrator;

import okhttp3.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import retrofit2.Response;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Credentials;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.MoneyCalcRs;
import ru.strcss.projects.moneycalc.moneycalcdto.dto.Status;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Person;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcmigrator.api.FileReader;
import ru.strcss.projects.moneycalc.moneycalcmigrator.api.MigrationAPI;
import ru.strcss.projects.moneycalc.moneycalcmigrator.properties.MigrationProperties;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;
import static ru.strcss.projects.moneycalc.moneycalcmigrator.utils.TestUtils.mockedCall;

/**
 * Created by Stormcss
 * Date: 01.12.2018
 */
@ContextConfiguration(classes = {MigratorMainTest.Config.class})
@TestPropertySource(locations = "classpath:migration.properties")
public class MigratorMainTest extends AbstractTestNGSpringContextTests {

    @MockBean
    @Autowired
    private MigrationAPI migrationAPI;

    @Autowired
    private FileParser fileParser;

    private List<SpendingSection> spendingSections = new ArrayList<>();

    private Long lastTransactionId = 0L;

    @BeforeSuite
    private void init() throws Exception {
        super.springTestContextPrepareTestInstance();

        prepareSpendingSections();
        prepareMocks();
    }

    @Test
    public void shouldSaveNewTransactions() {
        fileParser.parseOldFiles(true);

        assertEquals(spendingSections.size(), 4);
        verify(migrationAPI, times(2)).addSpendingSection(anyString(), any(SpendingSection.class));
        verify(migrationAPI, times(46)).addTransaction(anyString(), any(Transaction.class));
    }

    private void prepareMocks(){
        when(migrationAPI.registerPerson(any(Credentials.class)))
                .thenReturn(mockedCall(Response.success(new MoneyCalcRs<>(Status.SUCCESS, new Person(), null))));

        final Headers headers = new Headers.Builder().add("Authorization", "Bearer TEST").build();
        Response<Void> successfulLoginResponse = Response.success(null, headers);
        when(migrationAPI.login(any(Access.class)))
                .thenReturn(mockedCall(successfulLoginResponse));

        when(migrationAPI.getSpendingSections(anyString()))
                .thenReturn(mockedCall(Response.success(new MoneyCalcRs<>(Status.SUCCESS, spendingSections, null))));

        when(migrationAPI.registerPerson(any(Credentials.class)))
                .thenReturn(mockedCall(Response.success(new MoneyCalcRs<>(Status.SUCCESS, new Person(), null))));

        doAnswer(invocation -> {
            SpendingSection addedSection = (SpendingSection) invocation.getArguments()[1];
            addedSection.setId(spendingSections.stream().mapToLong(SpendingSection::getSectionId).max().getAsLong() + 1);
            addedSection.setSectionId(spendingSections.stream().mapToInt(SpendingSection::getSectionId).max().getAsInt() + 1);
            spendingSections.add(addedSection);
            return mockedCall(Response.success(new MoneyCalcRs<>(Status.SUCCESS, spendingSections, null)));
        }).when(migrationAPI).addSpendingSection(anyString(), any(SpendingSection.class));

        doAnswer(invocation -> {
            Transaction addedTransaction = (Transaction) invocation.getArguments()[1];
            addedTransaction.setId(lastTransactionId + 1);
            lastTransactionId++;
            return mockedCall(Response.success(new MoneyCalcRs<>(Status.SUCCESS, addedTransaction, null)));
        }).when(migrationAPI).addTransaction(anyString(), any(Transaction.class));
    }

    private void prepareSpendingSections() {
        spendingSections.add(new SpendingSection(1L, 1L, 1, 1, "Food", true, false, 5000L));
        spendingSections.add(new SpendingSection(1L, 1L, 2, 2, "Other", true, false, 5000L));
    }

    @TestConfiguration
    protected static class Config {

        @Bean
        MigrationProperties migrationProperties() {
            return new MigrationProperties();
        }

        @Bean
        ServerConnector serverConnector(MigrationAPI migrationAPI) {
            return new ServerConnector(migrationAPI, migrationProperties());
        }

        @Bean
        FileReader fileReader() {
            return new FileReaderImpl();
        }

        @Bean
        FileParser fileParser(MigrationAPI migrationAPI) {
            return new FileParser(migrationProperties(), serverConnector(migrationAPI), fileReader());
        }
    }
}