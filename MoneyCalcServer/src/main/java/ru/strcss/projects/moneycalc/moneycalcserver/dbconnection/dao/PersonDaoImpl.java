//package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;
//
//import lombok.Setter;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.query.Query;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.PersonDao;
//
//import java.util.List;
//
//@Repository
//public class PersonDaoImpl implements PersonDao {
//
//    @Setter
//    private SessionFactory sessionFactory;
//
//    @Autowired
//    public PersonDaoImpl(SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//
//    @Override
//    public Integer getPersonIdByLogin(String login) {
//        Session session = sessionFactory.openSession();
//
//        String hql = "SELECT p.id FROM Person p join Access a ON p.accessId = a.id WHERE a.login = :login";
//        Query query = session.createQuery(hql)
//                .setParameter("login", login);
//
//        List list = query.list();
//
//        Integer personId;
//        if (!list.isEmpty())
//            personId = (Integer) list.get(0);
//        else
//            personId = null;
//        session.close();
//        return personId;
//    }
//
//    @Override
//    public Integer getSettingsIdByPersonId(Integer personId) {
//        Session session = sessionFactory.openSession();
//
//        String hql = "SELECT p.settingsId FROM Person p WHERE p.id = :personId";
//        Query query = session.createQuery(hql)
//                .setParameter("personId", personId);
//
//        Integer settingsId = (Integer) query.list().get(0);
//
//        session.close();
//        return settingsId;
//    }
//
//    @Override
//    public Integer getAccessIdByPersonId(Integer personId) {
//        return getIdFromPersonTable("accessId", personId);
//    }
//
//    @Override
//    public Integer getIdentificationsIdByPersonId(Integer personId) {
//        return getIdFromPersonTable("identificationsId", personId);
//    }
//
//    private Integer getIdFromPersonTable(String requiredField, Integer personId) {
//        Session session = sessionFactory.openSession();
//
//        String hql = "SELECT p." + requiredField + " FROM Person p WHERE p.id = :personId";
//        Query query = session.createQuery(hql)
//                .setParameter("personId", personId);
//
//        Integer settingsId = (Integer) query.list().get(0);
//
//        session.close();
//        return settingsId;
//    }
//
//}
