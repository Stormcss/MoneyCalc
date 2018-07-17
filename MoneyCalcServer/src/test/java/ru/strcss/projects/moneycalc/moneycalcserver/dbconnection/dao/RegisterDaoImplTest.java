package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.Settings;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.AccessDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.IdentificationsDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.SettingsDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.SpendingSectionDao;

import javax.persistence.criteria.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static ru.strcss.projects.moneycalc.testutils.Generator.*;

public class RegisterDaoImplTest {
    private RegisterDaoImpl registerDao;

    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Session mockedSession = mock(Session.class);

    private CriteriaBuilder mockedCriteriaBuilder = mock(CriteriaBuilder.class);
    private CriteriaQuery<Access> mockedCriteriaQuery = mock(CriteriaQuery.class);
    private Root<Access> mockedRoot = mock(Root.class);
    private Path<Object> mockedPath = mock(Path.class);
    private Predicate mockedPredicate = mock(Predicate.class);
    private Query mockedQuery = mock(Query.class);

    private AccessDao accessDao = mock(AccessDao.class);
    private SettingsDao settingsDao = mock(SettingsDao.class);
    private IdentificationsDao identificationsDao = mock(IdentificationsDao.class);
    private SpendingSectionDao spendingSectionDao = mock(SpendingSectionDao.class);

    @BeforeGroups(groups = "RegisterDaoSuccessfulScenario")
    public void prepare_successfulScenario() {
        when(sessionFactory.openSession())
                .thenReturn(mockedSession);

        when(accessDao.saveAccess(any(Access.class)))
                .thenReturn(1);
        when(settingsDao.saveSettings(any(Settings.class)))
                .thenReturn(1);
        when(identificationsDao.saveIdentifications(any(Identifications.class)))
                .thenReturn(1);

        when(mockedSession.getCriteriaBuilder())
                .thenReturn(mockedCriteriaBuilder);
        when(mockedSession.createQuery(any(CriteriaQuery.class)))
                .thenReturn(mockedQuery);

        when(mockedQuery.getSingleResult())
                .thenReturn(generateAccess());

        when(mockedCriteriaQuery.from(Access.class))
                .thenReturn(mockedRoot);
        when(mockedCriteriaQuery.select(any()))
                .thenReturn(mockedCriteriaQuery);
        when(mockedRoot.get(anyString()))
                .thenReturn(mockedPath);

        when(mockedCriteriaBuilder.createQuery(Access.class))
                .thenReturn(mockedCriteriaQuery);

        when(mockedCriteriaBuilder.equal(any(Path.class), anyString()))
                .thenReturn(mockedPredicate);
        when(mockedPredicate.isNegated())
                .thenReturn(false);

        registerDao = new RegisterDaoImpl(sessionFactory, accessDao, settingsDao, identificationsDao, spendingSectionDao);
    }


    @Test(groups = "RegisterDaoSuccessfulScenario")
    public void testRegisterPerson() throws Exception {
        Person registeredPerson = registerDao.registerPerson(generateAccess(), generateIdentifications(), generateSettings());

        assertEquals(registeredPerson.getId(), 0);
        assertEquals(registeredPerson.getAccessId(), 1);
        assertEquals(registeredPerson.getIdentificationsId(), 1);
        assertEquals(registeredPerson.getSettingsId(), 1);
    }

    @Test(groups = "RegisterDaoSuccessfulScenario")
    public void testIsPersonExistsByLogin() throws Exception {
        boolean isExists = registerDao.isPersonExistsByLogin("login");
        assertTrue(isExists);
    }

    @Test(groups = "RegisterDaoSuccessfulScenario")
    public void testIsPersonExistsByEmail() throws Exception {
        boolean isExists = registerDao.isPersonExistsByEmail("email");
        assertTrue(isExists);
    }

    @Test(groups = "RegisterDaoSuccessfulScenario")
    public void testSetSessionFactory() throws Exception {
        registerDao.setSessionFactory(sessionFactory);
    }

}