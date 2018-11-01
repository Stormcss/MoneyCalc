//package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service;
//
//import org.springframework.stereotype.Service;
//import ru.strcss.projects.moneycalc.entities.Access;
//import ru.strcss.projects.moneycalc.entities.Identifications;
//import ru.strcss.projects.moneycalc.entities.Person;
//import ru.strcss.projects.moneycalc.entities.Settings;
//import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.RegisterDao;
//import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.RegisterService;
//
//@Service
//public class RegisterServiceImpl implements RegisterService {
//
//    private RegisterDao registerDao;
//
//    public RegisterServiceImpl(RegisterDao registerDao) {
//        this.registerDao = registerDao;
//    }
//
//    @Override
//    public Person registerUser(Access access, Identifications identifications, Settings settings) {
//        return registerDao.registerPerson(access, identifications, settings);
//    }
//
//    @Override
//    public boolean isPersonExistsByLogin(String login) {
//        return registerDao.isPersonExistsByLogin(login);
//    }
//
//    @Override
//    public boolean isPersonExistsByEmail(String email) {
//        return registerDao.isPersonExistsByEmail(email);
//    }
//}
