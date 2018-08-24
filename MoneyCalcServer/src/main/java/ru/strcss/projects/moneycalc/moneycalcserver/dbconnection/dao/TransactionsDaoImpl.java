package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;

import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.strcss.projects.moneycalc.enitities.Transaction;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.PersonDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.TransactionsDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.utils.DaoUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.List;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.localDate2String;

@Transactional
@Repository
public class TransactionsDaoImpl implements TransactionsDao {

    @Setter
    private SessionFactory sessionFactory;
    private PersonDao personDao;

    public TransactionsDaoImpl(SessionFactory sessionFactory, PersonDao personDao) {
        this.sessionFactory = sessionFactory;
        this.personDao = personDao;
    }

    @Override
    public Transaction getTransactionById(Integer transactionId) {
        Session session = sessionFactory.openSession();

        String hql = "FROM Transactions t WHERE t.id = :transactionId";
        Query<Transaction> query = session.createQuery(hql, Transaction.class)
                .setParameter("transactionId", transactionId);
        try {
            Transaction transaction = query.getSingleResult();
            session.close();
            return transaction;
        } catch (NoResultException nre) {
            session.close();
            return null;
        }
    }

    @Override
    public List<Transaction> getTransactionsByPersonId(Integer personId, LocalDate dateFrom, LocalDate dateTo, List<Integer> sectionIds) {

        List<Transaction> transactionList;
        try (Session session = sessionFactory.openSession()) {

            String hql;
            if (sectionIds == null || sectionIds.isEmpty()) {
                hql = "FROM Transactions t WHERE t.personId = :personId AND t.date BETWEEN :dateFrom AND :dateTo";
                transactionList = session.createQuery(hql, Transaction.class)
                        .setParameter("personId", personId)
                        .setParameter("dateFrom", dateFrom)
                        .setParameter("dateTo", dateTo)
                        .list();
            } else {
                hql = "FROM Transactions t WHERE t.personId = :personId AND t.date BETWEEN :dateFrom AND :dateTo AND t.sectionId IN (:ids)";
                transactionList = session.createQuery(hql, Transaction.class)
                        .setParameter("personId", personId)
                        .setParameter("dateFrom", (dateFrom))
                        .setParameter("dateTo", dateTo)
                        .setParameter("ids", sectionIds)
                        .list();
            }
        }
        return transactionList;
    }

    @Override
    public List<Transaction> getTransactionsByLogin(String login, LocalDate dateFrom, LocalDate dateTo, List<Integer> sectionIds) {
        Integer personId = personDao.getPersonIdByLogin(login);
        return this.getTransactionsByPersonId(personId, dateFrom, dateTo, sectionIds);
    }

    @Override
    public Integer addTransaction(Integer personId, Transaction transaction) {
        Session session = sessionFactory.openSession();
        transaction.setPersonId(personId);

        Integer addedTransactionId = (Integer) session.save(transaction);
        session.close();
        return addedTransactionId;
    }

    @Override
    public boolean updateTransaction(Transaction transaction) {
        return DaoUtils.updateEntity(sessionFactory, transaction);
//        Session session = sessionFactory.openSession();
//
//        boolean isError = false;
//        try {
//            session.beginTransaction();
//            session.update(transaction);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            isError = true;
//        } finally {
//            if (!isError)
//                session.getTransaction().commit();
//            else
//                session.getTransaction().rollback();
//            session.close();
//        }
//        return !isError;
    }

    @Override
    public boolean deleteTransaction(Transaction transaction) {
        Session session = sessionFactory.openSession();

        EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();

        boolean isError = false;
        try {
            entityManager.getTransaction().begin();
            entityManager.find(Transaction.class, transaction.getId());
            entityManager.remove(entityManager.contains(transaction) ? transaction : entityManager.merge(transaction));
            entityManager.getTransaction().commit();
            entityManager.clear();
        } catch (Exception ex) {
            ex.printStackTrace();
            isError = true;
        } finally {
            session.close();
        }
        return !isError;
    }
}
