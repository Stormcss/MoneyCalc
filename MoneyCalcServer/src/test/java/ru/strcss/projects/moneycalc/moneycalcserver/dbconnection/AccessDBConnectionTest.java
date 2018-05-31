package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.moneycalcserver.mongo.PersonRepository;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateAccess;

public class AccessDBConnectionTest {

    private PersonRepository repository = mock(PersonRepository.class);
    private AccessDBConnection accessDBConnection = new AccessDBConnection(repository);

    @BeforeMethod
    public void setUp() throws Exception {
        Person person = Person.builder().access(generateAccess()).build();
        when(repository.findAccessByAccess_Login(anyString()))
                .thenReturn(person);
    }

    @Test
    public void testGetAccess() throws Exception {
        Access access = accessDBConnection.getAccess("login");

        assertNotNull(access, "Access is null!");
        assertNull(access.getPassword(), "password is not null!");
    }

}