package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.utils;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Slf4j
public class DaoUtils {

    public static <E> boolean updateEntity(SessionFactory sessionFactory, E entity) {
        Session session = sessionFactory.openSession();

        boolean isError = false;
        try {
            session.beginTransaction();
            session.update(entity);
        } catch (Exception ex) {
            ex.printStackTrace();
            isError = true;
        } finally {
            if (!isError)
                session.getTransaction().commit();
            else
                session.getTransaction().rollback();
            session.close();
        }
        return !isError;
    }

    public static <T> boolean isFieldExists(SessionFactory sessionFactory, Class<T> eClass, String fieldName, String value) {
        Session session = sessionFactory.openSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(eClass);

        Root<T> root = query.from(eClass);

        query.select(root).where(builder.equal(root.get(fieldName), value));

        T result;
        try {
            result = session.createQuery(query).getSingleResult();
        } catch (NoResultException nre) {
            return false;
        }
        return result != null;
    }
}
