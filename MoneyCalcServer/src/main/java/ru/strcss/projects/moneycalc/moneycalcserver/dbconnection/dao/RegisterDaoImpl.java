//package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;
//
//import lombok.Setter;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.springframework.stereotype.Repository;
//import ru.strcss.projects.moneycalc.entities.*;
//import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.*;
//
//import javax.persistence.NoResultException;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
//import javax.transaction.Transactional;
//
//import static ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.utils.DaoUtils.isFieldExists;
//
//@Repository
//public class RegisterDaoImpl implements RegisterDao {
//    @Setter
//    private SessionFactory sessionFactory;
//
//    private AccessDao accessDao;
//    private SettingsDao settingsDao;
//    private SpendingSectionDao spendingSectionDao;
//    private IdentificationsDao identificationsDao;
//
//    public RegisterDaoImpl(SessionFactory sessionFactory, AccessDao accessDao, SettingsDao settingsDao, IdentificationsDao identificationsDao,
//                           SpendingSectionDao spendingSectionDao) {
//        this.sessionFactory = sessionFactory;
//        this.accessDao = accessDao;
//        this.settingsDao = settingsDao;
//        this.identificationsDao = identificationsDao;
//        this.spendingSectionDao = spendingSectionDao;
//    }
//
//    @Override
//    @Transactional
//    public Person registerPerson(Access access, Identifications identifications, Settings settings) {
//        Session session = sessionFactory.openSession();
//
//        int savedAccessId = accessDao.saveAccess(access);
//        int savedSettingsId = settingsDao.saveSettings(settings);
//        int savedIdentificationsId = identificationsDao.saveIdentifications(identifications);
//
//        Person person = Person.builder()
//                .accessId(savedAccessId)
//                .identificationsId(savedIdentificationsId)
//                .settingsId(savedSettingsId)
//                .build();
//
//        Integer personId = (Integer) session.save(person);
//
//        SpendingSection section1 = SpendingSection.builder()
//                .sectionId(0)
//                .isAdded(true)
//                .isRemoved(false)
//                .budget(5000)
//                .name("Еда")
//                .build();
//
//        SpendingSection section2 = SpendingSection.builder()
//                .sectionId(1)
//                .isAdded(true)
//                .isRemoved(false)
//                .budget(5000)
//                .name("Прочее")
//                .build();
//
//        spendingSectionDao.addSpendingSection(personId, section1);
//        spendingSectionDao.addSpendingSection(personId, section2);
//
//        session.close();
//        return person;
//    }
//
//    @Override
//    @Transactional
//    public boolean isPersonExistsByLogin(String login) {
//        Session session = sessionFactory.openSession();
//
//        CriteriaBuilder builder = session.getCriteriaBuilder();
//        CriteriaQuery<Access> query = builder.createQuery(Access.class);
//
//        Root<Access> root = query.from(Access.class);
//
//        query.select(root).where(builder.equal(root.get("login"), login));
//
//        Access access;
//        try {
//            access = session.createQuery(query).getSingleResult();
//        } catch (NoResultException nre) {
//            return false;
//        }
//        return access != null;
//    }
//
//    @Override
//    public boolean isPersonExistsByEmail(String email) {
//        return isFieldExists(sessionFactory, Access.class, "email", email);
//    }
//}
