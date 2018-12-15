package ru.strcss.projects.moneycalc.moneycalcserver.services.interfaces;

import ru.strcss.projects.moneycalc.moneycalcdto.dto.Credentials;
import ru.strcss.projects.moneycalc.moneycalcdto.entities.Person;

public interface RegisterService {
    Person registerUser(Credentials credentials);

    boolean isUserExistsByLogin(String login);

    boolean isUserExistsByEmail(String email);
}
