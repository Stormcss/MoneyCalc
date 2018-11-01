package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces;

import ru.strcss.projects.moneycalc.entities.Access;
import ru.strcss.projects.moneycalc.entities.Identifications;
import ru.strcss.projects.moneycalc.entities.Person;
import ru.strcss.projects.moneycalc.entities.Settings;

public interface RegisterService {
    Person registerUser(Access access, Identifications identifications, Settings settings);

    boolean isPersonExistsByLogin(String login);

    boolean isPersonExistsByEmail(String email);
}
