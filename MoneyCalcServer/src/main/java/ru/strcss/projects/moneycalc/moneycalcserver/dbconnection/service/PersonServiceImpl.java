//package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service;
//
//import org.springframework.stereotype.Service;
//import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.PersonDao;
//import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.PersonService;
//
//@Service
//public class PersonServiceImpl implements PersonService {
//
//    private PersonDao personDao;
//
//    public PersonServiceImpl(PersonDao personDao) {
//        this.personDao = personDao;
//    }
//
//
//    @Override
//    public Integer getPersonIdByLogin(String login) {
//        return personDao.getPersonIdByLogin(login);
//    }
//
//    @Override
//    public Integer getSettingsIdByPersonId(Integer personId) {
//        return personDao.getSettingsIdByPersonId(personId);
//    }
//
//    @Override
//    public Integer getAccessIdByPersonId(Integer personId) {
//        return personDao.getAccessIdByPersonId(personId);
//    }
//
//    @Override
//    public Integer getIdentificationsIdByPersonId(Integer personId) {
//        return personDao.getIdentificationsIdByPersonId(personId);
//    }
//}
