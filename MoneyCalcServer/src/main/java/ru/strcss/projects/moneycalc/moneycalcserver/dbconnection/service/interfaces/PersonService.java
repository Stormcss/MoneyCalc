package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces;

public interface PersonService {
    Integer getPersonIdByLogin(String login);

    Integer getSettingsIdByPersonId(Integer personId);

    Integer getAccessIdByPersonId(Integer personId);

    Integer getIdentificationsIdByPersonId(Integer personId);
}
