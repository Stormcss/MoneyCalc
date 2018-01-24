package ru.strcss.projects.moneycalcserver.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.strcss.projects.moneycalcserver.mongo.PersonRepository;

public abstract class AbstractController {
    @Autowired
    public PersonRepository repository;

    @Autowired
    MongoOperations mongoOperations;

    public final String REGISTER_SUCCESSFUL = "Person successfully registered";
    public final String NO_PERSON_EXIST = "Required Person does not exist!";

    public final String SAVE_SETTINGS = "Settings successfully saved";
    public final String RETURN_SETTINGS = "Settings successfully returned";

    public final String SAVE_IDENTIFICATIONS = "Identifications successfully saved";
    public final String RETURN_IDENTIFICATIONS = "Identifications successfully returned";

    public final String NO_TRANSACTIONS = "TRANSACTIONS successfully returned";
    public final String RETURN_TRANSACTIONS = "TRANSACTIONS successfully returned";

    public final String FIND_SUCCESSFUL = "Found successfully";
}
