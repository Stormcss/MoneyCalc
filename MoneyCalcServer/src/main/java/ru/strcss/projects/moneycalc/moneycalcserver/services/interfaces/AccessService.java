package ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces;

import ru.strcss.projects.moneycalc.entities.Access;

public interface AccessService {
    Access getAccess(String login);
}
