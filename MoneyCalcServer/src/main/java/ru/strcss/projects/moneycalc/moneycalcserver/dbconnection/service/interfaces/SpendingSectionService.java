package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces;

import ru.strcss.projects.moneycalc.entities.SpendingSection;
import ru.strcss.projects.moneycalc.moneycalcserver.dto.ResultContainer;

import java.util.List;

public interface SpendingSectionService {

    List<SpendingSection> getSpendingSections(String login, boolean withNonAdded,
                                              boolean withRemoved, boolean withRemovedOnly);

    ResultContainer deleteSpendingSection(String login, Integer sectionId);

    Integer getSectionIdByInnerId(Integer personId, Integer innerSectionId);

    Boolean isSpendingSectionIdExists(Integer personId, Integer sectionId);

    boolean isSpendingSectionNameNew(String login, String name);

//    int getMaxSpendingSectionId(Integer personId);

    void addSpendingSection(String login, SpendingSection section);

    boolean updateSpendingSection(SpendingSection section);


    SpendingSection getSpendingSectionById(Integer sectionId);

    List<SpendingSection> getSpendingSectionsByLogin(String login, boolean withNonAdded,
                                                     boolean withRemoved, boolean withRemovedOnly);

    List<SpendingSection> getSpendingSectionsByPersonId(Integer personId);
}
