package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces;

import ru.strcss.projects.moneycalc.enitities.Identifications;

public interface IdentificationsService {
    int saveIdentifications(Identifications identifications);

    Identifications updateIdentifications(Identifications identifications);

    Identifications getIdentificationsById(Integer id);
}
