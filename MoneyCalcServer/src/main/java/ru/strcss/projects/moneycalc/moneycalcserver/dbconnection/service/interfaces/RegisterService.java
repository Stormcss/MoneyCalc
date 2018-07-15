package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces;

import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.enitities.Identifications;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalc.enitities.Settings;

public interface RegisterService {
    Person registerUser(Access access, Identifications identifications, Settings settings);

    boolean isPersonExistsByLogin(String login);

    boolean isPersonExistsByEmail(String email);
}
