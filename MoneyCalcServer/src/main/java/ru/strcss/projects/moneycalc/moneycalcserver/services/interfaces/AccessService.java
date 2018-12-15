package ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces;

import ru.strcss.projects.moneycalc.moneycalcdto.entities.Access;

public interface AccessService {
    Access getAccess(String login);
}
