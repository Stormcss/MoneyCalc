//package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces;
//
//import ru.strcss.projects.moneycalc.entities.SpendingSection;
//import ru.strcss.projects.moneycalc.moneycalcserver.dto.ResultContainer;
//
//import java.util.List;
//
//public interface SpendingSectionDao {
//    Integer getSectionIdByName(Integer userId, String sectionName);
//
//    Integer getSectionIdByInnerId(Integer userId, Integer innerSectionId);
//
//    Boolean isSpendingSectionIdExists(Integer userId, Integer sectionId);
//
//    boolean isSpendingSectionNameNew(Integer userId, String name);
//
//    int getMaxSpendingSectionId(Integer userId);
//
//    Integer addSpendingSection(Integer userId, SpendingSection section);
//
//    boolean updateSpendingSection(SpendingSection section);
//
//    ResultContainer deleteSpendingSectionByInnerId(String login, Integer innerId);
//
//    SpendingSection getSpendingSectionById(Integer sectionId);
//
//    List<SpendingSection> getSpendingSectionsByLogin(String login, boolean withNonAdded,
//                                                     boolean withRemoved, boolean withRemovedOnly);
//
//    List<SpendingSection> getSpendingSectionsByPersonId(Integer userId);
//
//    List<SpendingSection> getActiveSpendingSectionsByLogin(String login);
//
//    List<SpendingSection> getActiveSpendingSectionsByPersonId(Integer userId);
//
//    List<Integer> getActiveSpendingSectionIdsByPersonId(Integer userId);
//}
