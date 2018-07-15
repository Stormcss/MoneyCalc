package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces;

import ru.strcss.projects.moneycalc.enitities.Access;

public interface AccessService {
    Access getAccessById(String id);

    Access getAccessByLogin(String login);

    int saveAccess(Access access);
}
