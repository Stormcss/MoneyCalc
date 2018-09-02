package ru.strcss.projects.moneycalc.integration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.strcss.projects.moneycalc.integration.testapi.MoneyCalcClient;
import ru.strcss.projects.moneycalc.integration.utils.LocalDateAdapter;
import ru.strcss.projects.moneycalc.moneycalcserver.Application;

import java.sql.SQLException;
import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {
        Application.class
//        ServletWebServerFactoryAutoConfiguration.class
})
@TestPropertySource(properties = {
        "spring.datasource.username=h2DB",
        "spring.datasource.url=jdbc:h2:mem:test",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.datasource.password=password"
})
public abstract class AbstractIT extends AbstractTestNGSpringContextTests {

    @LocalServerPort
    private int SpringBootPort;

    @Autowired
    JdbcTemplate jdbcTemplate;

    MoneyCalcClient service;

    private final String CREATE_SCHEMA_PATH = "dbSchema/createSchema_v1.0.sql";
    private final String DROP_SCHEMA_PATH = "dbSchema/dropSchema_v1.0.sql";

    @BeforeSuite
    public void createDbSchema() throws Exception {
        super.springTestContextPrepareTestInstance();
        ScriptUtils.executeSqlScript(jdbcTemplate.getDataSource().getConnection(), new ClassPathResource(CREATE_SCHEMA_PATH));
    }

    @BeforeClass
    public void init() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:" + SpringBootPort + "/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        service = retrofit.create(MoneyCalcClient.class);
    }

    @AfterSuite
    public void dropDbSchema() throws SQLException {
        ScriptUtils.executeSqlScript(jdbcTemplate.getDataSource().getConnection(), new ClassPathResource(DROP_SCHEMA_PATH));
    }
}
