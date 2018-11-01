package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces;

import ru.strcss.projects.moneycalc.entities.Access;

public interface AccessDao {

    Access getAccessById(String id);

    Access getAccessByLogin(String login);

    int saveAccess(Access access);
}
