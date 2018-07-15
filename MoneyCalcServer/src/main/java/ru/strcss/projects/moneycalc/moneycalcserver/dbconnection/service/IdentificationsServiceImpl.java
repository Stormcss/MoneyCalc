package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.IdentificationsDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.IdentificationsService;

@Service
public class IdentificationsServiceImpl implements IdentificationsService {

    private IdentificationsDao identificationsDao;

    public IdentificationsServiceImpl(IdentificationsDao identificationsDao) {
        this.identificationsDao = identificationsDao;
    }

    @Override
    public Identifications updateIdentifications(Identifications identifications) {
        return identificationsDao.updateIdentifications(identifications);
    }

    @Override
    @Transactional
    public int saveIdentifications(Identifications identifications) {
        return identificationsDao.saveIdentifications(identifications);
    }

    @Override
    @Transactional
    public Identifications getIdentificationsById(Integer id) {
        return identificationsDao.getIdentificationsById(id);
    }
}
