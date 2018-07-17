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

import static org.mockito.Matchers.*;
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
    public void setUp() throws Exception {
        when(sessionFactory.openSession())
                .thenReturn(mockedSession);
        when(mockedSession.getTransaction())
                .thenReturn(mockedTransaction);
        when(mockedSession.save(anyObject()))
                .thenReturn(1);
        when(mockedSession.createQuery(anyString()))
                .thenReturn(mockedQuery);
        when(mockedSession.createQuery(anyString(), any()))
                .thenReturn(mockedQuery);

        when(mockedQuery.setParameter(anyString(), anyInt()))
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
    public void testGetSectionIdByName() throws Exception {
        Integer sectionId = spendingSectionDao.getSectionIdByName(1, "sectionName");
        assertEquals((int) sectionId, 1);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testGetSectionIdById() throws Exception {
        Integer sectionId = spendingSectionDao.getSectionIdById(1, 1);
        assertEquals((int) sectionId, 1);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testIsSpendingSectionIdExists() throws Exception {
        when(mockedQuery.list())
                .thenReturn(Arrays.asList(1L, 2L));

        Boolean isSectionIdExists = spendingSectionDao.isSpendingSectionIdExists(1, 1);
        assertTrue(isSectionIdExists);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testIsSpendingSectionNameNew() throws Exception {
        boolean isNameNew = spendingSectionDao.isSpendingSectionNameNew(1, "newName");
        assertTrue(isNameNew);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testGetMaxSpendingSectionId() throws Exception {
        int maxSpendingSectionId = spendingSectionDao.getMaxSpendingSectionId(1);
        assertEquals(maxSpendingSectionId, 1);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testAddSpendingSection() throws Exception {
        Integer sectionId = spendingSectionDao.addSpendingSection(1, generateSpendingSection());
        assertEquals((int) sectionId, 1);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testUpdateSpendingSection() throws Exception {
        boolean isUpdateSuccessful = spendingSectionDao.updateSpendingSection(generateSpendingSection());
        assertTrue(isUpdateSuccessful);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testDeleteSpendingSection() throws Exception {
        boolean isDeleteSuccessful = spendingSectionDao.deleteSpendingSection(generateSpendingSection());
        assertTrue(isDeleteSuccessful);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testGetSpendingSectionById() throws Exception {
        SpendingSection spendingSection = spendingSectionDao.getSpendingSectionById(1);
        assertEquals((int) spendingSection.getId(), 1);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testGetSpendingSectionsByLogin() throws Exception {
        List<SpendingSection> spendingSection = spendingSectionDao.getSpendingSectionsByLogin("login");
        assertEquals(spendingSection.size(), 2);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testGetSpendingSectionsByPersonId() throws Exception {
        List<SpendingSection> spendingSection = spendingSectionDao.getSpendingSectionsByPersonId(1);
        assertEquals(spendingSection.size(), 2);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testSetSessionFactory() throws Exception {
        spendingSectionDao.setSessionFactory(sessionFactory);
    }


}