package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.dao.interfaces;

import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.Settings;

public interface RegisterDao {
    Person registerPerson(Access access, Identifications identifications, Settings settings);

    boolean isPersonExistsByLogin(String login);

    boolean isPersonExistsByEmail(String email);
}
