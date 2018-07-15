package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces;

import ru.strcss.projects.moneycalc.enitities.SpendingSection;

import java.util.List;

public interface SpendingSectionService {
    Integer getSectionIdByName(Integer personId, String sectionName);

    Integer getSectionIdById(Integer personId, Integer innerSectionId);

    Boolean isSpendingSectionIdExists(Integer personId, Integer sectionId);

    boolean isSpendingSectionNameNew(Integer personId, String name);

    int getMaxSpendingSectionId(Integer personId);

    Integer addSpendingSection(Integer personId, SpendingSection section);

    boolean updateSpendingSection(SpendingSection section);

    boolean deleteSpendingSection(SpendingSection section);

    SpendingSection getSpendingSectionById(Integer sectionId);

    List<SpendingSection> getSpendingSectionsByLogin(String login);

    List<SpendingSection> getSpendingSectionsByPersonId(Integer personId);
}
