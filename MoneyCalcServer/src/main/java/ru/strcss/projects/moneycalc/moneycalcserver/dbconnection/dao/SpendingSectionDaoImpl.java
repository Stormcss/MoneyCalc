//package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;
//
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.query.Query;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//import ru.strcss.projects.moneycalc.entities.SpendingSection;
//import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.SpendingSectionDao;
//import ru.strcss.projects.moneycalc.moneycalcserver.dto.ResultContainer;
//
//import javax.persistence.NoResultException;
//import java.util.List;
//import java.util.StringJoiner;
//
//import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_ALREADY_DELETED;
//import static ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.utils.DaoUtils.updateEntity;
//
//@Slf4j
//@Repository
//@Transactional
//public class SpendingSectionDaoImpl implements SpendingSectionDao {
//
//    @Setter
//    private SessionFactory sessionFactory;
//
//
//    public SpendingSectionDaoImpl(SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//
//    @Override
//    public Integer getSectionIdByName(Integer personId, String sectionName) {
//        try (Session session = sessionFactory.openSession()) {
//            String hql = "SELECT ss.id FROM SpendingSection ss WHERE ss.personId = :personId AND ss.name = :sectionName";
//            Query query = session.createQuery(hql)
//                    .setParameter("personId", personId)
//                    .setParameter("sectionName", sectionName);
//
//            Integer settingsId = (Integer) query.list().get(0);
//
//            return settingsId;
//        }
//    }
//
//    @Override
//    public Integer getSectionIdByInnerId(Integer personId, Integer innerSectionId) {
//        try (Session session = sessionFactory.openSession()) {
//            String hql = "SELECT ss.id FROM SpendingSection ss WHERE ss.personId = :personId AND ss.sectionId = :sectionId";
//            List list = session.createQuery(hql)
//                    .setParameter("personId", personId)
//                    .setParameter("sectionId", innerSectionId)
//                    .list();
//
//            if (list.isEmpty())
//                return null;
//            Integer settingsId = (Integer) list.get(0);
//
//            return settingsId;
//        }
//    }
//
//    @Override
//    public Boolean isSpendingSectionIdExists(Integer personId, Integer sectionId) {
//        try (Session session = sessionFactory.openSession()) {
//            String hql = "SELECT COUNT(*) FROM SpendingSection ss WHERE ss.personId = :personId AND ss.sectionId = :sectionId";
//            Query<Long> query = session.createQuery(hql, Long.class)
//                    .setParameter("personId", personId)
//                    .setParameter("sectionId", sectionId);
//
//            Long count = query.list().get(0);
//            return count != 0;
//        }
//    }
//
//    @Override
//    public boolean isSpendingSectionNameNew(Integer id, String name) {
//        try (Session session = sessionFactory.openSession()) {
//            String hql = "SELECT 1 from SpendingSection ss WHERE ss.personId = :personId AND ss.name = :name";
//
//            Query query = session.createQuery(hql)
//                    .setParameter("personId", id)
//                    .setParameter("name", name);
//            boolean isNew = query.uniqueResult() == null;
//            return isNew;
//        }
//    }
//
//    @Override
//    public int getMaxSpendingSectionId(Integer personId) {
//        try (Session session = sessionFactory.openSession()) {
//            String hql = "SELECT MAX(sectionId) FROM SpendingSection ss WHERE ss.personId = :personId";
//            Query query = session.createQuery(hql)
//                    .setParameter("personId", personId);
//
//            Integer integer = (Integer) query.list().get(0);
//            return integer;
//        }
//    }
//
//    @Override
//    public Integer addSpendingSection(Integer personId, SpendingSection section) {
//        try (Session session = sessionFactory.openSession()) {
//            section.setPersonId(personId);
//
//            Integer addedSectionId = (Integer) session.save(section);
//            return addedSectionId;
//        }
//    }
//
//    @Override
//    public boolean updateSpendingSection(SpendingSection section) {
//        return updateEntity(sessionFactory, section);
//    }
//
//    @Override
//    public ResultContainer deleteSpendingSectionByInnerId(String login, Integer innerId) {
//        try (Session session = sessionFactory.openSession()) {
//            session.getTransaction().begin();
//
//            String isAlreadyDeletedSqlQuery =
//                    "select ss.\"isRemoved\"\n" +
//                            "FROM \"SpendingSection\" ss\n" +
//                            "WHERE ss.id = (select ss.id\n" +
//                            "                from \"Person\" p\n" +
//                            "                       join \"Access\" a on p.\"accessId\" = a.id\n" +
//                            "                       join \"SpendingSection\" ss on ss.\"personId\" = p.id\n" +
//                            "                WHERE a.login = :login\n" +
//                            "                  AND ss.\"sectionId\" = :innerId)";
//            Query isAlreadyDeletedQuery = session.createNativeQuery(isAlreadyDeletedSqlQuery)
//                    .setParameter("login", login)
//                    .setParameter("innerId", innerId);
//
//            Boolean isAlreadyDeleted = (Boolean) isAlreadyDeletedQuery.getSingleResult();
//
//            if (isAlreadyDeleted) {
//                session.getTransaction().rollback();
//                log.info("deleteSpendingSectionByInnerId - trying to delete already deleted SpendingSection. login: '{}'", login);
//                return new ResultContainer(false, SPENDING_SECTION_ALREADY_DELETED, null);
//            }
//
//            String sqlQuery =
//                    "update \"SpendingSection\" s\n" +
//                            "SET \"isRemoved\" = TRUE, name = name || '_#del'\n" +
//                            "WHERE s.id = (select ss.id\n" +
//                            "                from \"Person\" p\n" +
//                            "                       join \"Access\" a on p.\"accessId\" = a.id\n" +
//                            "                       join \"SpendingSection\" ss on ss.\"personId\" = p.id\n" +
//                            "                WHERE a.login = :login\n" +
//                            "                  AND ss.\"sectionId\" = :innerId)";
//            Query query = session.createNativeQuery(sqlQuery)
//                    .setParameter("login", login)
//                    .setParameter("innerId", innerId);
//
//            int updatedCount = query.executeUpdate();
//            session.getTransaction().commit();
//            if (updatedCount == 0) {
//                log.error("deleteSpendingSectionByInnerId - updatedCount is 0");
//                return new ResultContainer(false, null, "Updated Count is 0");
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            log.error("deleteSpendingSectionByInnerId - {}", ex.getMessage());
//            return new ResultContainer(false, null, ex.getMessage());
//        }
//        return new ResultContainer(true);
//    }
//
//    @Override
//    public SpendingSection getSpendingSectionById(Integer sectionId) {
//        try (Session session = sessionFactory.openSession()) {
//            String hql = "FROM SpendingSection ss WHERE ss.id = :sectionId";
//
//            Query<SpendingSection> query = session.createQuery(hql, SpendingSection.class)
//                    .setParameter("sectionId", sectionId);
//
//            return query.getSingleResult();
//        } catch (NoResultException nre) {
//            return null;
//        }
//    }
//
//    @Override
//    public List<SpendingSection> getSpendingSectionsByLogin(String login, boolean withNonAdded,
//                                                            boolean withRemoved, boolean withRemovedOnly) {
//        List<SpendingSection> sectionList;
//        StringJoiner joiner = new StringJoiner(" AND ");
//
//        try (Session session = sessionFactory.openSession()) {
//            String baseSqlQuery = "select ss from SpendingSection ss \n" +
//                    "join Person p on ss.personId = p.id \n" +
//                    "join Access a on a.id = p.accessId \n" +
//                    "where a.login = :login";
//
//            joiner.add(baseSqlQuery);
//            if (!withNonAdded)
//                joiner.add("ss.isAdded IS TRUE");
//            if (!withRemovedOnly && !withRemoved)
//                joiner.add("ss.isRemoved IS FALSE");
//            else if (withRemovedOnly)
//                joiner.add("ss.isRemoved IS TRUE");
//            String queryString = joiner.toString() + " ORDER BY ss.sectionId ASC";
//
//            Query<SpendingSection> query = session.createQuery(queryString, SpendingSection.class)
//                    .setParameter("login", login);
//
//            sectionList = query.list();
//        }
//        return sectionList;
//    }
//
//    @Override
//    public List<SpendingSection> getSpendingSectionsByPersonId(Integer personId) {
//        try (Session session = sessionFactory.openSession()) {
//            String hql = "FROM SpendingSection ss WHERE ss.personId = :personId";
//
//            Query<SpendingSection> query = session.createQuery(hql, SpendingSection.class)
//                    .setParameter("personId", personId);
//
//            return query.list();
//        }
//    }
//
//    @Override
//    public List<SpendingSection> getActiveSpendingSectionsByPersonId(Integer personId) {
//        List<SpendingSection> sectionList;
//        try (Session session = sessionFactory.openSession()) {
//            String hql = "FROM SpendingSection ss where ss.personId = :personId AND ss.isAdded IS TRUE";
//
//            Query<SpendingSection> query = session.createQuery(hql, SpendingSection.class)
//                    .setParameter("personId", personId);
//
//            sectionList = query.list();
//        }
//        return sectionList;
//    }
//
//    @Override
//    public List<SpendingSection> getActiveSpendingSectionsByLogin(String login) {
//        List<SpendingSection> sectionList;
//        try (Session session = sessionFactory.openSession()) {
//            // TODO: 29.08.2018 finish me
//            String hql = "FROM SpendingSection ss where ss.personId = :personId AND ss.isAdded IS TRUE";
//
//            Query<SpendingSection> query = session.createQuery(hql, SpendingSection.class)
//                    .setParameter("login", login);
//
//            sectionList = query.list();
//        }
//        return sectionList;
//    }
//
//    @Override
//    public List<Integer> getActiveSpendingSectionIdsByPersonId(Integer personId) {
//        List<Integer> idsList;
//        try (Session session = sessionFactory.openSession()) {
//            String hql = "SELECT ss.sectionId " +
//                    "FROM SpendingSection ss " +
//                    "where ss.personId = :personId " +
//                    "AND ss.isAdded IS TRUE " +
//                    "ORDER BY sectionId ASC";
//
//            Query<Integer> query = session.createQuery(hql, Integer.class)
//                    .setParameter("personId", personId);
//            idsList = query.list();
//        }
//        return idsList;
//    }
//}
