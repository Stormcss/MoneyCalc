package ru.strcss.projects.moneycalc.moneycalcserver.dbconnection;

import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.enitities.Access;
import ru.strcss.projects.moneycalc.moneycalcserver.mongo.PersonRepository;

@Component
public class AccessDBConnection {

    private PersonRepository repository;

    public AccessDBConnection(PersonRepository repository) {
        this.repository = repository;
    }

    public Access getAccess(String login) {
        Access access = repository.findAccessByAccess_Login(login).getAccess();
        access.setPassword(null);
        return access;
    }
}
