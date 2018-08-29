package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces;

import ru.strcss.projects.moneycalc.dto.crudcontainers.settings.SpendingSectionDeleteContainer;
import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.dto.ResultContainer;

import java.util.List;

public interface SpendingSectionService {
    Integer getSectionIdByName(Integer personId, String sectionName);

    Integer getSectionIdByInnerId(Integer personId, Integer innerSectionId);

    Boolean isSpendingSectionIdExists(Integer personId, Integer sectionId);

    boolean isSpendingSectionNameNew(Integer personId, String name);

    int getMaxSpendingSectionId(Integer personId);

    Integer addSpendingSection(Integer personId, SpendingSection section);

    boolean updateSpendingSection(SpendingSection section);

//    boolean deleteSpendingSectionByName(SpendingSection section);

    ResultContainer deleteSpendingSection(String login, SpendingSectionDeleteContainer deleteContainer);

    SpendingSection getSpendingSectionById(Integer sectionId);

    List<SpendingSection> getSpendingSectionsByLogin(String login);

    List<SpendingSection> getSpendingSectionsByPersonId(Integer personId);
}
