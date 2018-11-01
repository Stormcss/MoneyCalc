package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.dto.ResultContainer;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSpendingSection;

public class SpendingSectionDaoImplTest {

    private SpendingSectionDaoImpl spendingSectionDao;

    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Session mockedSession = mock(Session.class);
    private Transaction mockedTransaction = mock(Transaction.class);
    private Query mockedQuery = mock(Query.class);
    private NativeQuery mockedNativeQuery = mock(NativeQuery.class);

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
        when(mockedSession.createNativeQuery(anyString()))
                .thenReturn(mockedNativeQuery);
        when(mockedSession.createQuery(anyString(), any()))
                .thenReturn(mockedQuery);

        when(mockedQuery.setParameter(anyString(), any()))
                .thenReturn(mockedQuery);

        when(mockedNativeQuery.setParameter(anyString(), any()))
                .thenReturn(mockedNativeQuery);
        when(mockedNativeQuery.getSingleResult())
                .thenReturn(false);
        when(mockedNativeQuery.executeUpdate())
                .thenReturn(1);

        when(mockedQuery.getSingleResult())
                .thenReturn(generateSpendingSection(null, null, 1, null, null,
                        null, null));
        when(mockedQuery.list())
                .thenReturn(Arrays.asList(1, 2));

        spendingSectionDao = new SpendingSectionDaoImpl(sessionFactory);
    }

    @BeforeGroups(groups = "SpendingSectionDaoSuccessfulScenario_Get")
    public void setUp_getChecks() {
        spendingSectionDao = new SpendingSectionDaoImpl(sessionFactory);
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
    public void testDeleteSpendingSectionByInnerId() {
        ResultContainer deleteContainer = spendingSectionDao.deleteSpendingSectionByInnerId("login", 0);
        assertTrue(deleteContainer.isSuccess());
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario")
    public void testGetSpendingSectionById() {
        SpendingSection spendingSection = spendingSectionDao.getSpendingSectionById(1);
        assertEquals((int) spendingSection.getId(), 1);
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

    @Test(groups = "SpendingSectionDaoSuccessfulScenario_Get", dependsOnGroups = {"SpendingSectionDaoSuccessfulScenario"})
    public void testGetSpendingSectionsByLogin_all_false() {
        assertSqlQuery(true, "isAdded IS TRUE", true, "isRemoved IS FALSE");

        spendingSectionDao.getSpendingSectionsByLogin("login", false, false, false);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario_Get", dependsOnGroups = {"SpendingSectionDaoSuccessfulScenario"})
    public void testGetSpendingSectionsByLogin_withNonAdded() {
        assertSqlQuery(false, "isAdded", true, "isRemoved IS FALSE");
        spendingSectionDao.getSpendingSectionsByLogin("login", true, false, false);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario_Get", dependsOnGroups = {"SpendingSectionDaoSuccessfulScenario"})
    public void testGetSpendingSectionsByLogin_withRemoved() {
        assertSqlQuery(true, "isAdded IS TRUE", false, "isRemoved");
        spendingSectionDao.getSpendingSectionsByLogin("login", false, true, false);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario_Get", dependsOnGroups = {"SpendingSectionDaoSuccessfulScenario"})
    public void testGetSpendingSectionsByLogin_withRemovedOnly() {
        assertSqlQuery(true, "isAdded IS TRUE", true, "isRemoved IS TRUE");
        spendingSectionDao.getSpendingSectionsByLogin("login", false, false, true);
    }

    @Test(groups = "SpendingSectionDaoSuccessfulScenario_Get", dependsOnGroups = {"SpendingSectionDaoSuccessfulScenario"})
    public void testGetSpendingSectionsByLogin_withRemoved_withRemovedOnly() {
        assertSqlQuery(true, "isAdded IS TRUE", true, "isRemoved IS TRUE");
        spendingSectionDao.getSpendingSectionsByLogin("login", false, true, true);
    }

    private void assertSqlQuery(boolean isText1Expected, String text1, boolean isText2Expected, String text2) {
        doAnswer(invocation -> {
            String sqlQuery = invocation.getArgument(0);

            if (isText1Expected)
                assertTrue(sqlQuery.contains(text1));
            else
                assertFalse(sqlQuery.contains(text1));

            if (isText2Expected)
                assertTrue(sqlQuery.contains(text2));
            else
                assertFalse(sqlQuery.contains(text2));
            return mockedQuery;
        }).when(mockedSession).createQuery(anyString(), any());
    }
}