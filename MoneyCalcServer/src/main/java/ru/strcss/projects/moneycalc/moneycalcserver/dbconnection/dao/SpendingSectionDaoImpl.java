package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;

import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.PersonDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.SpendingSectionDao;

import javax.persistence.NoResultException;
import java.util.List;

import static ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.utils.DaoUtils.updateEntity;

@Repository
@Transactional
public class SpendingSectionDaoImpl implements SpendingSectionDao {

    @Setter
    private SessionFactory sessionFactory;
    private PersonDao personDao;


    public SpendingSectionDaoImpl(SessionFactory sessionFactory, PersonDao personDao) {
        this.sessionFactory = sessionFactory;
        this.personDao = personDao;
    }

    @Override
    public Integer getSectionIdByName(Integer personId, String sectionName) {
        Session session = sessionFactory.openSession();

        String hql = "SELECT ss.id FROM SpendingSection ss WHERE ss.personId = :personId AND ss.name = :sectionName";
        Query query = session.createQuery(hql)
                .setParameter("personId", personId)
                .setParameter("sectionName", sectionName);

        Integer settingsId = (Integer) query.list().get(0);

        session.close();
        return settingsId;
    }

    @Override
    public Integer getSectionIdById(Integer personId, Integer innerSectionId) {
        Session session = sessionFactory.openSession();

        String hql = "SELECT ss.id FROM SpendingSection ss WHERE ss.personId = :personId AND ss.sectionId = :sectionId";
        List list = session.createQuery(hql)
                .setParameter("personId", personId)
                .setParameter("sectionId", innerSectionId)
                .list();

        if (list.isEmpty())
            return null;
        Integer settingsId = (Integer) list.get(0);

        session.close();
        return settingsId;
    }

    @Override
    public Boolean isSpendingSectionIdExists(Integer personId, Integer sectionId) {
        Session session = sessionFactory.openSession();
        String hql = "SELECT COUNT(*) FROM SpendingSection ss WHERE ss.personId = :personId AND ss.sectionId = :sectionId";
        Query<Long> query = session.createQuery(hql, Long.class)
                .setParameter("personId", personId)
                .setParameter("sectionId", sectionId);

        Long count = query.list().get(0);
        session.close();
        return count != 0;

    }

    @Override
    public boolean isSpendingSectionNameNew(Integer id, String name) {
        Session session = sessionFactory.openSession();

        String hql = "SELECT 1 from SpendingSection ss WHERE ss.personId = :personId AND ss.name = :name";

        Query query = session.createQuery(hql)
                .setParameter("personId", id)
                .setParameter("name", name);
        boolean isNew = query.uniqueResult() == null;
        session.close();

        return isNew;
    }

    @Override
    public int getMaxSpendingSectionId(Integer personId) {
        Session session = sessionFactory.openSession();
        String hql = "SELECT MAX(sectionId) FROM SpendingSection ss WHERE ss.personId = :personId";
        Query query = session.createQuery(hql)
                .setParameter("personId", personId);

        Integer integer = (Integer) query.list().get(0);
        session.close();
        return integer;
    }

    @Override
    public Integer addSpendingSection(Integer personId, SpendingSection section) {
        Session session = sessionFactory.openSession();
        section.setPersonId(personId);

        Integer addedSectionId = (Integer) session.save(section);
        session.close();
        return addedSectionId;
    }

    @Override
    public boolean updateSpendingSection(SpendingSection section) {
        return updateEntity(sessionFactory, section);
        //        Session session = sessionFactory.openSession();
//
//        boolean isError = false;
//        try {
//            session.beginTransaction();
//            session.update(section);
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
    public boolean deleteSpendingSection(SpendingSection section) {
        section.setIsRemoved(true);
        return updateEntity(sessionFactory, section);
//        Session session = sessionFactory.openSession();
//
//        EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
//
//        boolean isError = false;
//        try {
//            entityManager.getTransaction().begin();
//            entityManager.find(SpendingSection.class, section.getId());
//            entityManager.remove(entityManager.contains(section) ? section : entityManager.merge(section));
//            entityManager.getTransaction().commit();
//            entityManager.clear();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            isError = true;
//        } finally {
//            session.close();
//        }
//        return !isError;
    }

    @Override
    public SpendingSection getSpendingSectionById(Integer sectionId) {
        Session session = sessionFactory.openSession();

        String hql = "FROM SpendingSection ss WHERE ss.id = :sectionId";

        Query<SpendingSection> query = session.createQuery(hql, SpendingSection.class)
                .setParameter("sectionId", sectionId);

        try {
            SpendingSection spendingSection = query.getSingleResult();
            session.close();
            return spendingSection;
        } catch (NoResultException nre) {
            session.close();
            return null;
        }
    }

    @Override
    public List<SpendingSection> getSpendingSectionsByLogin(String login) {
        Integer personId = personDao.getPersonIdByLogin(login);
        return this.getSpendingSectionsByPersonId(personId);
    }

    @Override
    public List<SpendingSection> getSpendingSectionsByPersonId(Integer personId) {
        Session session = sessionFactory.openSession();

        String hql = "FROM SpendingSection ss WHERE ss.personId = :personId";

        Query<SpendingSection> query = session.createQuery(hql, SpendingSection.class)
                .setParameter("personId", personId);

        List<SpendingSection> sectionList = query.list();
        session.close();
        return sectionList;
    }

    @Override
    public List<SpendingSection> getActiveSpendingSectionsByPersonId(Integer personId) {
        List<SpendingSection> sectionList;
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM SpendingSection ss where ss.personId = :personId AND ss.isAdded IS TRUE";

            Query<SpendingSection> query = session.createQuery(hql, SpendingSection.class)
                    .setParameter("personId", personId);

            sectionList = query.list();
        }
        return sectionList;
    }

    @Override
    public List<Integer> getActiveSpendingSectionIdsByPersonId(Integer personId) {
        List<Integer> idsList;
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT ss.sectionId FROM SpendingSection ss where ss.personId = :personId AND ss.isAdded IS TRUE ORDER BY sectionId ASC";

            Query<Integer> query = session.createQuery(hql, Integer.class)
                    .setParameter("personId", personId);
            idsList = query.list();
        }
        return idsList;
    }
}
