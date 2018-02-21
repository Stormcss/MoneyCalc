package ru.strcss.projects.moneycalcserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.strcss.projects.moneycalc.dto.crudcontainers.AbstractContainer;
import ru.strcss.projects.moneycalcserver.mongo.PersonRepository;

public abstract class AbstractController {
    @Autowired
    public PersonRepository repository;

    @Autowired
    MongoOperations mongoOperations;

    final String REGISTER_SUCCESSFUL = "Person successfully registered";
    final String NO_PERSON_EXIST = "Person does not exist!";

    final String SETTINGS_UPDATED = "Settings successfully updated";
    final String SETTINGS_RETURNED = "Settings successfully returned";

    final String SAVE_IDENTIFICATIONS = "Identifications successfully saved";
    final String IDENTIFICATIONS_RETURNED = "Identifications successfully returned";
    final String IDENTIFICATIONS_SAVING_ERROR = "Identifications were not updated!";

    final String TRANSACTION_SAVING_ERROR = "ERROR has occurred while saving Transaction";
    final String TRANSACTION_SAVED = "TRANSACTION successfully saved";
    final String TRANSACTION_DELETED = "TRANSACTION successfully deleted";
    final String TRANSACTION_UPDATED = "TRANSACTION successfully updated";
    final String TRANSACTIONS_RETURNED = "TRANSACTIONS successfully returned";

    final String STATISTICS_RETURNED = "Statistics successfully returned";

    boolean isPersonExist(AbstractContainer container){
        return repository.existsByAccess_Login(container.getLogin());
    }

    boolean isPersonExist(String login){
        return repository.existsByAccess_Login(login);
    }
}
