package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.service.interfaces;

import ru.strcss.projects.moneycalc.dto.Credentials;
import ru.strcss.projects.moneycalc.entities.Person;

public interface RegisterService {
    Person registerUser(Credentials credentials);

    boolean isUserExistsByLogin(String login);

    boolean isUserExistsByEmail(String email);
}
