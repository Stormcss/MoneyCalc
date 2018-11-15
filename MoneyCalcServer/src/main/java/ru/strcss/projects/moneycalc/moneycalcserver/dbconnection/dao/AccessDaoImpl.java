//package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;
//
//import lombok.Setter;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//import ru.strcss.projects.moneycalc.entities.Access;
//import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.AccessDao;
//
//import javax.persistence.NoResultException;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Root;
//
//@Repository
//public class AccessDaoImpl implements AccessDao {
//    @Setter
//    private SessionFactory sessionFactory;
//
//    @Autowired
//    public AccessDaoImpl(SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//
//    @Override
//    public Access getAccessById(String id) {
//        Session session = sessionFactory.openSession();
//
//        Transaction tx = session.beginTransaction();
//
//        CriteriaBuilder builder = session.getCriteriaBuilder();
//        CriteriaQuery<Access> query = builder.createQuery(Access.class);
//
//        Root<Access> root = query.from(Access.class);
//
//        query.select(root).where(builder.equal(root.get("id"), id));
//
//        Access access = session.createQuery(query).getSingleResult();
//
//        tx.commit();
//        session.close();
//        return access;
//    }
//
//    @Override
//    public Access getAccessByLogin(String login) {
//        Session session = sessionFactory.openSession();
//
//        Transaction tx = session.beginTransaction();
//
//        CriteriaBuilder builder = session.getCriteriaBuilder();
//        CriteriaQuery<Access> query = builder.createQuery(Access.class);
//
//        Root<Access> root = query.from(Access.class);
//
//        query.select(root).where(builder.equal(root.get("login"), login));
//        Access access;
//        try {
//            access = session.createQuery(query).getSingleResult();
//        } catch (NoResultException nre) {
//            access = null;
//        }
//
//        tx.commit();
//        session.close();
//        return access;
//    }
//
//    @Override
//    public int saveAccess(Access access) {
//        Session session = sessionFactory.openSession();
//        Integer savedAccessId = (Integer) session.save(access);
//        session.close();
//        return savedAccessId;
//    }
//}
