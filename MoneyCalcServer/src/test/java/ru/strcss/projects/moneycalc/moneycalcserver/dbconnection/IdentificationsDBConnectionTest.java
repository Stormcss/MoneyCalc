package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection;

import com.mongodb.WriteResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.dto.crudcontainers.identifications.IdentificationsUpdateContainer;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.moneycalcserver.mongo.PersonRepository;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateIdentifications;

public class IdentificationsDBConnectionTest {

    private PersonRepository repository = mock(PersonRepository.class);
    private MongoTemplate mongoTemplate = mock(MongoTemplate.class);
    private IdentificationsDBConnection identificationsDBConnection = new IdentificationsDBConnection(repository, mongoTemplate);

    @BeforeClass
    public void setUp() throws Exception {
        when(repository.findIdentificationsByAccess_Login(anyString()))
                .thenReturn(Person.builder().identifications(generateIdentifications()).build());
        when(mongoTemplate.updateMulti(any(Query.class), any(Update.class), eq(Person.class)))
                .thenReturn(new WriteResult(1, false, new Object()));

    }

    @Test
    public void testUpdateIdentifications() throws Exception {
        Identifications identifications = identificationsDBConnection.getIdentifications("login");

        assertNotNull(identifications, "Identifications is null!");
    }

    @Test
    public void testGetIdentifications() throws Exception {
        IdentificationsUpdateContainer updateContainer = new IdentificationsUpdateContainer(generateIdentifications());

        WriteResult writeResult = identificationsDBConnection.updateIdentifications("login", updateContainer);

        assertEquals(writeResult.getN(), 1);
    }

}