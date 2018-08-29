package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces;

import ru.strcss.projects.moneycalc.enitities.SpendingSection;

import java.util.List;

public interface SpendingSectionDao {
    Integer getSectionIdByName(Integer personId, String sectionName);

    Integer getSectionIdByInnerId(Integer personId, Integer innerSectionId);

    Boolean isSpendingSectionIdExists(Integer personId, Integer sectionId);

    boolean isSpendingSectionNameNew(Integer personId, String name);

    int getMaxSpendingSectionId(Integer personId);

    Integer addSpendingSection(Integer personId, SpendingSection section);

    boolean updateSpendingSection(SpendingSection section);

    boolean deleteSpendingSectionByName(String login, String sectionName);

    boolean deleteSpendingSectionByInnerId(String login, Integer innerId);

    SpendingSection getSpendingSectionById(Integer sectionId);

    List<SpendingSection> getSpendingSectionsByLogin(String login);

    List<SpendingSection> getSpendingSectionsByPersonId(Integer personId);

    List<SpendingSection> getActiveSpendingSectionsByLogin(String login);

    List<SpendingSection> getActiveSpendingSectionsByPersonId(Integer personId);

    List<Integer> getActiveSpendingSectionIdsByPersonId(Integer personId);
}
