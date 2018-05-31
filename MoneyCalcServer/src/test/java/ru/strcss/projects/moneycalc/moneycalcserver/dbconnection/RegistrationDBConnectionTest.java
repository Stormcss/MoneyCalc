package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection;

import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.moneycalcserver.mongo.PersonRepository;

import java.util.Arrays;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateAccess;

public class RegistrationDBConnectionTest {

    private PersonRepository repository = mock(PersonRepository.class);
    private MongoTemplate mongoTemplate = mock(MongoTemplate.class);
    private RegistrationDBConnection registrationDBConnection = new RegistrationDBConnection(repository, mongoTemplate);

    @BeforeClass
    public void setUp() throws Exception {
        when(repository.existsByAccess_Login(anyString()))
                .thenReturn(true);
        when(repository.existsByAccess_Email(anyString()))
                .thenReturn(true);
        when(mongoTemplate.aggregate(any(Aggregation.class), eq(Person.class), eq(Access.class)))
                .thenReturn(new AggregationResults<>(Arrays.asList(generateAccess()), new BasicDBObject() {
                }));

    }

    @Test
    public void testIsPersonExistsByLogin() throws Exception {
        boolean existsByLogin = registrationDBConnection.isPersonExistsByLogin("login");
        assertTrue(existsByLogin);
    }

    @Test
    public void testIsPersonExistsByEmail() throws Exception {
        boolean existsByLogin = registrationDBConnection.isPersonExistsByEmail("email");
        assertTrue(existsByLogin);
    }

    @Test
    public void testGetAccessByLogin() throws Exception {
        Access access = registrationDBConnection.getAccessByLogin("login");
        assertNotNull(access);
        assertNotNull(access.getLogin());
        assertNotNull(access.getPassword());
        assertNotNull(access.getEmail());
    }

}