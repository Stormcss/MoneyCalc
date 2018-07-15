package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces;

import ru.strcss.projects.moneycalc.enitities.Identifications;

public interface IdentificationsDao {

    int saveIdentifications(Identifications identifications);

    Identifications updateIdentifications(Identifications identifications);

    Identifications getIdentificationsById(Integer id);
}
