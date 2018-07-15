package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.enitities.Access;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateAccess;

public class AccessDaoImplTest {

    private AccessDaoImpl accessDao;

    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Session mockedSession = mock(Session.class);
    private Transaction mockedTransaction = mock(Transaction.class);
    private CriteriaBuilder mockedCriteriaBuilder = mock(CriteriaBuilder.class);
    private CriteriaQuery<Access> mockedCriteriaQuery = mock(CriteriaQuery.class);
    private Root<Access> mockedRoot = mock(Root.class);
    private Query mockedQuery = mock(Query.class);

    @BeforeGroups(groups = "AccessDaoSuccessfulScenario")
    public void prepare_successfulScenario() {

        when(mockedSession.beginTransaction())
                .thenReturn(mockedTransaction);
        when(mockedSession.getCriteriaBuilder())
                .thenReturn(mockedCriteriaBuilder);
        when(mockedSession.createQuery(any(CriteriaQuery.class)))
                .thenReturn(mockedQuery);
        when(mockedSession.save(any()))
                .thenReturn(1);
        doNothing().when(mockedSession).close();

        when(mockedCriteriaBuilder.createQuery(Access.class))
                .thenReturn(mockedCriteriaQuery);

        when(mockedCriteriaQuery.from(Access.class))
                .thenReturn(mockedRoot);
        when(mockedCriteriaQuery.select(any()))
                .thenReturn(mockedCriteriaQuery);

        when(mockedQuery.getSingleResult())
                .thenReturn(generateAccess());

        when(sessionFactory.openSession())
                .thenReturn(mockedSession);
        when(sessionFactory.getCriteriaBuilder())
                .thenReturn(mockedCriteriaBuilder);

        doNothing().when(mockedTransaction).commit();

        accessDao = new AccessDaoImpl(sessionFactory);
    }


    @Test(groups = "AccessDaoSuccessfulScenario")
    public void testGetAccessById() throws Exception {
        Access accessById = accessDao.getAccessById("1");

        assertNotNull(accessById, "access is null!");
    }

    @Test(groups = "AccessDaoSuccessfulScenario")
    public void testGetAccessByLogin() throws Exception {
        Access accessById = accessDao.getAccessByLogin("1");

        assertNotNull(accessById, "access is null!");
    }

    @Test(groups = "AccessDaoSuccessfulScenario")
    public void testSaveAccess() throws Exception {
        int accessId = accessDao.saveAccess(generateAccess());

        assertEquals(accessId, 1, "access is not l!");
    }

    @Test(groups = "AccessDaoSuccessfulScenario")
    public void testSetSessionFactory() throws Exception {
        accessDao.setSessionFactory(sessionFactory);
    }

}