//package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao;
//
//import lombok.Setter;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.query.Query;
//import org.springframework.stereotype.Repository;
//import ru.strcss.projects.moneycalc.entities.Settings;
//import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.SettingsDao;
//
//@Repository
//public class SettingsDaoImpl implements SettingsDao {
//
//    @Setter
//    private SessionFactory sessionFactory;
//
//
//    public SettingsDaoImpl(SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//
//    @Override
//    public int saveSettings(Settings settings) {
//        Session session = sessionFactory.openSession();
//        Integer savedSettingsId = (Integer) session.save(settings);
//        session.close();
//        return savedSettingsId;
//    }
//
//    @Override
//    public Settings updateSettings(Settings settings) {
//        Session session = sessionFactory.openSession();
//
//        session.beginTransaction();
//        Settings updatedSettings = (Settings) session.merge(settings);
//        session.getTransaction().commit();
//        session.close();
//
//        return updatedSettings;
//    }
//
//    public Settings getSettingsById(Integer id) {
//        Session session = sessionFactory.openSession();
//
//        String hql = "FROM Settings s WHERE s.id = :settingsId";
//        Query query = session.createQuery(hql)
//                .setParameter("settingsId", id);
//        Settings settings = (Settings) query.getSingleResult();
//
//        session.close();
//        return settings;
//    }
//
//
//}