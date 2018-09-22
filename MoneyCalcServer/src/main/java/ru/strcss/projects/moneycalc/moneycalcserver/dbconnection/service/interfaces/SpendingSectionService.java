package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces;

import ru.strcss.projects.moneycalc.enitities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.dto.ResultContainer;

import java.util.List;

public interface SpendingSectionService {

    Integer getSectionIdByInnerId(Integer personId, Integer innerSectionId);

    Boolean isSpendingSectionIdExists(Integer personId, Integer sectionId);

    boolean isSpendingSectionNameNew(Integer personId, String name);

    int getMaxSpendingSectionId(Integer personId);

    Integer addSpendingSection(Integer personId, SpendingSection section);

    boolean updateSpendingSection(SpendingSection section);

    ResultContainer deleteSpendingSection(String login, Integer sectionId);

    SpendingSection getSpendingSectionById(Integer sectionId);

    List<SpendingSection> getSpendingSectionsByLogin(String login, boolean withNonAdded,
                                                     boolean withRemoved, boolean withRemovedOnly);

    List<SpendingSection> getSpendingSectionsByPersonId(Integer personId);
}
