package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;

import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.IdentificationsDao;

@Repository
public class IdentificationsDaoImpl implements IdentificationsDao {
    @Setter
    private SessionFactory sessionFactory;

    @Autowired
    public IdentificationsDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public int saveIdentifications(Identifications identifications) {
        Session session = sessionFactory.openSession();
        Integer saveIdentificationsId = (Integer) session.save(identifications);
        session.close();
        return saveIdentificationsId;
    }

    @Override
    public Identifications updateIdentifications(Identifications identifications) {
        Session session = sessionFactory.openSession();

        session.beginTransaction();
        Identifications updatedIdentifications = (Identifications) session.merge(identifications);
        session.getTransaction().commit();
        session.close();

        return updatedIdentifications;
    }

    @Override
    public Identifications getIdentificationsById(Integer id) {
        Session session = sessionFactory.openSession();

        String hql = "FROM Identifications i WHERE i.id = :identificationsId";

        //delme
        Query query1 = session.createQuery(hql);
        Query query2 = query1.setParameter("identificationsId", id);

        //delme
        Query query = session.createQuery(hql)
                .setParameter("identificationsId", id);
        Identifications identifications = (Identifications) query.getSingleResult();

        session.close();
        return identifications;
    }
}
