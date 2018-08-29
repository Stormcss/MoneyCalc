package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.PersonDao;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSpendingSection;

public class SpendingSectionDaoImplTest {

    private SpendingSectionDaoImpl spendingSectionDao;

    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Session mockedSession = mock(Session.class);
    private Transaction mockedTransaction = mock(Transaction.class);
    private Query mockedQuery = mock(Query.class);
    private PersonDao personDao = mock(PersonDao.class);

    @BeforeGroups(groups = "SpendingSectionDaoSuccessfulScenario")
    public void setUp() {
        when(sessionFactory.openSession())
                .thenReturn(mockedSession);
        when(mockedSession.getTransaction())
                .thenReturn(mockedTransaction);
        when(mockedSession.save(any()))
                .thenReturn(1);
        when(mockedSession.createQuery(anyString()))
                .thenReturn(mockedQuery);
        when(mockedSession.createQuery(anyString(), any()))
                .thenReturn(mockedQuery);

        when(mockedQuery.setParameter(anyString(), any()))
                .thenReturn(mockedQuery);

        when(mockedQuery.getSingleResult())
                .thenReturn(generateSpendingSection(null, null, 1, null, null, null));
        when(mockedQuery.list())
                .thenReturn(Arrays.asList(1, 2));

        when(personDao.getPersonIdByLogin("login"))
                .thenReturn(1);

        spendingSectionDao = new SpendingSectionDaoImpl(sessionFactory, personDao);

    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testGetSectionIdByName() {
        Integer sectionId = spendingSectionDao.getSectionIdByName(1, "sectionName");
        assertEquals((int) sectionId, 1);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testGetSectionIdById() {
        Integer sectionId = spendingSectionDao.getSectionIdByInnerId(1, 1);
        assertEquals((int) sectionId, 1);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testIsSpendingSectionIdExists() {
        when(mockedQuery.list())
                .thenReturn(Arrays.asList(1L, 2L));

        Boolean isSectionIdExists = spendingSectionDao.isSpendingSectionIdExists(1, 1);
        assertTrue(isSectionIdExists);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testIsSpendingSectionNameNew() {
        boolean isNameNew = spendingSectionDao.isSpendingSectionNameNew(1, "newName");
        assertTrue(isNameNew);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testGetMaxSpendingSectionId() {
        int maxSpendingSectionId = spendingSectionDao.getMaxSpendingSectionId(1);
        assertEquals(maxSpendingSectionId, 1);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testAddSpendingSection() {
        Integer sectionId = spendingSectionDao.addSpendingSection(1, generateSpendingSection());
        assertEquals((int) sectionId, 1);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testUpdateSpendingSection() {
        boolean isUpdateSuccessful = spendingSectionDao.updateSpendingSection(generateSpendingSection());
        assertTrue(isUpdateSuccessful);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testDeleteSpendingSection() {
        boolean isDeleteSuccessful = spendingSectionDao.deleteSpendingSectionByName("login", "name");
        assertTrue(isDeleteSuccessful);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testGetSpendingSectionById() {
        SpendingSection spendingSection = spendingSectionDao.getSpendingSectionById(1);
        assertEquals((int) spendingSection.getId(), 1);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testGetSpendingSectionsByLogin() {
        List<SpendingSection> spendingSection = spendingSectionDao.getSpendingSectionsByLogin("login");
        assertEquals(spendingSection.size(), 2);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testGetSpendingSectionsByPersonId() {
        List<SpendingSection> spendingSection = spendingSectionDao.getSpendingSectionsByPersonId(1);
        assertEquals(spendingSection.size(), 2);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testSetSessionFactory() {
        spendingSectionDao.setSessionFactory(sessionFactory);
    }


}