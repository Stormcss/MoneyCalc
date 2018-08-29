package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;

import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
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
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Transactions t WHERE t.id = :transactionId";
            Query<Transaction> query = session.createQuery(hql, Transaction.class)
                    .setParameter("transactionId", transactionId);
            return query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public List<Transaction> getTransactionsByPersonId(Integer personId, LocalDate dateFrom, LocalDate
            dateTo, List<Integer> sectionIds) {

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
    public List<Transaction> getTransactionsByLogin(String login, LocalDate dateFrom, LocalDate
            dateTo, List<Integer> sectionIds) {
        Integer personId = personDao.getPersonIdByLogin(login);
        return this.getTransactionsByPersonId(personId, dateFrom, dateTo, sectionIds);
    }

    @Override
    public List<Transaction> getTransactionsByLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            String sql = "select t.* from \"Transactions\" t\n" +
                    "join \"Person\" p on t.\"personId\" = p.id\n" +
                    "join \"Settings\" s on p.\"settingsId\" = s.id\n" +
                    "join \"Access\" a on p.\"accessId\" = a.id\n" +
                    "where\n" +
                    "    a.login = :login\n" +
                    "    AND t.date >= s.\"periodFrom\"\n" +
                    "    AND t.date < s.\"periodTo\"\n" +
                    "    AND t.\"sectionId\" IN (select ss.\"sectionId\" from \"SpendingSection\" ss\n" +
                    "                          where\n" +
                    "                            ss.\"personId\" = p.id\n" +
                    "                            AND ss.\"isAdded\" IS TRUE\n" +
                    "                            AND ss.\"isRemoved\" IS FALSE\n" +
                    "                         )";
            NativeQuery<Transaction> sqlQuery = session.createNativeQuery(sql, Transaction.class)
                    .setParameter("login", login);
            return sqlQuery.list();
        }
    }

    @Override
    public Integer addTransaction(Integer personId, Transaction transaction) {
        try (Session session = sessionFactory.openSession()) {
            transaction.setPersonId(personId);
            return (Integer) session.save(transaction);
        }
    }

    @Override
    public boolean updateTransaction(Transaction transaction) {
        return DaoUtils.updateEntity(sessionFactory, transaction);
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
