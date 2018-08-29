package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.strcss.projects.moneycalc.dto.crudcontainers.SpendingSectionSearchType;
import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces.SpendingSectionDao;
import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces.SpendingSectionService;
import ru.strcss.projects.moneycalc.moneycalcserver.dto.ResultContainer;

import java.util.List;

import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerMessages.SPENDING_SECTION_NOT_DELETED;
import static ru.strcss.projects.moneycalc.moneycalcserver.controllers.utils.ControllerUtils.fillLog;

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
    public ResultContainer deleteSpendingSection(String login, SpendingSectionDeleteContainer deleteContainer) {
        boolean isDeletionSuccessful;
        ResultContainer resultContainer;

        if (deleteContainer.getSearchType().equals(SpendingSectionSearchType.BY_NAME)) {
            isDeletionSuccessful = spendingSectionDao.deleteSpendingSectionByName(login, deleteContainer.getIdOrName());
        } else {
            isDeletionSuccessful = spendingSectionDao.deleteSpendingSectionByInnerId(login, Integer.valueOf(deleteContainer.getIdOrName()));
        }
        if (isDeletionSuccessful)
            resultContainer = new ResultContainer(true);
        else {
            resultContainer = new ResultContainer(false);
            resultContainer.setErrorMessage(SPENDING_SECTION_NOT_DELETED);
            resultContainer.setFullErrorMessage(fillLog("SpendingSection with searchType: '%s' and query: '%s' for login: '%s' was not deleted",
                    deleteContainer.getSearchType().toString(), deleteContainer.getIdOrName(), login));
        }
        return resultContainer;
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
    public List<SpendingSection> getSpendingSectionsByLogin(String login) {
        return spendingSectionDao.getSpendingSectionsByLogin(login);
    }
}
