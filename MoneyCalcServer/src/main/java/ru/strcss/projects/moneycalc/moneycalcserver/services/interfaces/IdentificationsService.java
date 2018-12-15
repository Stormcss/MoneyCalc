package ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces;

import ru.strcss.projects.moneycalc.moneycalcdto.entities.Identifications;

public interface IdentificationsService {
    Identifications getIdentifications(String login);

    boolean updateIdentifications(String login, Identifications identifications);
}
