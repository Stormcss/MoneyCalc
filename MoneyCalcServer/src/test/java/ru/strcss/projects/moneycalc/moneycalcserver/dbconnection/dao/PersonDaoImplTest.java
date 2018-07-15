package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import javax.persistence.criteria.CriteriaQuery;
import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateIdentifications;

public class PersonDaoImplTest {

    private PersonDaoImpl personDao;

    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Session mockedSession = mock(Session.class);
    private Query mockedQuery = mock(Query.class);

    @BeforeGroups(groups = "PersonDaoSuccessfulScenario")
    public void prepare_successfulScenario() {

        when(mockedSession.createQuery(any(CriteriaQuery.class)))
                .thenReturn(mockedQuery);
        when(mockedSession.createQuery(anyString()))
                .thenReturn(mockedQuery);
        when(mockedSession.save(any()))
                .thenReturn(1);
        when(mockedSession.merge(any()))
                .thenReturn(generateIdentifications());
        doNothing().when(mockedSession).close();

        when(mockedQuery.list())
                .thenReturn(Arrays.asList(1));
        when(mockedQuery.setParameter(anyString(), anyInt()))
                .thenReturn(mockedQuery);

        when(sessionFactory.openSession())
                .thenReturn(mockedSession);

        personDao = new PersonDaoImpl(sessionFactory);
    }

    @Test(groups = "PersonDaoSuccessfulScenario")
    public void testGetPersonIdByLogin() throws Exception {
        Integer personId = personDao.getPersonIdByLogin("login");
        assertEquals((int) personId, 1);
    }

    @Test(groups = "PersonDaoSuccessfulScenario")
    public void testGetSettingsIdByPersonId() throws Exception {
        Integer settingsId = personDao.getSettingsIdByPersonId(1);
        assertEquals((int) settingsId, 1);
    }

    @Test(groups = "PersonDaoSuccessfulScenario")
    public void testGetAccessIdByPersonId() throws Exception {
        Integer accessId = personDao.getAccessIdByPersonId(1);
        assertEquals((int) accessId, 1);
    }

    @Test(groups = "PersonDaoSuccessfulScenario")
    public void testGetIdentificationsIdByPersonId() throws Exception {
        Integer identificationsId = personDao.getIdentificationsIdByPersonId(1);
        assertEquals((int) identificationsId, 1);
    }

    @Test(groups = "PersonDaoSuccessfulScenario")
    public void testSetSessionFactory() throws Exception {
        personDao.setSessionFactory(sessionFactory);
    }

}