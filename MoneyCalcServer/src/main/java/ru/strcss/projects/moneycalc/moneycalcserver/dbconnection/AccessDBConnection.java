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
        return repository.findAccessByAccess_Login(login).getAccess();
    }
}
