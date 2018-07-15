package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.AccessDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.AccessService;

@Service
public class AccessServiceImpl implements AccessService {

    private AccessDao accessDao;

    public AccessServiceImpl(AccessDao accessDao) {
        this.accessDao = accessDao;
    }

    @Override
    @Transactional
    public Access getAccessById(String id) {
        return accessDao.getAccessById(id);
    }

    @Override
    public Access getAccessByLogin(String login) {
        return accessDao.getAccessByLogin(login);
    }

    @Override
    public int saveAccess(Access access) {
        return accessDao.saveAccess(access);
    }
}
