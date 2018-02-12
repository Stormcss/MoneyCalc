package ru.strcss.projects.moneycalcserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.strcss.projects.moneycalcserver.mongo.PersonRepository;

public abstract class AbstractController {
    @Autowired
    public PersonRepository repository;

    @Autowired
    MongoOperations mongoOperations;

    final String REGISTER_SUCCESSFUL = "Person successfully registered";
    final String NO_PERSON_EXIST = "Required Person does not exist!";

    final String SAVE_SETTINGS = "Settings successfully saved";
    final String RETURN_SETTINGS = "Settings successfully returned";

    public final String SAVE_IDENTIFICATIONS = "Identifications successfully saved";
    final String RETURN_IDENTIFICATIONS = "Identifications successfully returned";

    final String TRANSACTION_SAVING_ERROR = "ERROR has occurred while saved Transaction";
    final String TRANSACTION_SAVED = "TRANSACTION successfully saved";
    final String TRANSACTION_DELETED = "TRANSACTION successfully deleted";
    final String TRANSACTION_UPDATED = "TRANSACTION successfully updated";
    final String TRANSACTIONS_RETURNED = "TRANSACTIONS successfully returned";

    final String FIND_SUCCESSFUL = "Found successfully";
}
