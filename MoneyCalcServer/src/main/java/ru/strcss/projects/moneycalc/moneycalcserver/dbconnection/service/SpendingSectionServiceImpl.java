package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.SpendingSectionDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.SpendingSectionService;

import java.util.List;

@Service
public class SpendingSectionServiceImpl implements SpendingSectionService {

    private SpendingSectionDao spendingSectionDao;

    public SpendingSectionServiceImpl(SpendingSectionDao spendingSectionDao) {
        this.spendingSectionDao = spendingSectionDao;
    }

    @Override
    public Integer getSectionIdByName(Integer personId, String sectionName) {
        return spendingSectionDao.getSectionIdByName(personId, sectionName);
    }

    @Override
    public Integer getSectionIdById(Integer personId, Integer innerSectionId) {
        return spendingSectionDao.getSectionIdById(personId, innerSectionId);
    }

    @Override
    public Boolean isSpendingSectionIdExists(Integer personId, Integer sectionId) {
        return spendingSectionDao.isSpendingSectionIdExists(personId, sectionId);
    }

    @Override
    @Transactional
    public boolean isSpendingSectionNameNew(Integer personId, String name) {
        return spendingSectionDao.isSpendingSectionNameNew(personId, name);
    }

    @Override
    @Transactional
    public int getMaxSpendingSectionId(Integer personId) {
        return spendingSectionDao.getMaxSpendingSectionId(personId);
    }

    @Override
    @Transactional
    public Integer addSpendingSection(Integer personId, SpendingSection section) {
        return spendingSectionDao.addSpendingSection(personId, section);
    }

    @Override
    @Transactional
    public boolean updateSpendingSection(SpendingSection section) {
        return spendingSectionDao.updateSpendingSection(section);
    }

    @Override
    @Transactional
    public boolean deleteSpendingSection(SpendingSection section) {
        return spendingSectionDao.deleteSpendingSection(section);
    }

    @Override
    @Transactional
    public SpendingSection getSpendingSectionById(Integer sectionId) {
        return spendingSectionDao.getSpendingSectionById(sectionId);
    }

    @Override
    @Transactional
    public List<SpendingSection> getSpendingSectionsByPersonId(Integer personId) {
        return spendingSectionDao.getSpendingSectionsByPersonId(personId);
    }

    @Override
    public List<SpendingSection> getSpendingSectionsByLogin(String login) {
        return spendingSectionDao.getSpendingSectionsByLogin(login);
    }
}
