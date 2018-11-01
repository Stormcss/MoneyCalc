package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import ru.strcss.projects.moneycalc.entities.Settings;

import javax.persistence.criteria.CriteriaQuery;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static ru.strcss.projects.moneycalc.testutils.Generator.generateSettings;

public class SettingsDaoImplTest {
    private SettingsDaoImpl settingsDao;

    private SessionFactory sessionFactory = mock(SessionFactory.class);
    private Transaction mockedTransaction = mock(Transaction.class);
    private Session mockedSession = mock(Session.class);
    private Query mockedQuery = mock(Query.class);


    @BeforeGroups(groups = "SettingsDaoSuccessfulScenario")
    public void prepare_successfulScenario() {
        Settings settings = generateSettings();
        settings.setId(0);

        when(mockedSession.createQuery(any(CriteriaQuery.class)))
                .thenReturn(mockedQuery);
        when(mockedSession.createQuery(anyString()))
                .thenReturn(mockedQuery);
        when(mockedSession.getTransaction())
                .thenReturn(mockedTransaction);
        when(mockedSession.save(any()))
                .thenReturn(1);
        when(mockedSession.merge(any()))
                .thenReturn(settings);
        when(mockedQuery.setParameter(anyString(), anyInt()))
                .thenReturn(mockedQuery);
        when(sessionFactory.openSession())
                .thenReturn(mockedSession);
        when(mockedQuery.getSingleResult())
                .thenReturn(generateSettings());

        settingsDao = new SettingsDaoImpl(sessionFactory);
    }


    @Test(groups = "SettingsDaoSuccessfulScenario")
    public void testSaveSettings() throws Exception {
        int settingsId = settingsDao.saveSettings(generateSettings());
        assertEquals(settingsId, 1);
    }

    @Test(groups = "SettingsDaoSuccessfulScenario")
    public void testUpdateSettings() throws Exception {
        Settings settings = generateSettings();

        Settings savedSettings = settingsDao.updateSettings(settings);

        assertEquals(savedSettings, settings);
    }

    @Test(groups = "SettingsDaoSuccessfulScenario")
    public void testGetSettingsById() throws Exception {
        Settings settingsById = settingsDao.getSettingsById(0);

        assertEquals(settingsById.getId(), 0);
    }

    @Test(groups = "SettingsDaoSuccessfulScenario")
    public void testSetSessionFactory() throws Exception {
        settingsDao.setSessionFactory(sessionFactory);
    }

}