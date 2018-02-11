package ru.strcss.projects.moneycalcserver.dbconnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.strcss.projects.moneycalc.enitities.Person;
import ru.strcss.projects.moneycalcserver.mongo.PersonRepository;

@Component
public class SettingsDBConnection {

    private PersonRepository repository;

    @Autowired
    public SettingsDBConnection(PersonRepository repository) {
        this.repository = repository;
    }

    public Person getSettings(String login) {
        login = login.replace("\"", "");
        return repository.findSettingsByAccess_Login(login);
    }
}
