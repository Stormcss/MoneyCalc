//package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;
//
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
//import org.hibernate.query.Query;
//import org.testng.annotations.BeforeGroups;
//import org.testng.annotations.Test;
//import ru.strcss.projects.moneycalc.entities.Identifications;
//
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
//
//import static org.mockito.Matchers.any;
//import static org.mockito.Matchers.anyInt;
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.*;
//import static org.testng.Assert.assertEquals;
//import static org.testng.Assert.assertNotNull;
//import static ru.strcss.projects.moneycalc.testutils.Generator.generateIdentifications;
//
//public class IdentificationsDaoImplTest {
//
//    private IdentificationsDaoImpl identificationsDao;
//
//    private SessionFactory sessionFactory = mock(SessionFactory.class);
//    private Session mockedSession = mock(Session.class);
//    private Transaction mockedTransaction = mock(Transaction.class);
//    private CriteriaBuilder mockedCriteriaBuilder = mock(CriteriaBuilder.class);
//    private CriteriaQuery<Identifications> mockedCriteriaQuery = mock(CriteriaQuery.class);
//    private Root<Identifications> mockedRoot = mock(Root.class);
//    private Query mockedQuery = mock(Query.class);
//
//    @BeforeGroups(groups = "IdentificationsDaoSuccessfulScenario")
//    public void prepare_successfulScenario() {
//
//        when(mockedSession.beginTransaction())
//                .thenReturn(mockedTransaction);
//        when(mockedSession.getTransaction())
//                .thenReturn(mockedTransaction);
//        when(mockedSession.getCriteriaBuilder())
//                .thenReturn(mockedCriteriaBuilder);
//        when(mockedSession.createQuery(any(CriteriaQuery.class)))
//                .thenReturn(mockedQuery);
//        when(mockedSession.createQuery(anyString()))
//                .thenReturn(mockedQuery);
//        when(mockedSession.save(any()))
//                .thenReturn(1);
//        when(mockedSession.merge(any()))
//                .thenReturn(generateIdentifications());
//        doNothing().when(mockedSession).close();
//
//        when(mockedCriteriaBuilder.createQuery(Identifications.class))
//                .thenReturn(mockedCriteriaQuery);
//
//        when(mockedCriteriaQuery.from(Identifications.class))
//                .thenReturn(mockedRoot);
//        when(mockedCriteriaQuery.select(any()))
//                .thenReturn(mockedCriteriaQuery);
//
//        when(mockedQuery.getSingleResult())
//                .thenReturn(generateIdentifications());
//        when(mockedQuery.setParameter(anyString(), anyInt()))
//                .thenReturn(mockedQuery);
//
//        when(sessionFactory.openSession())
//                .thenReturn(mockedSession);
//        when(sessionFactory.getCriteriaBuilder())
//                .thenReturn(mockedCriteriaBuilder);
//
//        doNothing().when(mockedTransaction).commit();
//
//        identificationsDao = new IdentificationsDaoImpl(sessionFactory);
//    }
//
//    @Test(groups = "IdentificationsDaoSuccessfulScenario")
//    public void testSaveIdentifications() throws Exception {
//        int identificationsId = identificationsDao.saveIdentifications(generateIdentifications());
//        assertEquals(identificationsId, 1, "identificationsId is not 1!");
//    }
//
//    @Test(groups = "IdentificationsDaoSuccessfulScenario")
//    public void testUpdateIdentifications() throws Exception {
//        Identifications updatedIdentifications = identificationsDao.updateIdentifications(generateIdentifications());
//        assertNotNull(updatedIdentifications);
//    }
//
//    @Test(groups = "IdentificationsDaoSuccessfulScenario")
//    public void testGetIdentificationsById() throws Exception {
//        Identifications identificationsById = identificationsDao.getIdentifications(1);
//        assertNotNull(identificationsById);
//    }
//
//    @Test(groups = "IdentificationsDaoSuccessfulScenario")
//    public void testSetSessionFactory() throws Exception {
//        identificationsDao.setSessionFactory(sessionFactory);
//    }
//
//}