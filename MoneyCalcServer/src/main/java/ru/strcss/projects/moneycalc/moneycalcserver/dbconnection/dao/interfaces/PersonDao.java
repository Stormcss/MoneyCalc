package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces;

public interface PersonDao {
    Integer getPersonIdByLogin(String login);

    Integer getSettingsIdByPersonId(Integer personId);

    Integer getAccessIdByPersonId(Integer personId);

    Integer getIdentificationsIdByPersonId(Integer personId);
}
