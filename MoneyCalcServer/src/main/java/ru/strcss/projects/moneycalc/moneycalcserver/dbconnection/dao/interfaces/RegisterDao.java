package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces;

import ru.strcss.projects.moneycalc.entities.Access;
import ru.strcss.projects.moneycalc.entities.Identifications;
import ru.strcss.projects.moneycalc.entities.Person;
import ru.strcss.projects.moneycalc.entities.Settings;

public interface RegisterDao {
    Person registerPerson(Access access, Identifications identifications, Settings settings);

    boolean isPersonExistsByLogin(String login);

    boolean isPersonExistsByEmail(String email);
}
