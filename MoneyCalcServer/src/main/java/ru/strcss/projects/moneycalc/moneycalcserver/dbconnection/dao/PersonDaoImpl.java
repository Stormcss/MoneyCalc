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
//        Integer userId;
//        if (!list.isEmpty())
//            userId = (Integer) list.get(0);
//        else
//            userId = null;
//        session.close();
//        return userId;
//    }
//
//    @Override
//    public Integer getSettingsIdByPersonId(Integer userId) {
//        Session session = sessionFactory.openSession();
//
//        String hql = "SELECT p.settingsId FROM Person p WHERE p.id = :userId";
//        Query query = session.createQuery(hql)
//                .setParameter("userId", userId);
//
//        Integer settingsId = (Integer) query.list().get(0);
//
//        session.close();
//        return settingsId;
//    }
//
//    @Override
//    public Integer getAccessIdByPersonId(Integer userId) {
//        return getIdFromPersonTable("accessId", userId);
//    }
//
//    @Override
//    public Integer getIdentificationsIdByPersonId(Integer userId) {
//        return getIdFromPersonTable("identificationsId", userId);
//    }
//
//    private Integer getIdFromPersonTable(String requiredField, Integer userId) {
//        Session session = sessionFactory.openSession();
//
//        String hql = "SELECT p." + requiredField + " FROM Person p WHERE p.id = :userId";
//        Query query = session.createQuery(hql)
//                .setParameter("userId", userId);
//
//        Integer settingsId = (Integer) query.list().get(0);
//
//        session.close();
//        return settingsId;
//    }
//
//}
