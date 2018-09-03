package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.SpendingSectionDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.SpendingSectionService;
import ru.strcss.projects.moneycalc.moneycalcserver.dto.ResultContainer;

import java.util.List;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_NOT_DELETED;

@Slf4j
@Service
public class SpendingSectionServiceImpl implements SpendingSectionService {

    private SpendingSectionDao spendingSectionDao;

    public SpendingSectionServiceImpl(SpendingSectionDao spendingSectionDao) {
        this.spendingSectionDao = spendingSectionDao;
    }

    @Override
    public Integer getSectionIdByInnerId(Integer personId, Integer innerSectionId) {
        return spendingSectionDao.getSectionIdByInnerId(personId, innerSectionId);
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
    public ResultContainer deleteSpendingSection(String login, Integer sectionId) {

        ResultContainer deletionResult = spendingSectionDao.deleteSpendingSectionByInnerId(login, sectionId);

        if (deletionResult.isSuccess())
            return deletionResult;
        else {
            if (deletionResult.getErrorMessage() == null)
                deletionResult.setErrorMessage(SPENDING_SECTION_NOT_DELETED);
            log.error("SpendingSection with id: '' was not deleted, having searchType: '{}' for login: '{}'", sectionId, login);
        }
        return deletionResult;
    }

    @Override
    public SpendingSection getSpendingSectionById(Integer sectionId) {
        return spendingSectionDao.getSpendingSectionById(sectionId);
    }

    @Override
    @Transactional
    public List<SpendingSection> getSpendingSectionsByPersonId(Integer personId) {
        return spendingSectionDao.getSpendingSectionsByPersonId(personId);
    }

    @Override
    public List<SpendingSection> getSpendingSectionsByLogin(String login, boolean withNonAdded,
                                                            boolean withRemoved, boolean withRemovedOnly) {
        return spendingSectionDao.getSpendingSectionsByLogin(login, withNonAdded, withRemoved, withRemovedOnly);
    }
}
